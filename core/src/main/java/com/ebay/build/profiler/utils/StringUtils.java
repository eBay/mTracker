package com.ebay.build.profiler.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}
	
	public static List<String> getFound(String contents, String regex,
			boolean isCaseInsensitive) {
		if (isEmpty(regex) || isEmpty(contents)) {
			return new ArrayList<String>();
		}
		List<String> results = new ArrayList<String>();
		Pattern pattern;
		if (isCaseInsensitive) {
			pattern = Pattern.compile(regex, Pattern.UNICODE_CASE
					| Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
		}
		Matcher matcher = pattern.matcher(contents);
		while (matcher.find()) {
			if (matcher.groupCount() > 0) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					if (!isEmpty(matcher.group(i))) {
						results.add(matcher.group(i));
					}
				}
			} else {
				if (!isEmpty(matcher.group())) {
					results.add(matcher.group());
				}
			}
		}
		return results;
	}

	public static String getFirstFound(String contents, String regex,
			boolean isCaseInsensitive) {
		if (isEmpty(contents) || isEmpty(regex)) {
			return null;
		}
		List<String> found = getFound(contents, regex, isCaseInsensitive);
		if (found.size() > 0) {
			return (String) found.iterator().next();
		}
		return "";
	}
	
	public static Date setTime(Date date, String timeString) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		
		Calendar cal2 = Calendar.getInstance();

		String[] time = timeString.split(":");
		
		cal2.set(cal1.get(Calendar.YEAR), 
				cal1.get(Calendar.MONTH), 
				cal1.get(Calendar.DAY_OF_MONTH), 
				Integer.parseInt(time[0]),
				Integer.parseInt(time[1]),
				Integer.parseInt(time[2]));
		
		return cal2.getTime();
	}
	
	public static String readFile(String fileName, String characterEncoding)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
			FileInputStream fis;
			InputStreamReader isr;
			StringBuffer sb = new StringBuffer(75000);
			char[] buf = new char[4096];
			int numRead;
			fis = new FileInputStream(fileName);
			isr = new InputStreamReader(fis, characterEncoding);
			do {
				numRead = isr.read(buf, 0, buf.length);
				if (numRead > 0) {
					sb.append(buf, 0, numRead);
				}
			} while (numRead >= 0);
			isr.close();
			fis.close();
			return sb.toString();
	}
}
