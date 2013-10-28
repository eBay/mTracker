/*
 * Created on Aug 19, 2010 Copyright (c) eBay, Inc. 2010 All rights reserved.
 */
package com.ebay.build.udc.dao;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ebay.build.udc.UsageDataInfo;

/**
 * @author bishen
 */
public class UsageDataDaoJDBCImpl implements IUsageDataDao {

    private ApplicationContext context = null;
    private  UDCJDBCTemplate udcJdbc = null;

    public UsageDataDaoJDBCImpl() {
        super();

        System.out.println("[INFO]: init UDCJDBCTemplate bean...");
        context = new ClassPathXmlApplicationContext(
                "udc-sping-jdbc-config.xml");
        udcJdbc = (UDCJDBCTemplate) context.getBean("UDCJDBCTemplate");
        System.out.println("[INFO]: finish initing UDCJDBCTemplate bean!");

    }
    
    
    public JdbcTemplate getJdbcTemplate() {
		return udcJdbc.getJdbcTemplate();
	}


	@Override
    public int[] insertUsageData(List<UsageDataInfo> infos) throws DaoException {

        if (infos == null) {
            return new int[0];
        }

        return udcJdbc.create(infos);

    }

	@Override
	public List<UsageDataInfo> queryUsageData(UsageDataInfo data)
			throws DaoException {
		 return udcJdbc.query(data);
	}
}
