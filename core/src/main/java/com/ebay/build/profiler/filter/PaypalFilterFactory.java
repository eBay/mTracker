package com.ebay.build.profiler.filter;

public class PaypalFilterFactory extends RideFilterFactory{
	public PaypalFilterFactory(){
		FILTER_LIST_FILE = "/paypal-filters.xml";
		FILTER_LIST_IN_GIT = BASE_FILTER_LIST_IN_GIT + FILTER_LIST_FILE;
	}
}
