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

package com.ccoe.build.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Generator {
	
	private static final String ALGORITHM = "MD5";
	private static final int BUFFER_SIZE = 1024;

	public static String generateMd5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMD5Checksum(File file) throws IOException  {
		String md5Checksum = null;
		try {
			md5Checksum = createMessageDisgestChecksum(file, ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// Do nothing.. never happens unless the algorithm changes in the JDK 
			System.err.println("No valid implementation found for alogrithm -> \"" + ALGORITHM + "\"" + e.getMessage());
		}
		return md5Checksum;
	}
	
	
	public static String createMessageDisgestChecksum(File file, String algorithm) throws IOException, NoSuchAlgorithmException  {
		byte[] b = createChecksum(file, algorithm);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}
	
	private static byte[] createChecksum(File file, String algorithm) throws IOException, NoSuchAlgorithmException {		
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			MessageDigest complete = MessageDigest.getInstance(algorithm);
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}

			} while (numRead != -1);
			return complete.digest();
		} finally {
			if(fis != null) {
				fis.close();
			}
		}
	}
}
