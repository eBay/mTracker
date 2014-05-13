package com.ebay.build.udc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.ebay.build.profiler.filter.ErrorClassifier;
import com.ebay.build.udc.ErrorRowCallBackHandler;
import com.ebay.build.udc.UsageDataInfo;
import com.ebay.build.utils.ServiceConfig;

public class UDCJDBCTemplate {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    private JdbcTemplate updateJdbcTemplateObject;
    
    private String udcTable;
    
    private static final int batchSize = 1000;
    
    private static Logger logger = Logger.getLogger(UDCJDBCTemplate.class.getName());
    
    private static final String STMT_INSERT = "insert into {0} "
            + "(IDEType, IDEVersion, SessionId, Host, UserName, Kind, What, Description, BundleId, BundleVersion, AccessTime, Duration, Size_, Quantity, Exception, Properties, category, errorcode) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String STMT_INSERT_SESSION = "insert into "
            + ServiceConfig.get("udc.table.session.name")
            + " (Id, Properties) values " + "(?, ?)";
    private static final String STMT_QUERY_SESSION = "select id from "
            + ServiceConfig.get("udc.table.session.name");
           
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
        this.updateJdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    
 
    public void setUdcTable(String udcTable) {
		this.udcTable = udcTable;
	}

    public String getUdcTable(){
    	return udcTable;
    }
    
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplateObject;
	}

	private Map<String, String> extractSessionProperties(List<UsageDataInfo> infos) {
        Map<String, String> props = new HashMap<String, String>();
		for (UsageDataInfo usageDataInfo : infos) {
			if (StringUtils.isNotEmpty(usageDataInfo.getSessionId())
					&& !props.containsKey(usageDataInfo.getSessionId())
					&& !StringUtils.isEmpty(usageDataInfo
							.getSessionProperties())) {

				props.put(usageDataInfo.getSessionId(),
						usageDataInfo.getSessionProperties());

			}
		}

		if (props.size() > 0) {
			List<String> existIds = jdbcTemplateObject.queryForList(getQuerySessionSql(props.keySet()), String.class);
			for (String id : existIds) {
				logger.log(Level.WARNING,
						"Skipping insert to DB as session id already exists in DB, id:" + id);
				props.remove(id);

			}
		}

        return props;

    }

	private String getQuerySessionSql(Set<String> keySet) {
		StringBuffer sql= new StringBuffer(STMT_QUERY_SESSION +" where ");
		for (String id : keySet) {
			sql.append(" id='" + id + "' or");
		}
		return StringUtils.removeEnd(sql.toString(), "or");
	}

	private List<List<UsageDataInfo>> divideList(List<UsageDataInfo> infos){
		List<List<UsageDataInfo>> lsResult = new ArrayList<List<UsageDataInfo>>();
		int batchCount = infos.size()%batchSize==0?infos.size()/batchSize: infos.size()/batchSize + 1;
		int countIndex;
		for(countIndex=1;countIndex<=batchCount;countIndex++){
			int fromIndex = (countIndex-1)*batchSize;
			int toIndexTemp = countIndex*batchSize;
			int toIndex = toIndexTemp < infos.size()?toIndexTemp:infos.size();			
			List<UsageDataInfo> tempInfos = infos.subList(fromIndex, toIndex);
			lsResult.add(tempInfos);
		}
		return lsResult;
	}
	
	public int create(final List<UsageDataInfo> infos) {
		long startTime = System.currentTimeMillis();
		final Map<String, String> props = extractSessionProperties(infos);
        final String[] keys = (String[]) props.keySet().toArray(new String[0]);

        //insert session data
        if (keys.length > 0) {
            jdbcTemplateObject.batchUpdate(STMT_INSERT_SESSION,
                    new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {

                    ps.setString(1, keys[i]);
                    ps.setString(2, props.get(keys[i]));

                }

                @Override
                public int getBatchSize() {
                    return props.size();
                }
            });

        }
        
        logger.log(Level.INFO, "UDCJDBCTemplate: size of UsageDataInfo " + infos.size());
		String sql = MessageFormat.format(STMT_INSERT, udcTable);
		
		List<List<UsageDataInfo>> lsArInfos = divideList(infos);
		int dealtNum = 0;
		for(final List<UsageDataInfo> lsInfos: lsArInfos){
			try{
				int[] result = jdbcTemplateObject.batchUpdate(sql,
						new BatchPreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps, int i)
									throws SQLException {
								UsageDataInfo info = lsInfos.get(i);

								ps.setString(1, info.getIdeType());
								ps.setString(2, info.getIdeVersion());
								ps.setString(3, info.getSessionId());
								ps.setString(4, info.getHost());
								ps.setString(5, info.getUser());
								ps.setString(6, info.getKind());
								ps.setString(7, info.getWhat());
								ps.setString(8, info.getDescription());
								ps.setString(9, info.getBundleId());
								ps.setString(10, info.getBundleVersion());
								ps.setTimestamp(11,
										new Timestamp(info.getWhen()));
								ps.setInt(12, info.getDuration());
								ps.setInt(13, info.getSize());
								ps.setInt(14, info.getQuantity());
								ps.setString(15, info.getException());
								ps.setString(16, info.getProperties());
								if (info.getCategory() == null)
									ps.setNull(17, java.sql.Types.VARCHAR);
								else
									ps.setString(17, info.getCategory());
								if (info.getErrorCode() == null)
									ps.setNull(18, java.sql.Types.VARCHAR);
								else
									ps.setString(18, info.getErrorCode());
							}

							@Override
							public int getBatchSize() {
								return lsInfos.size();
							}
						});
				dealtNum+=result.length;
			} catch (Exception e){
				logger.log(Level.SEVERE, "Error occurred in one batch insert at " + (new Date()));
				e.printStackTrace();
			}
		}
		long duration = System.currentTimeMillis() - startTime;
		int iCompare = infos.size() - dealtNum;
		if(iCompare>0){
			logger.log(Level.SEVERE, iCompare + " records are failed to insert. ");
		}else{
			logger.log(Level.INFO, "Insert " + dealtNum + " records into DB. Use " + duration + " milliseconds. ");
		}
		return dealtNum;
    }

	public List<UsageDataInfo> query(UsageDataInfo data) {
		//TODO not used now.
		return null;
		
	}
	
	
	/**
	 * query and update uncategoried error records that occurred between startDate to endDate 
	 * @param startDate
	 * @param endDate
	 * @return update records' quantity
	 */
	public int queryAndUpdateUncategoriedErrorRecords(Date startDate, Date endDate, 
			ErrorClassifier errorClassifier) throws DataAccessException{
		final String startStr =new SimpleDateFormat("dd-MM-yy").format(startDate);
		
		final String endStr = new SimpleDateFormat("dd-MM-yy").format(endDate);
		
		String sqlQueryTemp = "select ID, kind, what, exception, category, ErrorCode from " 
				+ udcTable  
				+ " where accesstime >= to_date(?, 'DD-MM-YY') "
				+ " and accesstime < to_date(?, 'DD-MM-YY') "
				+ " and exception is not null "
				+ " and category is null";
		
		ErrorRowCallBackHandler handler = new ErrorRowCallBackHandler(errorClassifier){
			@Override
			protected int updateRecordsToDB(List<UsageDataInfo> ls) {
				return updateErrorInfoToDB(ls);
		}};
		jdbcTemplateObject.query(sqlQueryTemp, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, startStr);
				ps.setString(2, endStr);
			}
			
		}, handler);
		handler.flush();
		return handler.updateAmount;
	}
	
	/**
	 * update catetory and errorcode of usagedatainfo
	 */
	private int updateErrorInfoToDB(final List<UsageDataInfo> infos) {
		long time = System.currentTimeMillis();
		String sqlUpdate = "update " + udcTable
				+ " set category = ? , ErrorCode= ? where id = ?";

		List<List<UsageDataInfo>> lsArInfos = divideList(infos);
		int dealtNum=0;
		for(final List<UsageDataInfo> lsInfos: lsArInfos){
			try{
			int result[] = updateJdbcTemplateObject.batchUpdate(sqlUpdate,
					new BatchPreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps, int i)
								throws SQLException {
							UsageDataInfo info = lsInfos.get(i);
							if (info.getCategory() == null)
								ps.setNull(1, java.sql.Types.VARCHAR);
							else
								ps.setString(1, info.getCategory());
							if (info.getErrorCode() == null)
								ps.setNull(2, java.sql.Types.VARCHAR);
							else
								ps.setString(2, info.getErrorCode());
							ps.setLong(3, info.getId());
						}

						@Override
						public int getBatchSize() {
							return lsInfos.size();
						}

					});
			
			dealtNum += result.length;
			} catch(Exception e){
				logger.log(Level.SEVERE, "Error Occurred in one batch update at " + (new Date()));
				e.printStackTrace();
			}
		}
		long duration = System.currentTimeMillis() - time;
		int iCompare = infos.size() - dealtNum;
		if(iCompare>0)
		{
			logger.log(Level.SEVERE, iCompare + " records are failed to update.");
		}else{
			logger.log(Level.INFO, "Update " + dealtNum + " records to DB. Use " + duration + " milliseconds.");
		}
		return dealtNum;
	}
	
}
