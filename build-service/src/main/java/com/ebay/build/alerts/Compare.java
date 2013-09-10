package com.ebay.build.alerts;

import java.io.File;
import java.util.List;
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
	
	public Compare(File file, DB db) {
		rules = XMLConnector.unmarshal(file);
		this.db = db;
	}

	
	public AlertResult judgeRules(Condition condition) {
		List<Rule> rulelist = rules.getRulelist();
		for(Rule rule : rulelist){
			judgeSingleRule(rule, condition);
		}
		return alertResult;
	}
	
	public void judgeSingleRule(Rule rule, Condition condition) {
		String collection = rule.getCollection();

		DBCollection dbc = Connector.connectCollection(db, collection);
		DBObject totaldbo = Connector.getLastRecord(dbc, condition.getStartDate(), condition.getEndDate());
		DBObject dbo = (DBObject) totaldbo.get("Data");

		String field = rule.getField();
		String operatorStr = rule.getOperator();
		int operator;

		if ("lt".equals(operatorStr)) {
			operatorStr = "<";
			operator = 0;
		} else if ("eq".equals(operatorStr)) {
			operatorStr = "=";
			operator = 1;
		} else if ("gt".equals(operatorStr)) {
			operatorStr = ">";
			operator = 2;
		} else {
			operator = 3;
		}

		String thresholdStr = rule.getThreshold();

		// only support type double
		Double threshold = Double.parseDouble(thresholdStr);

		Set<String> keyset = dbo.keySet();

		SingleResult singleResult = null;
		for (String keyname : keyset) {
			if (field.equals(keyname)) {
				Object keyvalue = dbo.get(keyname);
				double keyvaluenum = 0;

				if (keyvalue instanceof Integer) {
					int k1 = (Integer) keyvalue;
					keyvaluenum = Double.parseDouble("" + k1);
				} else if (keyvalue instanceof Double) {
					keyvaluenum = (Double) keyvalue;
				}

				singleResult = new SingleResult(collection,
						keyname, keyvalue.toString(), operatorStr, thresholdStr);

				switch (operator) {
				case 0:
					if (keyvaluenum < threshold) {
						singleResult.setColor("black");
					} else {
						singleResult.setColor("red");
					}
					break;
				case 1:
					if (keyvalue.equals(threshold)) {
						singleResult.setColor("black");
					} else {
						singleResult.setColor("red");
					}
					break;
				case 2:
					if (keyvaluenum > threshold) {
						singleResult.setColor("black");
					} else {
						singleResult.setColor("red");
					}
					break;
				case 3:
				}
			}
		}
		if (singleResult != null) {
			alertResult.getResultlist().add(singleResult);
		} else {
			alertResult.getResultlist().add(new SingleResult(collection,
					field, "N/A", operatorStr, thresholdStr));
		}
		return;
	}
}
