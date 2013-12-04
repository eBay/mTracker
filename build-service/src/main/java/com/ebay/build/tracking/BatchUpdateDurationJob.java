package com.ebay.build.tracking;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.tracking.jdbc.DurationObject;
import com.ebay.build.tracking.jdbc.RawDataJDBCTemplate;
import com.ebay.build.tracking.jdbc.SessionJDBCTemplate;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
		System.out.println("Executing BatchUpdateDuration...");
        long startRunningTime = System.currentTimeMillis();

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -3);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 1);


        String startDateString = dateFormatter.format(startDate.getTime());
        String endDateString = dateFormatter.format(endDate.getTime());

        System.out.println("Date Range: " + startDateString + "   ~   " + endDateString);

        List<Session> sessions = sessionJDBCTemplate.getSessionWithoutDod(startDateString, endDateString);
        System.out.println("Loaded " + sessions.size() + " sessions");

        String sessionsSQL = "select id from RBT_SESSION " + " where status = 0 and duration_build is null and duration_download is null "
                + " and start_time > to_date('" + startDateString + "', 'DD-Mon-YY HH24:Mi') "
                + " and start_time < to_date('" + endDateString + "', 'DD-Mon-YY HH24:Mi')";

        Map<Integer, Integer> totalPluginDurationMap = rawJDBCTemplate.getMapTotalDuration(sessionsSQL);
        Map<Integer, Integer> totalExcludedPluginDurationMap = rawJDBCTemplate.getMapExcludedPluginDuration(sessionsSQL);

        System.out.println(totalPluginDurationMap);
        System.out.println(totalExcludedPluginDurationMap);

        List<DurationObject> durations = new ArrayList<DurationObject>();

        for (Session session : sessions) {
            int totalPluginDuration = 0;
            long downloadDuration = 0;
            if (totalPluginDurationMap.get(session.getId()) != null) {
                totalPluginDuration = totalPluginDurationMap.get(session.getId());
                downloadDuration = session.getDuration() - totalPluginDuration;
            }

            int totalExcludedPluginDuration = 0;
            long buildDuration = 0;
            if (totalExcludedPluginDurationMap.get(session.getId()) != null) {
                totalExcludedPluginDuration = totalExcludedPluginDurationMap.get(session.getId());
                buildDuration = session.getDuration() - totalExcludedPluginDuration;
            }

            System.out.println("Updating " + session.getId() + "  --> " );
            System.out.println("                  download duration    " + session.getDuration() + " - " + totalPluginDuration + " = " + downloadDuration);
            System.out.println("                  build duration    " + session.getDuration() + " - " + totalExcludedPluginDuration + " = " + buildDuration);

            DurationObject d1 = new DurationObject();
            d1.setSessionId(session.getId());
            d1.setDownloadDuration((int) downloadDuration);
            d1.setBuildDuration((int) buildDuration);

            durations.add(d1);
        }

        System.out.println("============ batch updated: " + durations.size());
        sessionJDBCTemplate.batchUpdateDuration(durations);
        System.out.println("[INFO] Total Time: " + (System.currentTimeMillis() - startRunningTime) + " ms.");
	}

    public static void main(String[] args) {
        BatchUpdateDurationJob job = new BatchUpdateDurationJob();
        job.execute(null);
    }
}
