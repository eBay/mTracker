package com.ebay.ide.profile.filter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ebay.ide.profile.filter.model.RideCategory;
import com.ebay.ide.profile.filter.model.RideCause;
import com.ebay.ide.profile.filter.model.RideFilter;
import com.ebay.ide.profile.filter.model.RideFilters;


@XmlRootElement(name = "data")
class Rules{
	@XmlElement(name="row")
	List<Rule> lsRules = new ArrayList<Rule>();

	public List<Rule> getRules() {
		return lsRules;
	}

	public void setRules(List<Rule> rules) {
		this.lsRules = rules;
	}
		
}

@XmlAccessorType(XmlAccessType.FIELD)
class Rule{
	@XmlElement(name="id")
	private int id;
	@XmlElement(name="pattern")
	private String keyword;
	@XmlElement(name="errorcode")
	private String errorCode;
	@XmlElement(name="what")
	private String what;
	@XmlElement(name="causedByName")
	private String errorCategory;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getWhat() {
		return what;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public String getErrorCategory() {
		return errorCategory;
	}
	public void setErrorCategory(String errorCategory) {
		this.errorCategory = errorCategory;
	}
}

public class GenerateFilter {
	
	public Rules readRules() throws JAXBException{
		URL ruleXml = this.getClass().getResource("/error-rules-no-server.xml");
		JAXBContext jc;
		jc = JAXBContext.newInstance(Rules.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (Rules) u.unmarshal(ruleXml);
	}
	public RideFilters parseRules(Rules rules){
		List<Rule> lsRules = rules.getRules();
		//<category, <errorcode, <what, keyword>>>
		Map<String, Map<String, String[]>> map = new HashMap<String,Map<String, String[]>>();
		
		for(Rule rule: lsRules){
			String category = rule.getErrorCategory();
			String what = rule.getWhat();
			String errorCode = rule.getErrorCode();
			String keyword = rule.getKeyword();
			Map<String, String[]> mapValue;
			if(map.containsKey(category)){
				mapValue = map.get(category);
			}else{
				mapValue = new HashMap<String, String[]>();
				map.put(category, mapValue);
			}
			String[] strArray;
			if(mapValue.containsKey(errorCode)){
				strArray = mapValue.get(errorCode);
				System.out.println(strArray[0]+":"+strArray[1]+"<===> "+what+":"+keyword);
				continue;
			}else{
				strArray = new String[]{what, keyword};
				mapValue.put(errorCode, strArray);
			}
			
		}
		List<RideCategory> lsRideCategory = new ArrayList<RideCategory>();
		for(String category: map.keySet()){
			Map<String, String[]> value = map.get(category);
			List<RideFilter> lsRideFilter = new ArrayList<RideFilter>();
			for(String errorCode: value.keySet()){
				String[] whatPattern = value.get(errorCode);
				List<RideCause> lsRideCause = new ArrayList<RideCause>();
				RideCause cause1 = new RideCause();
				cause1.setSource("what");
				cause1.setValue(whatPattern[0]);
				RideCause cause2 = new RideCause();
				cause2.setKeyword(whatPattern[1]);
				lsRideCause.add(cause1);				
				lsRideCause.add(cause2);
				
				RideFilter rideFilter = new RideFilter();
				rideFilter.setName(errorCode);
				rideFilter.setDescription("N/A");
				rideFilter.setCause(lsRideCause);
				lsRideFilter.add(rideFilter);
			}
			RideCategory rideCategory = new RideCategory();
			rideCategory.setName(category);
			rideCategory.setFilter(lsRideFilter);
			lsRideCategory.add(rideCategory);
		}
		RideFilters rideFilters = new RideFilters();
		rideFilters.setCategory(lsRideCategory);
		return rideFilters;
	}
	public void saveFilters(RideFilters filters, String filePath) throws JAXBException{
		JAXBContext jc;
		
		jc = JAXBContext.newInstance(RideFilters.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//URL filterXml = this.getClass().getResource("/ride-filters.xml");
		
		File file = new File(filePath);
		marshaller.marshal(filters, file);
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateFilter ge = new GenerateFilter();
		String filePath = "C:\\ride-filters.xml";
		try {
			Rules rules = ge.readRules();
			RideFilters rideFilters = ge.parseRules(rules);
			ge.saveFilters(rideFilters, filePath);
			System.out.println("aaaa:" + rules.getRules().size());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
