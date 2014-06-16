package com.ebay.build.core.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.ebay.build.core.filter.FilterFactory;
import com.ebay.build.core.filter.MavenBuildFilterFactory;
import com.ebay.build.core.filter.model.Category;
import com.ebay.build.core.filter.model.Cause;
import com.ebay.build.core.filter.model.Filter;
import com.ebay.build.core.filter.model.Filters;

public class FilterFactoryTest {

	@Test
	public void testBuild() {
		Filters filters = new Filters();
		
		Cause cause1 = new Cause();
		cause1.setKeyword("key1");
		
		Cause cause2 = new Cause();
		cause2.setKeyword("key2");
		
		Filter filter1 = new Filter();
		filter1.setName("filter1");
		filter1.getCause().add(cause1);
		filter1.getCause().add(cause2);
		
		Category category1 = new Category();
		category1.setName("user");
		category1.getFilter().add(filter1);
		
		Cause cause3 = new Cause();
		cause3.setKeyword("key3");
		
		Cause cause4 = new Cause();
		cause4.setKeyword("key4");
		
		Filter filter2 = new Filter();
		filter2.setName("filter2");
		filter2.getCause().add(cause3);
		filter2.getCause().add(cause4);

		Category category2 = new Category();
		category2.setName("system");
		category2.getFilter().add(filter2);
		
		Cause cause5 = new Cause();
		cause5.setKeyword("key5");
		
		Cause cause6 = new Cause();
		cause6.setKeyword("key6");
		
		Filter filter3 = new Filter();
		filter3.setName("filter3");
		filter3.getCause().add(cause5);
		filter3.getCause().add(cause6);

		category2.getFilter().add(filter3);		

		
		filters.getCategory().add(category1);
		filters.getCategory().add(category2);
		File file = new File("target/category-filter.xml");
		if (file.exists()) {
			file.delete();
		}
		FilterFactory factory = new MavenBuildFilterFactory();
		factory.marshal(file, filters);
		
		assertTrue(file.exists());
		
		Filters result = factory.unmarshal(file);
		
		assertNotNull(result);
		
		assertEquals(2, result.getCategory().size());
		assertEquals(2, result.getCategory().get(1).getFilter().size());
		assertEquals("key6", result.getCategory().get(1).getFilter().get(1).getCause().get(1).getKeyword());
	}
	
	@Test
	public void testRemoteFilter() {
		URL remoteFilter = null;
		try {
			remoteFilter = new URL(MavenBuildFilterFactory.FILTER_LIST_IN_GIT);
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}
		FilterFactory factory = new MavenBuildFilterFactory();
		List<Filter> filters = factory.build(remoteFilter, null);
		assertTrue(filters.size() > 0);
	}
	
	@Test
	public void testLocalFilter() {
		URL localFilter = getClass().getClassLoader().getResource("test-filter.xml");
		FilterFactory factory = new MavenBuildFilterFactory();
		List<Filter> filters = factory.build(null, localFilter);
		assertTrue(filters.size() > 0);
	}
}
