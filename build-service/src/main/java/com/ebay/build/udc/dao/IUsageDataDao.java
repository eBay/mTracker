/*
 * Created on Aug 19, 2010 Copyright (c) eBay, Inc. 2010 All rights reserved.
 */

package com.ebay.build.udc.dao;

import java.util.Date;
import java.util.List;

import com.ebay.build.profiler.filter.RideErrorClassifier;
import com.ebay.build.udc.UsageDataInfo;

/**
 * @author bishen
 */
public interface IUsageDataDao
       
{

    /**
     * Inserts the given usage data.
     * 
     * @param infos
     * @return
     * @throws DaoException
     */
    public int insertUsageData(List<UsageDataInfo> infos)
            throws DaoException;
    
    public List<UsageDataInfo> queryUsageData(UsageDataInfo data)
             throws DaoException;
    
    public int queryAndUpdateUncategoriedErrorRecords(Date startDate, Date endDate, RideErrorClassifier errorClassifier)
    		 throws DaoException;
    
    
    public class DaoException extends Exception {
    	
    	public DaoException (Throwable t) {
    		super(t);
    	} 

    	public DaoException (String str) {
    		super(str);
    	} 

    	public DaoException (String str, Throwable t) {
    		super(str, t);
    	}
    }


}
