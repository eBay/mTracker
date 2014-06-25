package com.ccoe.build.profiler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class GitUtil {
	
	public static File findGitRepository( File currentDir ) {

		File gitFolder=null;
		File workDir = currentDir;
		
		while( workDir != null && workDir.exists() ){
			if( isValidGitDir(workDir) ){
				gitFolder = workDir;
				break;
			}else{
				workDir = workDir.getParentFile();
			}
		}
		
		return gitFolder;
	}
	
	
	private static boolean isValidGitDir( File dir ){
		boolean result = false;
		
		File gitDir = new File( dir, ".git");
		
		if( gitDir.exists() ){
			if( gitDir.isDirectory() ){
				if( gitDir.canRead() ){
					result = true;
				}
			}
		}
		
		return result;
	}
	
	public static String getRepoName(File gitConfigFile) {
		String repourl=null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(gitConfigFile));
			String line = "";
			while((line = br.readLine()) != null) {
				//System.out.println(line);
				if(line.trim().startsWith("url")) {
					//System.out.println("Inisde IF");
					repourl = line.split("=")[1].trim();
				}
			}
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
		
		return repourl;
	}

}
