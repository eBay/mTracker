package com.ebay.build.cal.logging;

import java.lang.reflect.Method;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

public class CALUtils {
	@SuppressWarnings("rawtypes")
	private Class caloggerClz;
	private Object caloggerObj;
	
	@SuppressWarnings("rawtypes")
	private Class calTransactionClz;
	
	public CALUtils(MavenSession session){
		
		initialize(session);
	}
	
	@SuppressWarnings("deprecation")
	private void initialize(MavenSession session) {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		
		try {
			ClassRealm containerRealm = session.getContainer().getContainerRealm();
			
			Thread.currentThread().setContextClassLoader( containerRealm );
			
			caloggerClz = containerRealm.loadClass("com.ebay.build.cal.CALLoggerImpl");
			caloggerObj = session.getContainer().lookup("com.ebay.build.cal.CALLogger");
			
			calTransactionClz = containerRealm.loadClass("com.ebay.kernel.calwrapper.CalTransaction");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader( oldClassLoader );
		}
		
	}

	@SuppressWarnings("unchecked")
	public boolean isCalInitialized(){
		Method isCalInitialized;
		try {
			isCalInitialized = caloggerClz.getMethod("isCalInitialized");
			Boolean calServiceObj = (Boolean)isCalInitialized.invoke(caloggerObj);
			return calServiceObj.booleanValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Object startCALTransaction(String transName, String transData){
		Method startTrasaction;
		try {
			startTrasaction = caloggerClz.getMethod("startCALTransaction", String.class, String.class);
			return startTrasaction.invoke(caloggerObj, transName, transData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void endCALtransaction(Object calTransactionObj, String status){
		Method endTrasaction;
		try {
			endTrasaction = caloggerClz.getMethod("endCALTransaction", calTransactionClz, String.class);
			endTrasaction.invoke(caloggerObj, calTransactionObj, status);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void logCALEvent(String eventType, String data){
		Method logCALEvent;
		try {
			logCALEvent = caloggerClz.getMethod("logCALEvent", String.class, String.class);
			logCALEvent.invoke(caloggerObj, eventType, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
