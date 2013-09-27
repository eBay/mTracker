package com.ebay.build.alerts;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebay.build.alerts.connector.Connector;
import com.ebay.build.alerts.connector.XMLConnector;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Compare {

	private final Rules rules;
	private final DB db;
	private AlertResult alertResult = new AlertResult();
	private final String lightRed = "#FF9696";
	private final String lightGreen = "#E0FFA3";
	private final String normalColor = "#CACACA";

	public Compare(File file, DB db) {
		rules = XMLConnector.unmarshal(file);
		this.db = db;
	}

	public AlertResult judgeRules(Condition current, Condition previous) {
		List<Rule> rulelist = rules.getRulelist();
		for (Rule rule : rulelist) {
			judgeSingleRule(rule, current, previous);
		}

		Collections.sort(alertResult.getResultlist(), new Comparator<SingleResult>() {

			@Override
			public int compare(SingleResult result1, SingleResult result2) {
				Map<String, Integer> collectionMap = new HashMap<String, Integer>();
				collectionMap.put("RIDEWorkspaceSetupData", 1);
				collectionMap.put("RIDEServerStartupData", 2);
				collectionMap.put("CIBuildData", 3);
				collectionMap.put("EDEWorkspaceSetupData", 4);
				collectionMap.put("EDEServerStartupData", 5);
				collectionMap.put("ICEBuildData", 6);
				int value1 = collectionMap.get(result1.getCollection());
				int value2 = collectionMap.get(result2.getCollection());
				return value1 - value2;
			}
		});

		return alertResult;
	}

	public void judgeSingleRule(Rule rule, Condition current, Condition previous) {
		String collection = rule.getCollection();
		DBCollection dbc = Connector.connectCollection(db, collection);
		if ("CIData".equals(collection)) {
			collection = "CIBuildData";
		}
		DBObject totaldbo = Connector.getLastRecord(dbc, current.getStartDate(),
				current.getEndDate());
		DBObject previousdbo = Connector.getLastRecord(dbc, previous.getStartDate(),
				previous.getEndDate());
		String field = rule.getField();
		String operatorStr = rule.getOperator();

		if ("lt".equals(operatorStr)) {
			operatorStr = "<";
		} else if ("eq".equals(operatorStr)) {
			operatorStr = "=";
		} else if ("gt".equals(operatorStr)) {
			operatorStr = ">";
		}

		String thresholdStr = rule.getThreshold();
		String deltaThresholdStr = rule.getDeltaThreshold();

		// only support type double
		Double threshold = Double.parseDouble(thresholdStr);
		Double deltaThreshold = Double.parseDouble(deltaThresholdStr);
		DecimalFormat df = new DecimalFormat("##0.00");
		SingleResult singleResult = null;

		if (totaldbo == null) {
			if (previousdbo == null) {
				singleResult = new SingleResult(collection, field, "N/A", operatorStr,
						thresholdStr, "N/A", lightRed, "N/A", deltaThresholdStr, "N/A",
						lightRed);
			} else {
				DBObject previousObject = (DBObject) previousdbo.get("Data");
				Set<String> key = previousObject.keySet();

				for (String keyname : key) {
					if (field.equals(keyname)) {
						Object beforeKeyValue = previousObject.get(keyname);
						double beforeKeyValueNum = 0;
						if (beforeKeyValue instanceof Integer) {
							int k1 = (Integer) beforeKeyValue;
							beforeKeyValueNum = Double.parseDouble("" + k1);
						} else if (beforeKeyValue instanceof Double) {
							beforeKeyValueNum = (Double) beforeKeyValue;
						}

						if ("ICEBuildData".equals(collection) && "Average".equals(field)) {
							beforeKeyValueNum = (Double) beforeKeyValue / 60;
							beforeKeyValue = (Double) beforeKeyValue / 60;
						}
						singleResult = new SingleResult(collection, field, "N/A",
								operatorStr, thresholdStr, "N/A", lightRed,
								df.format(beforeKeyValueNum), deltaThresholdStr, "N/A",
								lightRed);
					}
				}
			}

			if ("Average".equals(field)) {
				singleResult.setField("Performance");
			}
			alertResult.getResultlist().add(singleResult);
			return;
		}
		DBObject dbo = (DBObject) totaldbo.get("Data");
		Set<String> keyset = dbo.keySet();

		for (String keyname : keyset) {
			if (field.equals(keyname)) {
				double outOfThreshold;
				Object keyvalue = dbo.get(keyname);
				double keyvaluenum = 0;
				if (keyvalue instanceof Integer) {
					int k1 = (Integer) keyvalue;
					keyvaluenum = Double.parseDouble("" + k1);
				} else if (keyvalue instanceof Double) {
					keyvaluenum = (Double) keyvalue;
				}

				if ("ICEBuildData".equals(collection) && "Average".equals(field)) {
					keyvaluenum = (Double) keyvalue / 60;
					keyvalue = (Double) keyvalue / 60;
				}

				String outOfThresholdColor = null;
				outOfThreshold = keyvaluenum - threshold;

				if (">".equals(operatorStr) && (outOfThreshold < 0)
						|| "<".equals(operatorStr) && (outOfThreshold > 0)) {
					outOfThresholdColor = lightRed;
				} else {
					outOfThresholdColor = normalColor;
				}

				if (previousdbo == null) {
					singleResult = new SingleResult(collection, keyname,
							df.format(keyvalue), operatorStr, thresholdStr,
							df.format(outOfThreshold), outOfThresholdColor, "N/A",
							deltaThresholdStr, "N/A", lightRed);
				} else {
					DBObject previousObject = (DBObject) previousdbo.get("Data");
					Set<String> key = previousObject.keySet();

					for (String beforeKeyName : key) {
						if (field.equals(beforeKeyName)) {
							Object beforeKeyValue = previousObject.get(beforeKeyName);
							double beforeKeyValueNum = 0;

							if (beforeKeyValue instanceof Integer) {
								int k1 = (Integer) beforeKeyValue;
								beforeKeyValueNum = Double.parseDouble("" + k1);
							} else if (beforeKeyValue instanceof Double) {
								beforeKeyValueNum = (Double) beforeKeyValue;
							}

							if ("ICEBuildData".equals(collection)
									&& "Average".equals(field)) {
								beforeKeyValueNum = (Double) beforeKeyValue / 60;
								beforeKeyValue = (Double) beforeKeyValue / 60;
							}
							
							String outOfThresholdDeltaColor = null;
							double outOfDelta = keyvaluenum - beforeKeyValueNum;
							if (Math.abs(outOfDelta) <= deltaThreshold) {
								outOfThresholdDeltaColor = normalColor;
							} else {
								if (">".equals(operatorStr) && (outOfDelta > 0)
										|| "<".equals(operatorStr) && (outOfDelta < 0)) {
									outOfThresholdDeltaColor = lightGreen;
								} else if (">".equals(operatorStr) && (outOfDelta < 0)
										|| "<".equals(operatorStr) && (outOfDelta > 0)) {
									outOfThresholdDeltaColor = lightRed;
								} else {
									outOfThresholdDeltaColor = normalColor;
								}								
							}
							singleResult = new SingleResult(collection, field,
									df.format(keyvalue), operatorStr, thresholdStr,
									df.format(outOfThreshold), outOfThresholdColor,
									df.format(beforeKeyValue), deltaThresholdStr,
									df.format(outOfDelta), outOfThresholdDeltaColor);
						}
					}

				}

			}
		}
		if (singleResult == null) {
			singleResult = new SingleResult(collection, field, "N/A", operatorStr,
					thresholdStr, "N/A", lightRed, "N/A", deltaThresholdStr, "N/A",
					lightRed);
		}
		if ("Average".equals(field)) {
			singleResult.setField("Performance");
		}
		alertResult.getResultlist().add(singleResult);
		return;
	}
}
