package com.ebay.build.udc;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.udc.dao.IUsageDataDao.DaoException;

public class PaypalDataCollectorTest {
	private static final String SELECT_COUNT = "Select count(*) from usagedata4p where ideVersion = 'x.x.x'";
	private static final String DELETE_TEST_DATA = "delete from usagedata4p where ideVersion='x.x.x'";
	
	@Test
	public void testCollect() throws DaoException {
		// do not rename the test resouce file, UDC will parse the name
		URL url = getClass().getClassLoader().getResource(
				"D-SHC-00436909_wecai_1382582655937.csv");
		File file = new File(url.getFile());

		UsageDataDaoJDBCImpl dao = new UsageDataDaoJDBCImpl("paypal");

		new UsageDataRecorder(Arrays.asList(file), "paypal").run();

		int newSize = dao.getJdbcTemplate().queryForInt(SELECT_COUNT);
		Assert.assertTrue(newSize == 2);
		
		dao.getJdbcTemplate().execute(DELETE_TEST_DATA);

	}
}
