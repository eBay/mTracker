package com.ebay.build.udc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.ebay.build.udc.dao.IUsageDataDao;
import com.ebay.build.udc.dao.IUsageDataDao.DaoException;
import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.profiler.utils.DateUtils;
import com.ebay.ide.profile.filter.RideFilterFactory;
import com.ebay.ide.profile.filter.model.RideFilter;

public class UDCUpdateJob {
	private IUsageDataDao dao;
	private Date fromDate;
	private List<RideFilter> lsFilter;
	public UDCUpdateJob(Date fromDate) throws JAXBException{
		dao = new UsageDataDaoJDBCImpl(null);
		this.fromDate = fromDate;
		RideFilterFactory factory = new RideFilterFactory();
		lsFilter = factory.getRideFilters();
	}
	
	public void run(){
		Date currDate = DateUtils.getCurrDate();
		Date startDate = fromDate;
		try {
			//update day by day
			while(startDate.before(currDate)){
				long time = System.currentTimeMillis();
				Date tempDate = DateUtils.addDays(startDate, 1);
				int result = dao.queryAndUpdateUncategoriedErrorRecords(startDate, tempDate, lsFilter);
				String startStr = new SimpleDateFormat("dd-MM-yy").format(startDate);
				System.out.println(startStr + ": Update "+result+" records. Excute time is: " + (System.currentTimeMillis() - time));
				startDate = tempDate;
			}
		} catch (DaoException e) {
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
