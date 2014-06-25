package com.ccoe.build.tracking;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.tracking.jdbc.DurationObject;
import com.ccoe.build.tracking.jdbc.RawDataJDBCTemplate;
import com.ccoe.build.tracking.jdbc.SessionJDBCTemplate;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BatchUpdateDurationJob implements Job {

	private ApplicationContext context = null;
    private final SessionJDBCTemplate sessionJDBCTemplate;
    private final RawDataJDBCTemplate rawJDBCTemplate;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy 00:00");    

	public BatchUpdateDurationJob() {
		 context = new ClassPathXmlApplicationContext("tracking-spring-jdbc-config.xml");
         sessionJDBCTemplate = (SessionJDBCTemplate) context.getBean("sessionJDBCTemplate");
         rawJDBCTemplate = (RawDataJDBCTemplate) context.getBean("rawDataJDBCTemplate");
	}
	
	public void execute(JobExecutionContext context) {
		System.out.println("[INFO] " + new Date() + " Start executing BatchUpdateDurationJob...");
        long startRunningTime = System.currentTimeMillis();

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -5);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 1);


        String startDateString = dateFormatter.format(startDate.getTime());
        String endDateString = dateFormatter.format(endDate.getTime());

        System.out.println("Date Range: " + startDateString + "   ~   " + endDateString);

        updateRaptorAssemblerDuration(startDateString, endDateString);
        updateRaptorBuildDuration(startDateString, endDateString);

        System.out.println("[INFO] Total Time: " + (System.currentTimeMillis() - startRunningTime) + " ms.");
        System.out.println("[INFO] End executing BatchUpdateDurationJob...");
	}
	
	private String getBuildSQLClaus(String startDateString, String endDateString) {
		return " status = 0 and duration_build is null and duration_download is null "
                + " and start_time > to_date('" + startDateString + "', 'DD-Mon-YY HH24:Mi') "
                + " and start_time < to_date('" + endDateString + "', 'DD-Mon-YY HH24:Mi')";
	}
	
	private String getAssemblerSQLClaus(String startDateString, String endDateString) {
		return " status = 0 and duration_build is null and duration_download is null "
                + " and start_time > to_date('" + startDateString + "', 'DD-Mon-YY HH24:Mi') "
                + " and start_time < to_date('" + endDateString + "', 'DD-Mon-YY HH24:Mi')";
	}	
	
	private void updateRaptorBuildDuration(String startDateString, String endDateString) {
		System.out.println("===== Batch updating build duration =====");
		updateDuration(getBuildSQLClaus(startDateString, endDateString), 
				"select plugin_key from rbt_plugin_count_in");
	}
	
	private void updateRaptorAssemblerDuration(String startDateString, String endDateString) {
		System.out.println("===== Batch updating assembler duration =====");
		updateDuration(getAssemblerSQLClaus(startDateString, endDateString), 
				"'com.ccoe.devex.assembler:assembler-maven-plugin', 'com.ccoe.raptor.build:assembler-maven-plugin'");
	}
	
	private void updateDuration(String sqlClaus, String pluginKeyList) {
		List<Session> sessions = new ArrayList<Session>();
        try {
        	sessions = sessionJDBCTemplate.getSessionWithoutDod(sqlClaus);
        } catch (Exception e) {
        	System.out.println("[ERROR] No sessions fetched due to: " + e.getMessage());
        }
        System.out.println("Loaded " + sessions.size() + " sessions");
        
        if (sessions.size() == 0) {
        	return;
        }
		String sessionsSQL = "select id from RBT_SESSION " + " where " + sqlClaus;

        Map<Integer, Integer> totalPluginDurationMap = rawJDBCTemplate.getMapTotalDuration(sessionsSQL);
        Map<Integer, Integer> totalIncludedPluginDurationMap = rawJDBCTemplate.getMapIncludedPluginDuration(sessionsSQL, pluginKeyList);

        System.out.println(totalPluginDurationMap);
        System.out.println(totalIncludedPluginDurationMap);

        List<DurationObject> durations = new ArrayList<DurationObject>();

        for (Session session : sessions) {
            long downloadDuration = 0;
            if (totalPluginDurationMap.get(session.getId()) != null) {
                downloadDuration = session.getDuration() - totalPluginDurationMap.get(session.getId());
            }

            long buildDuration = 0;
            if (totalIncludedPluginDurationMap.get(session.getId()) != null) {
            	buildDuration = totalIncludedPluginDurationMap.get(session.getId());
            } 

            System.out.println("Updating " + session.getId() + "  --> " );
            System.out.println("                  download duration    " + downloadDuration);
            System.out.println("                  build duration    " + buildDuration);

            DurationObject d1 = new DurationObject();
            d1.setSessionId(session.getId());
            d1.setDownloadDuration((int) downloadDuration);
            d1.setBuildDuration((int) buildDuration);

            durations.add(d1);
        }
        if (!durations.isEmpty()) {
        	System.out.println("============ batch updated: " + durations.size());
        	sessionJDBCTemplate.batchUpdateDuration(durations);
        }				
	}

    public static void main(String[] args) {
        BatchUpdateDurationJob job = new BatchUpdateDurationJob();
        job.execute(null);
    }
}
