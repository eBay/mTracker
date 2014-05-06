package com.ebay.build.alerts;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebay.build.alerts.connector.Connector;
import com.ebay.build.alerts.connector.XMLConnector;
import com.ebay.build.profiler.utils.DateUtils;
import com.ebay.build.profiler.utils.StringUtils;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Compare {

	private final Rules rules;
	private final DB db;
	private AlertResult alertResult = new AlertResult();
	private final String lightRed = "#FF0000";
	private final String lightGreen = "#92D050";
	private final String normalColor = "#CACACA";

	public Compare(File file, DB db) {
		rules = XMLConnector.unmarshal(file, Rules.class);
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
				collectionMap.put("RIDEWorkspaceSetup1", 1);
				collectionMap.put("RIDEServerStartup1", 2);
				collectionMap.put("CIBuild1", 3);
				collectionMap.put("CIBuild2", 4);
				collectionMap.put("SRPBenchmarkCI1", 5);
				collectionMap.put("SRPBenchmarkCI2", 6);
				collectionMap.put("CIAssembly1", 7);
				collectionMap.put("CIAssembly2", 8);
				collectionMap.put("SRPTeamCI1", 9);
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
		
		if (!StringUtils.isEmpty(rule.getCollectionPresentName())) {
			collection =  rule.getCollectionPresentName();
		}
		
		DBObject totaldbo = Connector.getLastRecord(dbc, current.getStartDate(), current.getEndDate());
       
		String field = rule.getField();
		String operatorStr = rule.getOperator();
		
		Date oneWeekBack = DateUtils.getUTCOneWeekBack(current.getEndDate());
		double movingAverage = Connector.getMovingAverage(dbc, field, oneWeekBack, current.getEndDate());
		String flag = "";
		if (Connector.DAYS < 7) {
			//flag = "~";
		}
		
		if ("lt".equals(operatorStr)) {
			operatorStr = "<";
		} else if ("le".equals(operatorStr)) {
			operatorStr = "<=";
		} else if ("eq".equals(operatorStr)) {
			operatorStr = "=";
		} else if ("gt".equals(operatorStr)) {
			operatorStr = ">";
		} else if ("ge".equals(operatorStr)) {
			operatorStr = ">=";
		}
		
		String goalStr = rule.getGoal();
		String lowerLimit = rule.getLower();
		String upperLimit = rule.getUpper();
		// only support type double
		double lower = Double.parseDouble(lowerLimit);
		double upper = Double.parseDouble(upperLimit);
		DecimalFormat df = new DecimalFormat("##0.00");
		SingleResult singleResult = null;

		if (totaldbo == null) {
			singleResult = new SingleResult(collection, field, "N/A", lightRed,
					operatorStr, goalStr, lowerLimit, upperLimit, "N/A", flag);

			if (!StringUtils.isEmpty(rule.getFieldPresentName())) {
				singleResult.setField(rule.getFieldPresentName());
			}
			if (!StringUtils.isEmpty(rule.getLink())) {
				singleResult.setLink(rule.getLink());
			}
			alertResult.getResultlist().add(singleResult);
			return;
		}
		
		DBObject dbo = (DBObject) totaldbo.get("Data");
		Set<String> keyset = dbo.keySet();

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
				
				String color = normalColor;
				if (keyvaluenum >= lower && keyvaluenum <= upper) {
					color = normalColor;					
				} else if ((keyvaluenum < lower && "<=".equals(operatorStr)) || (keyvaluenum > upper && ">=".equals(operatorStr))) {
					color = lightGreen;				
				} else if ((keyvaluenum < lower && ">=".equals(operatorStr)) || (keyvaluenum > upper && "<=".equals(operatorStr))) {
					color = lightRed;				
				}
				singleResult  = new SingleResult(collection, field, df.format(keyvaluenum), color,
						operatorStr, goalStr, lowerLimit, upperLimit, df.format(movingAverage), flag);
			}
		}
		if (singleResult == null) {			
			singleResult = new SingleResult(collection, field, "N/A", lightRed,
					operatorStr, goalStr, lowerLimit, upperLimit, "N/A", flag);
		}
		
		if (!StringUtils.isEmpty(rule.getFieldPresentName())) {
			singleResult.setField(rule.getFieldPresentName());
		}
		
		if (!StringUtils.isEmpty(rule.getLink())) {
			singleResult.setLink(rule.getLink());
		}
		alertResult.getResultlist().add(singleResult);
		return;
	}
	
}
