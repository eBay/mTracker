package com.ebay.build.udc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ebay.build.core.filter.ErrorClassifier;
import com.ebay.build.core.utils.DateUtils;
import com.ebay.build.udc.dao.IUsageDataDao;
import com.ebay.build.udc.dao.IUsageDataDao.DaoException;

public class UDCUpdateJob extends Thread{
	private static Logger logger = Logger.getLogger(UDCUpdateJob.class.getName());
	private IUsageDataDao dao;
	private Date fromDate;
	private ErrorClassifier errorClassifier;
	private StringBuffer inProcessStatus = new StringBuffer();

	public UDCUpdateJob(){
		logger.log(Level.INFO, "Initialize UDCUpdateJob");
		inProcessStatus.append("Init");
		fromDate = DateUtils.addDays(DateUtils.getCurrDate(), -30);
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setDao(IUsageDataDao dao) {
		this.dao = dao;
	}
	public void setErrorClassifier(ErrorClassifier errorClassifier) {
		this.errorClassifier = errorClassifier;
	}

	public String getStatus(){
		return inProcessStatus.toString();
	}
	private void changeStringBufferContent(StringBuffer sb, String content){
		sb.delete(0, sb.length());
		sb.append(content);
	}
	public void run(){
		changeStringBufferContent(inProcessStatus, "Executing");
		String resultMsg;
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
				changeStringBufferContent(inProcessStatus," Executing. " + startStr + ": Update "+result+" records. Excute time is: " + (System.currentTimeMillis() - time));
				startDate = tempDate;
			}
			resultMsg="Success";
		} catch (DaoException e) {
			logger.log(Level.SEVERE, "Error In Update Job");
			resultMsg="Failed";
			e.printStackTrace();
		} 
		changeStringBufferContent(inProcessStatus, resultMsg);		
	}
	
}
