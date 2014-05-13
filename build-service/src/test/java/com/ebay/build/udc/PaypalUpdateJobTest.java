package com.ebay.build.udc;

import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.utils.SpringConfig;

public class PaypalUpdateJobTest {
	private UsageDataDaoJDBCImpl dao;
	private String udcTable;
	private UDCUpdateJob job;
	private static String SELECT = "select count(*) from {0} where ideVersion=''x.x.x'' and category is not null";
	
	private static String DELETE = "delete from {0} where ideVersion=''x.x.x''";
	private static String INSERT =  "insert into {0}(ideType, ideVersion, host, userName, kind, what, description, accesstime, exception) "+
			"values (''ride'', ''x.x.x'', ''d-shc-00355773'', ''qingqliu'', ''buildevent'', ''runMavenBuild'' ,''for test'',  to_date(''{1}'', ''DD-MM-YY hh24:mi:ss'') "+ 
			", ''[ERROR] No goals have been specified for this build . You must specify a valid lifecycle phase or a goal in the format ...'')";
	@Before
	public void setUp() throws Exception {
		dao = (UsageDataDaoJDBCImpl)SpringConfig.getBean("paypalDao");
		udcTable = dao.getUdcJdbc().getUdcTable();
		job = (UDCUpdateJob)SpringConfig.getBean("paypalUpdateJob");
	}
	@Test
	public void testRun() throws JAXBException {
				
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(System.currentTimeMillis());
		ca.add(Calendar.DATE, -1);
		String strDate =new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(ca.getTime());
		String sql = MessageFormat.format(INSERT, udcTable, strDate);
		dao.getJdbcTemplate().execute(sql);
		
		sql = MessageFormat.format(SELECT, udcTable);
		int size = dao.getJdbcTemplate().queryForInt(sql);
		
		
		job.setFromDate(ca.getTime());
		job.run();
		
		int size2 = dao.getJdbcTemplate().queryForInt(sql);
		fail("Not yet implemented");
				
	}
	@After
	public void tearDown() throws Exception {
		String sql = MessageFormat.format(DELETE, udcTable);
		dao.getJdbcTemplate().execute(sql);	
	}
}
