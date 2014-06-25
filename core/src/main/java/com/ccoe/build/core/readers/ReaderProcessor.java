package com.ccoe.build.core.readers;

import com.ccoe.build.core.model.Session;

public interface ReaderProcessor {
	Session process(String payload);
}
