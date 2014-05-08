package com.ebay.build.udc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.ebay.build.profiler.filter.RideErrorClassifier;
import com.ebay.build.profiler.utils.DateUtils;
import com.ebay.build.udc.dao.IUsageDataDao;
import com.ebay.build.udc.dao.IUsageDataDao.DaoException;
import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;

public class UDCUpdateJob {
	private static Logger logger = Logger.getLogger(UDCUpdateJob.class.getName());
	private IUsageDataDao dao;
	private Date fromDate;
	private RideErrorClassifier errorClassifier;
	public UDCUpdateJob(Date fromDate) throws JAXBException{
		dao = new UsageDataDaoJDBCImpl(null);
		this.fromDate = fromDate;
		errorClassifier = new RideErrorClassifier();
	}
	
	public void run(){
		Date currDate = DateUtils.getCurrDate();
		Date startDate = fromDate;
		try {
			//update day by day
			while(startDate.before(currDate)){
				long time = System.currentTimeMillis();
				Date tempDate = DateUtils.addDays(startDate, 1);
				int result = dao.queryAndUpdateUncategoriedErrorRecords(startDate, tempDate, errorClassifier);
				String startStr = new SimpleDateFormat("dd-MM-yy").format(startDate);
				logger.log(Level.INFO,  startStr + ": Update "+result+" records. Excute time is: " + (System.currentTimeMillis() - time));
				startDate = tempDate;
			}
		} catch (DaoException e) {
			logger.log(Level.SEVERE, "Error In Update Job");
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args){
		
		Date date = DateUtils.addDays(DateUtils.getCurrDate(), -30);
		try {
			UDCUpdateJob job = new UDCUpdateJob(date);
			job.run();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	} 
	
}
