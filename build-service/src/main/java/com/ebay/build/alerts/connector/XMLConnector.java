package com.ebay.build.alerts.connector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ebay.build.alerts.Rules;


public class XMLConnector {

	public static void marshal(File reportFile, Rules rules) {
		try {
			JAXBContext jc;

			jc = JAXBContext.newInstance(Rules.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(rules, reportFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static Rules unmarshal(File reportFile) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(Rules.class);
			Unmarshaller u = jc.createUnmarshaller();
			return (Rules) u.unmarshal(new FileInputStream(reportFile));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Rules();
	}
	
}
