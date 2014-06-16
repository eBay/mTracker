package com.ebay.build.core.readers;

import com.ebay.build.core.model.Session;

public interface ReaderProcessor {
	Session process(String payload);
}
