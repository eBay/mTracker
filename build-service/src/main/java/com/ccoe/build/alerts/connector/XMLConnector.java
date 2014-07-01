/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

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
