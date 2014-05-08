package com.ebay.build.udc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;

public class UDCUpdateJobTest {
	private UsageDataDaoJDBCImpl dao;
	private static final String SELECT = "select count(*) from usagedata where ideVersion='x.x.x' and category is not null";
	
	private static final String DELETE_TEST_DATA = "delete from usagedata where ideVersion='x.x.x'";
	
	@Before
	public void setUp() throws Exception {
		dao = new UsageDataDaoJDBCImpl("");
	}
	@Test
	public void testRun() throws JAXBException {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(System.currentTimeMillis());
		ca.add(Calendar.DATE, -1);
		String strDate =new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(ca.getTime());
	
		String INSERT =  "insert into usagedata(ideType, ideVersion, host, userName, kind, what, description, accesstime, exception) "+
				"values ('ride', 'x.x.x', 'd-shc-00355773', 'qingqliu', 'buildevent', 'runMavenBuild' ,'for test',  to_date('"+
				strDate +
				"', 'DD-MM-YY hh24:mi:ss') "+ 
				", '[ERROR] No goals have been specified for this build . You must specify a valid lifecycle phase or a goal in the format ...')";
		
		dao.getJdbcTemplate().execute(INSERT);
		int size = dao.getJdbcTemplate().queryForInt(SELECT);
		UDCUpdateJob job = new UDCUpdateJob(ca.getTime());
		job.run();
		int size2 = dao.getJdbcTemplate().queryForInt(SELECT);
		Assert.assertTrue(size2-size>0);
				
	}
	@After
	public void tearDown() throws Exception {
		dao.getJdbcTemplate().execute(DELETE_TEST_DATA);	
	}
}
