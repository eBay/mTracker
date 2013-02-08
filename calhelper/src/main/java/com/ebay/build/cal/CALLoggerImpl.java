package com.ebay.build.cal;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.inject.Singleton;

import org.codehaus.plexus.component.annotations.Component;

import com.ebay.kernel.cal.CalServiceFactory;
import com.ebay.kernel.cal.java.CalClientConfigBean;
import com.ebay.kernel.cal.java.JavaCalService;
import com.ebay.kernel.calwrapper.CalEvent;
import com.ebay.kernel.calwrapper.CalEventFactory;
import com.ebay.kernel.calwrapper.CalTransaction;
import com.ebay.kernel.calwrapper.CalTransactionFactory;
import com.ebay.kernel.calwrapper.CalTransactionHelper;

/**
 * 
 * @author kmuralidharan
 *
 */
@Component( role = CALLogger.class )
@Singleton
public class CALLoggerImpl implements CALLogger{
	private final String CAL_DEFAULT_TRAN_TYPE = "URL";
	private JavaCalService calService;
	
	public CALLoggerImpl(){
		
	}
	
	/**
	 * 
	 * @param calConfig
	 */
	public boolean initialize(URL calConfig, String appName) {
		
		boolean isSuccess=true;
		
		if (calConfig == null){
			throw new IllegalArgumentException("calConfig URL cannot be Null");
		}
		CalClientConfigBean calClientCfgBean = new CalClientConfigBean(null,false,calConfig);
		try {
			calClientCfgBean.setPoolname(appName + "-MavenBuild");
			calClientCfgBean.setMachineName(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e1) {
			isSuccess=false;
		}
		
		try {
			initialize(calClientCfgBean);
		} catch (Throwable t) {
			isSuccess = false;
		}
		
		return isSuccess;
	}
	
	/**
	 * 
	 * @param calClientCfgBean
	 */
	public void initialize(CalClientConfigBean calClientCfgBean){
		if(isCalInitialized()){
			return;
		}
		if (calClientCfgBean == null){
			throw new IllegalArgumentException("calConfig URL cannot be Null");
		}
		calService = new JavaCalService(calClientCfgBean, CalTransactionHelper.CAL_TRANSACTION_LEVEL);
		CalServiceFactory.setCalService(calService);
		
		System.out.println("initialized CAL...");
	}
	
	/**
	 * get available cal service.
	 * @return
	 */
	public JavaCalService getCalService(){
		return calService;
	}
	
	/**
	 * destroy() method needs to be called at the end of the application.
	 * This method is disconnect the CAL Service and terminate the process.
	 */
	public void destroy()
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(calService != null){
			calService.disconnect();
		}
		
		System.out.println("CAL is destroied...");
		
	}
	
	/**
	 * Creates a CAL Transaction (default type URL) for the given transaction Name and data
	 * 
	 * @param transName name to identify the transaction
	 * @param transData data which needs to be logged 
	 * @return
	 */
	public CalTransaction startCALTransaction(String transName, String transData) {
		return createTransaction(transName, CAL_DEFAULT_TRAN_TYPE, transData);
	}
	
	/**
	 * Creates a CAL Transaction for the given transaction Name, transaction Type and data
	 * 
	 * @param transName name to identify the transaction
	 * @param transType type of the transaction
	 * @param transData data which needs to be logged 
	 * @return
	 */
	public CalTransaction startCALTransaction(String transName,String transType, String transData) {
		return createTransaction(transName, transType, transData);
	}
	
	/**
	 * Complete a CAL Transaction with a status
	 * 
	 * @param calTransaction
	 * @param status
	 */
	public void endCALTransaction(CalTransaction calTransaction, String status) {
		calTransaction.setStatus(status);
		calTransaction.completed();
	}
	
	/**
	 * Complete a CAL Transaction with a status
	 * 
	 * @param calTransaction
	 * @param status
	 */
	public void endCALTransaction(CalTransaction calTransaction, String status, Throwable t) {
		calTransaction.setStatus(t);
		calTransaction.completed();
	}
	
	/**
	 * log CAL Event with the given name and data
	 * @param eventType Type of the event
	 * @param data data to be logged
	 */
	public void logCALEvent(String eventType, String data) {
	    CalEvent event = CalEventFactory.create(eventType);
	    event.setName(eventType);
	    event.addData(data);
	    event.setStatus("0");
	    event.completed();
	  }
	
	private CalTransaction createTransaction(String transName, String transType, String transData) {
		CalTransaction calTransaction = CalTransactionFactory.create(transType);
		calTransaction.setName(transName);
		calTransaction.addData(transData);
		return calTransaction;
	}
	
	/**
	 * Answers if a CAL Service is initialized
	 * 
	 * @return
	 */
	public boolean isCalInitialized(){
		return calService != null;
	}
}
