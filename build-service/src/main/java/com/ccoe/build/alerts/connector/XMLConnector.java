package com.ccoe.build.alerts.connector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class XMLConnector {

	public static<T> void marshal(File reportFile, T obj, Class<T> theClass) {
		try {
			JAXBContext jc;

			jc = JAXBContext.newInstance(theClass);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(obj, reportFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static<T> T unmarshal(File reportFile, Class<T> theClass) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(theClass);
			Unmarshaller u = jc.createUnmarshaller();
			return (T) u.unmarshal(new FileInputStream(reportFile));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
