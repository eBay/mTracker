package com.ebay.build.profiler.mdda.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.ebay.build.profiler.mdda.bean.DArtifact;

public class ParaDownload {
	
    public static void threaddownload(List<DArtifact> artifacts) {
        
  
       int size = artifacts.size();
       int threadnum = 0;
       int downloadcount = 0;
       while(downloadcount < size){		
    	   
    	   threadnum = MyDownThread.activeCount();
    	   
    	   if(threadnum < 50){
       			
       				MyDownThread md = new MyDownThread(artifacts.get(downloadcount));
       				
       				downloadcount++;
       				
       				md.start();
       				 
       		}
       }
    }

    
    
    static class MyDownThread extends Thread{
    	 

        
        private DArtifact artifact;
        
        public MyDownThread(DArtifact a){
     	   this.artifact = a;
        }
   
      
        @Override
 		public void run() {

 			int byteread = 0;

 			URL url;
 			File targetFile;

 			try {
 				url = new URL(this.artifact.getQuick_url());

 				targetFile = artifact.generateFilePath();
 				
 				URLConnection conn = url.openConnection();
 				InputStream inStream = conn.getInputStream();
 				FileOutputStream fs = new FileOutputStream(targetFile);
 				byte[] buffer = new byte[1024];

 				while ((byteread = inStream.read(buffer)) != -1) {
 					fs.write(buffer, 0, byteread);
 				}
 				inStream.close();
 				fs.close();

 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 		}
     }
    
    static class MyDownThread2 extends Thread{
 

       
       private DArtifact artifact;
       
       public MyDownThread2(DArtifact a){
    	   this.artifact = a;
       }
  
     
       @Override
		public void run() {

    	   HttpClient client = new HttpClient();  
	       
	       GetMethod httpGet = new GetMethod(artifact.getQuick_url());  
	        try {  
	            client.executeMethod(httpGet);  
	              
	            InputStream in = httpGet.getResponseBodyAsStream();  
	             
	            FileOutputStream out = new FileOutputStream(artifact.generateFilePath());  
	             
	            byte[] b = new byte[1024];  
	            
	            int len = 0;  
	            
	            while((len=in.read(b))!= -1){  
	                out.write(b,0,len);  
	            }  
	            
	            in.close();  
	            out.close();  
	              
	        }catch (Exception e){  
	            e.printStackTrace();  
	        }finally{  
	            httpGet.releaseConnection();  
	        } 
	        
		}
    }
}