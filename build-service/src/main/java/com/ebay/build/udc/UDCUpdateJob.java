package com.ebay.build.udc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.ebay.build.udc.dao.IUsageDataDao;
import com.ebay.build.udc.dao.IUsageDataDao.DaoException;
import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.utils.DateUtil;
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
		Date currDate = DateUtil.getCurrDate();
		Date startDate = fromDate;
		try {
			//update day by day
			while(startDate.before(currDate)){
				long time = System.currentTimeMillis();
				List<UsageDataInfo> lsNeedUpdate = new ArrayList<UsageDataInfo>();
				
				Date tempDate = DateUtil.addDays(startDate, 1);
				//1. read data from db
				List<UsageDataInfo> lsInfo = dao.queryUncategoriedErrorRecords(startDate, tempDate);
				startDate = tempDate;
				if(lsInfo == null || lsInfo.size() == 0)
					continue;
				//2. find UsageDataInfo that need updated
				System.out.println("Start filter.....");
				for(UsageDataInfo info: lsInfo){
					RideFilter filter = findMatchFilter(info);
					if(filter != null){
						if(info.getCategory()!=null && info.getCategory().equals(filter.getCategory())
								&& info.getErrorCode()!=null && info.getErrorCode().equals(filter.getName())){
							continue;
						}else{
							info.setCategory(filter.getCategory());
							info.setErrorCode(filter.getName());
							lsNeedUpdate.add(info);
						}
					}else{
						if(info.getCategory() != null || info.getErrorCode() != null){
							info.setCategory(null);
							info.setErrorCode(null);
							lsNeedUpdate.add(info);
						}
					}
				}
				//3. update db
				System.out.println("Start update category and error code....");
				dao.updateUsageDataErrorInfo(lsNeedUpdate);
				
				System.out.println(System.currentTimeMillis() - time);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		} 
	}
	
	private RideFilter findMatchFilter(UsageDataInfo info){
		for(RideFilter filter: lsFilter){
			if(filter.isMatch(info.getWhat(), info.getException()))
			{
				return filter;
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		
		Date date = DateUtil.addDays(DateUtil.getCurrDate(), -3);
		try {
			UDCUpdateJob job = new UDCUpdateJob(date);
			job.run();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		

	} 
	
}
