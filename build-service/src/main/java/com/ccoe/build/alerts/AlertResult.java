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
