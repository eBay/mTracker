package com.ebay.build.service.track;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.ebay.build.utils.ServiceConfig;

@Path("")
public class QueueService {

    private final SimpleDateFormat actionDatePattern = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss-SSS");
    private final File queueRoot = new File(ServiceConfig.get("queue_root_dir"));
    
    public QueueService() {
        if (!queueRoot.exists()) {
            System.out.println("Making queue root: " + queueRoot);
            queueRoot.mkdirs();
        }
    }
    
    @POST
    @Path("/queue/{appname}/{jobname}")
    @Produces(MediaType.TEXT_PLAIN)
    public String post(@HeaderParam("Content-Type") String contentType, @PathParam("appname") String appName,
            @PathParam("jobname") String jobName,
            String body) {
        System.out.println("in queue service....");
        File appQueueFolder = new File(queueRoot, appName);
        if (!appQueueFolder.exists()) {
            System.out.println("Making application queue root: " + appQueueFolder);
            appQueueFolder.mkdirs();
        }
        String extension = ".txt";
        if (contentType != null && contentType.contains("/")) {
            extension = "." + contentType.split("/")[1];
        }
        String resultFileName = jobName + "_" + actionDatePattern.format(new Date()) + extension;
        File resultFile = new File(appQueueFolder, resultFileName);
        
        try {
            FileUtils.writeStringToFile(resultFile, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appName + "/" + jobName + " queued @ " + getHostName() + " on folder: " + resultFile;
    }
    
    @POST
    @Path("/queue/{appname}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(final FormDataMultiPart multiPart, @PathParam("appname") String appName) {
        
        File appQueueFolder = new File(queueRoot, appName);
        if (!appQueueFolder.exists()) {
            System.out.println("Making application queue root: " + appQueueFolder);
            appQueueFolder.mkdirs();
        }
        
        List<String> files = new ArrayList<String>();
        
        List<BodyPart> parts = multiPart.getBodyParts();
        for (BodyPart bodyPart : parts) {
            if (bodyPart instanceof FormDataBodyPart) {
                FormDataBodyPart part = (FormDataBodyPart) bodyPart;
                String fileName = part.getFormDataContentDisposition()
                        .getFileName();
                
                
                
                File resultFile = new File(appQueueFolder, fileName);
                if (resultFile.exists()) {
                    System.err
                            .println(fileName+" already upLoaded? Same file found in queue root: "
                            + appQueueFolder);
                } else {
                    
                    File uploadedTemp = part.getEntityAs(File.class);
                    try {
                        FileUtils.copyFile(uploadedTemp, resultFile);
                        FileUtils.deleteQuietly(uploadedTemp);
                        
                        files.add(fileName);
                    } catch (IOException ex) {
                        Logger.getLogger(QueueService.class.getName()).log(
                                Level.SEVERE,
                                "Cannot move: " + uploadedTemp + " to: "
                                + appQueueFolder, ex);
						System.err.println("Failed to process upload file: "
								+ fileName);
                    }
                }
            }
        }
		return "Files: " + files + " queued @ " + getHostName()
				+ " in folder: " + appQueueFolder;
    }
    
    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "";
    }    
}
