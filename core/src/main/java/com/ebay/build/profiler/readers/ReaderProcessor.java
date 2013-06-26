package com.ebay.build.profiler.readers;

import com.ebay.build.profiler.model.Session;

public interface ReaderProcessor {
	Session process(String payload);
}
