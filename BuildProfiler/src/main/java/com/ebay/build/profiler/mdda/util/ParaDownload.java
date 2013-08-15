package com.ebay.build.profiler.mdda.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.ebay.build.profiler.mdda.bean.DArtifact;

public class ParaDownload {
	
    public static void threaddownload(List<DArtifact> artifacts) {
        
  
       int size = artifacts.size();
       int downloadcount = 0;
       while(downloadcount < size){		
    	    	     
    	   if(MyDownThread2.activeCount() < 50){
       			
       				MyDownThread2 md = new MyDownThread2(artifacts.get(downloadcount));
       				
       				downloadcount++;
       				
       				md.start();
       				 
       		}
       }
       while(MyDownThread2.activeCount() != 3){
    	   
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
	            
	            File localfile = artifact.generateFilePath();
	            
	            FileOutputStream out = new FileOutputStream(localfile);  
	             
	            byte[] b = new byte[1024];  
	            
	            int len = 0;  
	            
	            while((len = in.read(b))!= -1){  
	                out.write(b,0,len);  
	            }  
	            
	            in.close();  
	            
	            out.close(); 
	            
	            long size = localfile.length();
	         
	            if(!artifact.artifactOK(size)){
	            	localfile.delete();
	            }
	               
	        }catch (Exception e){  
	            e.printStackTrace();  
	        }finally{  
	            httpGet.releaseConnection();  
	        } 
	        
		}
    }
}