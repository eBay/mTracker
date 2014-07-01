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

package com.ccoe.build.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author yiqushen
 * 
 */

public class CompressUtils {

	private final static int BUFFER = 2048;	
	
	/**
	 * Compress files to a zip
	 * @param zip
	 * @param files
	 * @throws IOException 
	 */
	public static void compressToZip(File zip, File[] files) throws IOException {
		byte data[] = new byte[BUFFER];
		FileOutputStream fozip = new FileOutputStream(zip);
		ZipOutputStream zo = new ZipOutputStream(
				new BufferedOutputStream(fozip));
		for (int i = 0; i < files.length; i++) {
			System.out.println("Adding:" + files[i]);
			FileInputStream fi = new FileInputStream(files[i]);
			BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry zipentry = new ZipEntry(files[i].getName());
			zo.putNextEntry(zipentry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				zo.write(data, 0, count);
			}
			origin.close();
		}
		zo.close();
	}

	/**
	 * Compress files to a zip, and includes other operations such as filter,clean files
	 * 
	 * @param zip
	 * @param directory
	 * @param filter
	 * @throws IOException 
	 */
	public static void compress(String zip, String directory,
			FilenameFilter filter) throws IOException {

		File fzip = new File(zip);
		File fdir = new File(directory);
		File files[] = fdir.listFiles(filter);

		if (files.length <= 0) {
			cleanZip(fzip);
			return;
		} else {
			compressToZip(fzip, files);
			cleanFiles(files);
		}
	}


	/**
	 * Uncompress a zip to files
	 * @param zip
	 * @param unzipdir
	 * @param isNeedClean
	 * @return
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public static List<File> unCompress(File zip, String unzipdir) throws IOException  {
		ArrayList<File> unzipfiles = new ArrayList<File>();

		FileInputStream fi = new FileInputStream(zip);
		ZipInputStream zi = new ZipInputStream(new BufferedInputStream(fi));
		try {

			ZipEntry entry;
			while ((entry = zi.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				File unzipfile = new File(unzipdir + File.separator
						+ entry.getName());
				FileOutputStream fos = new FileOutputStream(unzipfile);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
				
				while ((count = zi.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				
				unzipfiles.add(unzipfile);
			}
			
		} catch (IOException e) {
			throw e;
		}finally{
			IOUtils.closeQuietly(zi);
		}
		
		

		return unzipfiles;
	}
	 
	 
	/**
	 * @param dir
	 * @param filter
	 */
   static void cleanFiles(File[] files) {
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	/**
	 * @param file
	 */
   static void cleanZip(File file) {
		file.delete();
	}

}
