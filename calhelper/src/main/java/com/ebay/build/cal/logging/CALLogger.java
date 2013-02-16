package com.ebay.build.cal.logging;

import java.net.URL;

import com.ebay.kernel.cal.java.CalClientConfigBean;
import com.ebay.kernel.cal.java.JavaCalService;
import com.ebay.kernel.calwrapper.CalTransaction;

public interface CALLogger {
	
	/**
	 * 
	 * @param calConfig
	 */
	public boolean initialize(URL calConfig, String appName);
	
	/**
	 * 
	 * @param calClientCfgBean
	 */
	public void initialize(CalClientConfigBean calClientCfgBean);
	
	/**
	 * get available cal service.
	 * @return
	 */
	public JavaCalService getCalService();
	
	/**
	 * destroy() method needs to be called at the end of the application.
	 * This method is disconnect the CAL Service and terminate the process.
	 */
	public void destroy();
	
	/**
	 * Creates a CAL Transaction (default type URL) for the given transaction Name and data
	 * 
	 * @param transName name to identify the transaction
	 * @param transData data which needs to be logged 
	 * @return
	 */
	public CalTransaction startCALTransaction(String transName, String transData);
	
	/**
	 * Creates a CAL Transaction for the given transaction Name, transaction Type and data
	 * 
	 * @param transName name to identify the transaction
	 * @param transType type of the transaction
	 * @param transData data which needs to be logged 
	 * @return
	 */
	public CalTransaction startCALTransaction(String transName,String transType, String transData);
	
	/**
	 * Complete a CAL Transaction with a status
	 * 
	 * @param calTransaction
	 * @param status
	 */
	public void endCALTransaction(CalTransaction calTransaction, String status);
	
	/**
	 * Complete a CAL Transaction with a status
	 * 
	 * @param calTransaction
	 * @param status
	 */
	public void endCALTransaction(CalTransaction calTransaction, String status, Throwable t);
	
	/**
	 * log CAL Event with the given name and data
	 * @param eventType Type of the event
	 * @param data data to be logged
	 */
	public void logCALEvent(String eventType, String data);
	
	
	/**
	 * Answers if a CAL Service is initialized
	 * 
	 * @return
	 */
	public boolean isCalInitialized();
}
