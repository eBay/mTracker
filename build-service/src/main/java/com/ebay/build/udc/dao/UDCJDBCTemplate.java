package com.ebay.build.udc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ebay.build.udc.UsageDataInfo;
import com.ebay.build.utils.ServiceConfig;

public class UDCJDBCTemplate {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    private String udcTable;
    
    private static final String STMT_INSERT = "insert into {0} "
            + "(IDEType, IDEVersion, SessionId, Host, UserName, Kind, What, Description, BundleId, BundleVersion, AccessTime, Duration, Size_, Quantity, Exception, Properties) values "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
			List<String> list = jdbcTemplateObject.queryForList(
					STMT_QUERY_SESSION, String.class);
			List<String> duplicate = new ArrayList<String>();
			for (String key : props.keySet()) {
				if (list.contains(key)) {
					duplicate.add(key);
					LoggerFactory.getLogger(getClass()).warn(
							"Skipping insert to DB as session id already exists in DB, id:"
									+ key);
				}
			}

			for (String key : duplicate) {
				props.remove(key);

			}
		}

        return props;

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
}
