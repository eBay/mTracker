package com.ebay.build.profiler.filter;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.ebay.build.profiler.filter.model.Cause;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.utils.StringUtils;

public class FilterMatcher {

	public boolean isMatchFilter(HashMap<String, String> source, Filter filter) {
//		for (Cause cause : filter.getCause()) {
//			for (String key : source.keySet()) {
//				if (key.equals(cause.getSource()) && source.get(key) != null) {
//					if (!isMatchCause(source.get(key), cause)) {
//						return false;
//					}
//				}
//			}
//		}
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
		if (!StringUtils.isEmpty(cause.getKeyword()) && content.equals(cause.getKeyword())) {
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
