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

package com.ccoe.build.profiler.util;

public class Timer {

	public static final int MS_PER_SEC = 1000;
	public static final int SEC_PER_MIN = 60;

	private long start;
	private long elaspsedTime;

	public Timer() {
		start = System.currentTimeMillis();
	}

	public void stop() {
		elaspsedTime = elapsedTime();
	}

	public long getTime() {
		return elaspsedTime;
	}
	
	public long getStartTime() {
		return this.start;
	}

	private long elapsedTime() {
		return System.currentTimeMillis() - start;
	}

	public static String formatTime(long ms) {
		long secs = ms / MS_PER_SEC;
		long mins = secs / SEC_PER_MIN;
		secs = secs % SEC_PER_MIN;
		long fractionOfASecond = ms - (secs * 1000);

		String msg = mins + "m " + secs + "." + fractionOfASecond;

		if (msg.length() == 3) {
			msg += "00s";
		} else if (msg.length() == 4) {
			msg += "0s";
		} else {
			msg += "s";
		}
		
		msg = ms + " ms ( " + msg + " )"; 

		return msg;
	}
}
