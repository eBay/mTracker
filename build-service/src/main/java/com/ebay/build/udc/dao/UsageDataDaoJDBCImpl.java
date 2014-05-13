/*
 * Created on Aug 19, 2010 Copyright (c) eBay, Inc. 2010 All rights reserved.
 */
package com.ebay.build.udc.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ebay.build.profiler.filter.ErrorClassifier;
import com.ebay.build.udc.UsageDataInfo;

/**
 * @author bishen
 */
public class UsageDataDaoJDBCImpl implements IUsageDataDao {

	private UDCJDBCTemplate udcJdbc = null;
	public void setUdcJdbc(UDCJDBCTemplate udcJdbc) {
		this.udcJdbc = udcJdbc;
	}
    public UDCJDBCTemplate getUdcJdbc(){
    	return this.udcJdbc;
    }
	public UsageDataDaoJDBCImpl() {
		super();
	}

	public JdbcTemplate getJdbcTemplate() {
		return udcJdbc.getJdbcTemplate();
	}

	@Override
	public int insertUsageData(List<UsageDataInfo> infos) throws DaoException {

		if (infos == null) {
			return 0;
		}

		return udcJdbc.create(infos);

	}

	@Override
	public List<UsageDataInfo> queryUsageData(UsageDataInfo data) throws DaoException {
		return udcJdbc.query(data);
	}

	@Override
	public int queryAndUpdateUncategoriedErrorRecords(
			Date startDate, Date endDate, ErrorClassifier errorClassifier) throws DaoException {
		return udcJdbc.queryAndUpdateUncategoriedErrorRecords(startDate, endDate, errorClassifier);
	}

}
