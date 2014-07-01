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

package com.ccoe.build.core.filter;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.ccoe.build.core.filter.model.Cause;
import com.ccoe.build.core.filter.model.Filter;
import com.ccoe.build.core.utils.StringUtils;

public class FilterMatcher {

	public boolean isMatch(HashMap<String, String> source, Filter filter) {
		for (Cause cause : filter.getCause()) {
			String sourceName = cause.getSource();
			//whether sourceName is null or not, there should be a key with value sourceName in source
			//or the record can't match, that means returning false;
			if(source.containsKey(sourceName)) {
				String value = source.get(sourceName);   //wouldn't cause error when sourceName is null;
				if(!isMatchCause(value, cause)) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean isMatch(String fullstack, Filter filter) {
		for (Cause cause : filter.getCause()) {
			if (!isMatchCause(fullstack, cause)) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean isMatchCause(String content, Cause cause) {
		if (isMatchContent(content, cause)
				|| isMatchKeyword(content, cause)
				|| isMatchPattern(content, cause)) {
			return true;
		}
		return false;
	}
	
	protected boolean isMatchContent(String content, Cause cause) {
		if (!StringUtils.isEmpty(cause.getValue()) && content.equals(cause.getValue())) {
			return true;
		}
		return false;
	}
	
	protected boolean isMatchKeyword(String content, Cause cause) {
		if (!StringUtils.isEmpty(cause.getKeyword()) && content.contains(cause.getKeyword())) {
			return true;
		}
		return false;
	}
	
	protected boolean isMatchPattern(String content, Cause cause) {
		if (!StringUtils.isEmpty(cause.getPattern())) {
			if (Pattern.compile(cause.getPattern(), Pattern.DOTALL).matcher(content).matches()) {
			//if (StringUtils.isEmpty(StringUtils.getFirstFound(fullstack, cause.getPattern(), true))) {
				return true;
			}
		}
		return false;
	}
}
