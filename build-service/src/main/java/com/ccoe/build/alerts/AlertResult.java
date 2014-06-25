package com.ccoe.build.alerts;

import java.util.ArrayList;
import java.util.List;

public class AlertResult {

	private List<SingleResult> resultlist = new ArrayList<SingleResult>();


	public List<SingleResult> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List <SingleResult> resultlist) {
		this.resultlist = resultlist;
	}
}
