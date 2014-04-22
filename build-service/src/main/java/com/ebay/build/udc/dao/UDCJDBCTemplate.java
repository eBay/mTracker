package com.ebay.build.udc.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.ebay.build.udc.UsageDataInfo;
import com.ebay.build.utils.ServiceConfig;

public class UDCJDBCTemplate {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    private String udcTable;
    
    private static final String STMT_INSERT = "insert into {0} "
            + "(IDEType, IDEVersion, SessionId, Host, UserName, Kind, What, Description, BundleId, BundleVersion, AccessTime, Duration, Size_, Quantity, Exception_, Properties, category, errorcode) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String STMT_INSERT_SESSION = "insert into "
            + ServiceConfig.get("udc.table.session.name")
            + " (Id, Properties) values " + "(?, ?)";
    private static final String STMT_QUERY_SESSION = "select id from "
            + ServiceConfig.get("udc.table.session.name");
           
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    
 
    public void setUdcTable(String udcTable) {
		this.udcTable = udcTable;
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
				LoggerFactory.getLogger(getClass()).warn(
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

	public int[] create(final List<UsageDataInfo> infos) {
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
        

		String sql = MessageFormat.format(STMT_INSERT, udcTable);
        return jdbcTemplateObject.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                UsageDataInfo info = infos.get(i);

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
                ps.setTimestamp(11, new Timestamp(info.getWhen()));
                ps.setInt(12, info.getDuration());
                ps.setInt(13, info.getSize());
                ps.setInt(14, info.getQuantity());
                ps.setString(15, info.getException());
                ps.setString(16, info.getProperties());
                if(info.getCategory() == null)
                	ps.setNull(17, java.sql.Types.VARCHAR);
                else
                	ps.setString(17, info.getCategory());
                if(info.getErrorCode() == null)
                	ps.setNull(18, java.sql.Types.VARCHAR);
                else
                    ps.setString(18, info.getErrorCode());
            }

            @Override
            public int getBatchSize() {
                return infos.size();
            }
        });

    }

	public List<UsageDataInfo> query(UsageDataInfo data) {
		//TODO not used now.
		return null;
		
	}
	
	public List<UsageDataInfo> queryUncategoriedErrorRecords(Date startDate, Date endDate){
		final String startStr =new SimpleDateFormat("dd-MMM-yy").format(startDate);
		
		final String endStr = new SimpleDateFormat("dd-MMM-yy").format(endDate);
		
//		String sqlQuery = "select ID, kind, what, exception_, category, ErrorCode from "+ 
//				udcTable + 
//			" where accesstime >= to_date(?, 'DD-Mon-YY') and accesstime < to_date(?, 'DD-Mon-YY') and idetype='RIDE' and exception_ is not null";
		
		String sqlQueryTemp = "select ID, kind, what, exception_, category, ErrorCode from "+ 
				udcTable + 
			" where accesstime >= to_date(?, 'DD-Mon-YY') and accesstime < to_date(?, 'DD-Mon-YY') and exception_ is not null and category is null";
		
		List<UsageDataInfo> ls = jdbcTemplateObject.query(sqlQueryTemp, new PreparedStatementSetter(){
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				System.out.println(startStr);
				System.out.println(endStr);
				ps.setString(1, startStr);
				ps.setString(2, endStr);
			}
			
		},
				new RowMapper<UsageDataInfo>(){
			@Override
			public UsageDataInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				UsageDataInfo record = new UsageDataInfo();  
		        record.setId(rs.getLong("id"));
		        record.setKind(rs.getString("kind"));
		        record.setWhat(rs.getString("what"));
		        Clob clobException = rs.getClob("exception_");
		        String strException;
				try {
					strException = clobToString(clobException);
				} catch (Exception e) {
					e.printStackTrace();
					strException = "Unknown";
				}
		        record.setException(strException);
		        record.setCategory(rs.getString("category"));
		        record.setErrorCode(rs.getString("ErrorCode"));
		        
		        return record;  
			}
		});
		return ls;
	}
	
	private String clobToString(Clob clob) throws SQLException, IOException{
        BufferedReader br = new BufferedReader(clob.getCharacterStream());
        String s = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (s != null) {
            sb.append(s);
            s = br.readLine();
        }
        return sb.toString();
	}
	/**
	 * update catetory and errorcode of usagedatainfo
	 */
	public void updateErrorInfo(final List<UsageDataInfo> ls){
		String sqlUpdate = "update " + udcTable +
				" set category = ? , ErrorCode= ? where id = ?"; 
		
		int result[] = jdbcTemplateObject.batchUpdate(sqlUpdate, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				UsageDataInfo info = ls.get(i);
				if(info.getCategory() == null)
					ps.setString(1, "null");
				else
					ps.setString(1, info.getCategory());
				if(info.getErrorCode() == null)
					ps.setString(2, "null");
				else
					ps.setString(2, info.getErrorCode());
				ps.setLong(3, info.getId());
			}

			@Override
			public int getBatchSize() {
				return ls.size();
			}
			
		});
		System.out.println(result);
	}
}
