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

package com.ccoe.build.alerts.devx;

public class ValueRetriever {
	
	private final Collections collections;
	
	public ValueRetriever(Collections c) {
		this.collections = c;
	}
	
	public String getValue(String collectionName, String fieldName) {
		for (Collection collection : collections.getCollections()) {
			if (collectionName.equals(collection.getName())) {
				for (Field field : collection.getFields()) {
					if (fieldName.equals(field.getName())) {
						if (field.getValue() < 0) {
							return "N/A";
						} else {
							if (field.getName().contains("Reliability")) {
								return field.getValue() + "%";
							} else if (field.getName().contains("Events")) {
								return (int) field.getValue() + "";
							} else {
								return field.getValue() + " min";
							}
						}
					}
				}
			} else {
				continue;
			}
		}
		return "N/A";
	}
}
