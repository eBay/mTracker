package com.ebay.build.profiler.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ebay.build.profiler.filter.model.Cause;
import com.ebay.build.profiler.filter.model.Filter;

public class FilterMatcherTest {
	static FilterMatcher matcher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		matcher = new FilterMatcher();
	}

	@Test
	public void testIsMatchHashMapOfStringStringFilter() {
		List<Cause> lsCause = new ArrayList<Cause>();
		Cause cause = new Cause();
		cause.setSource("A");
		cause.setValue("A");
		lsCause.add(cause);

		cause = new Cause();
		cause.setSource("B");
		cause.setKeyword("B");
		lsCause.add(cause);
		
		cause = new Cause();
		cause.setSource("C");
		cause.setPattern("[C]");
		lsCause.add(cause);
		
		cause = new Cause();
		cause.setKeyword("D");
		lsCause.add(cause);
		
		Filter filter = new Filter();
		filter.setCategory("Category");
		filter.setDescription("Description");
		filter.setName("ErrorName");
		filter.setCauses(lsCause);
		//normal 		
		HashMap<String, String> source = new HashMap<String, String>();
		source.put("A", "A");
		source.put("B", "B");
		source.put("C", "C");
		source.put(null, "D");
		Assert.assertTrue(matcher.isMatch(source, filter));
		
		//source provide more information
		source.put("E", "E");
		Assert.assertTrue(matcher.isMatch(source, filter));
		
		//source lack some information
		source.remove(null);
		Assert.assertFalse(matcher.isMatch(source, filter));
		
		//source provide unproper information
		source.put(null, "X");
		Assert.assertFalse(matcher.isMatch(source, filter));
		
	}

	@Test
	public void testIsMatchStringFilter() {
		//a loop of isMatchCause;
	}

	@Test
	public void testIsMatchCause() {
		//a composite of testIsMatchContent, testIsMatchKeyword, testIsMatchPattern
	}

	@Test
	public void testIsMatchContent() {
		String content = "sample";
		Cause cause = new Cause();
		cause.setValue(content);
		Assert.assertTrue(matcher.isMatchContent(content, cause));
		
		content = "Sample";
		Assert.assertFalse(matcher.isMatchContent(content, cause));
		
		Cause cause1 = new Cause();
		Assert.assertFalse(matcher.isMatchContent(content, cause1));
	}

	@Test
	public void testIsMatchKeyword() {
		String content = "Hello world!";
		Cause cause = new Cause();
		cause.setKeyword("Hello");
		Assert.assertTrue(matcher.isMatchKeyword(content, cause));
		
		content = "For Test";
		Assert.assertFalse(matcher.isMatchKeyword(content, cause));
		
		Cause cause1 = new Cause();
		Assert.assertFalse(matcher.isMatchKeyword(content, cause1));
	}

	@Test
	public void testIsMatchPattern() {
		String content = "Hello world for 2014";
		Cause cause = new Cause();
		cause.setPattern("Hello world for [0-9]{4}");
		Assert.assertTrue(matcher.isMatchPattern(content, cause));
		
		content = "Hello world for";
		Assert.assertFalse(matcher.isMatchPattern(content, cause));
		
		Cause cause1 = new Cause();
		Assert.assertFalse(matcher.isMatchPattern(content, cause1));
	}

}
