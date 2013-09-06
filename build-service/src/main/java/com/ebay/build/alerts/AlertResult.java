package com.ebay.build.alerts;

import java.util.ArrayList;
import java.util.List;

public class AlertResult {

	/**
	 * @param args
	 */
	private List<SingleResult> resultlist = new ArrayList<SingleResult>();


	public List<SingleResult> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List <SingleResult> resultlist) {
		this.resultlist = resultlist;
	}
}
