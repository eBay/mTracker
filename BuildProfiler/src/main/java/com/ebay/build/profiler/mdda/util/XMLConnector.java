package com.ebay.build.profiler.mdda.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ebay.build.profiler.mdda.bean.DArtifacts;

public class XMLConnector {

	public static void marshal(File reportFile, DArtifacts dArtifacts) {
		try {
			JAXBContext jc;

			jc = JAXBContext.newInstance(DArtifacts.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(dArtifacts, reportFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static DArtifacts unmarshal(File reportFile) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(DArtifacts.class);
			Unmarshaller u = jc.createUnmarshaller();
			return (DArtifacts) u.unmarshal(new FileInputStream(reportFile));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new DArtifacts();
	}
	
}
