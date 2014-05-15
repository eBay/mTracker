package com.ebay.build.udc;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ebay.build.udc.dao.IUsageDataDao.DaoException;
import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.utils.SpringConfig;

public class PaypalDataCollectorTest {
	private static final String SELECT_COUNT = "Select count(*) from {0} where ideVersion = ''x.x.x''";
	private static final String DELETE_TEST_DATA = "delete from {0} where ideVersion=''x.x.x''";
	private UsageDataDaoJDBCImpl dao;
	private String udcTable;
	private UsageDataRecorder recorder;
	
	@Before
	public void setUp() throws Exception {
		dao = (UsageDataDaoJDBCImpl)SpringConfig.getBean("paypalDao");
		udcTable = dao.getUdcJdbc().getUdcTable();
		recorder = (UsageDataRecorder)SpringConfig.getBean("paypalUsageDataRecorder");
	}
	@Test
	public void testCollect() throws DaoException {
		// do not rename the test resouce file, UDC will parse the name
		URL url = getClass().getClassLoader().getResource(
				"D-SHC-00436909_qingqliu_1382582655937.csv");
		File file = new File(url.getFile());

		recorder.setFiles(Arrays.asList(file));
		recorder.run();

		String sql = MessageFormat.format(SELECT_COUNT, udcTable);
		int newSize = dao.getJdbcTemplate().queryForInt(sql);
		Assert.assertTrue(newSize == 2);
				
	}
	@After
	public void tearDown() throws Exception {
		String sql = MessageFormat.format(DELETE_TEST_DATA, udcTable);
		dao.getJdbcTemplate().execute(sql);
	}
}
