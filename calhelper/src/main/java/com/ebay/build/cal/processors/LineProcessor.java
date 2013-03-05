package com.ebay.build.cal.processors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ebay.build.cal.model.Machine;
import com.ebay.build.cal.model.Pool;
import com.ebay.build.cal.model.Project;
import com.ebay.build.cal.model.Session;
import com.ebay.build.cal.query.utils.StringUtils;

public class LineProcessor {

	public List<Session> process(String payload) {
		List<Session> sessions = new ArrayList<Session>();
		
		List<String> lines = Arrays.asList(payload.split("\n"));
		Session session = null;
		for (String line : lines) {
			 
			if ((session = newSession(line)) != null) {
				sessions.add(session);
				continue;
			}
			
			if (sessionStart(line, session)) {
				continue;
			}
			
			if (transactionStart(line, session)) {
				continue;
			}
			
			if (transactionEnd(line, session)) {
				continue;
			}
			
			if (projectStart(line, session)) {
				continue;
			}
			
			if (projectEnd(line, session)) {
				continue;
			}
		}
		return sessions;
	}
	
	protected boolean projectEnd(String line, Session session) {
		String prjPattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s*Project\\s+(.*)\\s+(\\d+)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(line, prjPattern, false);
		System.out.println(found);
		if (found.size() == 5) {
			String id = found.get(0);
			String name = found.get(1);
			String status = found.get(2);
			String duration = found.get(3);
			String payload = found.get(4);
			
			String key = id + "-" + name;
			
			Project project = session.getProjects().get(key);
			if (project == null) {
				// TODO : throw exception here, the transaction should end?
				return false;
			}
			project.setDuration(Long.parseLong(duration));
			project.setStatus(status);
			// TODO: parsePayload here
			praseProjectPayload(payload);
			return true;
		}
		return false;
	}
	
	private void praseProjectPayload(String payload) {
		// TODO Auto-generated method stub
		
	}

	protected boolean projectStart(String line, Session session) {
		String prjPattern = "(\\d+)\\s+t(\\d{2}:\\d{2}:\\d{2})\\.\\d+\\s*Project\\s+(.*)";
		
		List<String> found = StringUtils.getFound(line, prjPattern, false);
		
		if (found.size() == 3) {
			String id = found.get(0);
			String timeString = found.get(1);
			String name= found.get(2);
			
			String key = id + "-" + name;
			Project project = session.getProjects().get(key);
				
			if (project == null) {
				project = new Project();
				session.getProjects().put(key, project);
			}

			project.setName(name);
			project.setStartTime(StringUtils.setTime(session.getStartTime(), timeString));
			return true;
		}
		return false;
	}
	
	protected boolean transactionEnd(String line, Session session) {
		String tEndPattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s+URL\\s+\\w+\\s+(\\d+)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(line, tEndPattern, false);
		
		if (found.size() == 4) {
			String id = found.get(0);
			String status = found.get(1);
			String duration = found.get(2);
			String payload = found.get(3);
		
			if ("0".equals(id)) {
				session.setDuration(Long.parseLong(duration));
				session.setStatus(status);
				parseSessionPayLoad(payload, session);
				return true;
			}
		}
		
		return false;
	}

	private void parseSessionPayLoad(String payload, Session session) {
		// TODO Auto-generated method stub
	}

	protected boolean transactionStart(String line, Session session) {
		String tStartPattern = "(\\d+)\\s+t\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s+URL\\s+(\\w+)";
		// 0  t17:41:38.58	URL	RIDE
		List<String> found = StringUtils.getFound(line, tStartPattern, false);
		
		if (found.size() == 2) {
			String id = found.get(0);
			String env = found.get(1);
			
			if ("0".equals(id) && !StringUtils.isEmpty(env)) {
				session.setEnvironment(env);
				return true;
			}
		}
		return false;
	}
	
	protected boolean sessionStart(String line, Session session) {
		if (line.startsWith("Start:")) {
			String timeString = StringUtils.getFirstFound(line, "Start:\\s(\\d{2}-\\d{2}-\\d{4}\\s\\d{2}:\\d{2}:\\d{2})", false);
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date date = new Date();
			try {
				date = dateFormatter.parse(timeString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			session.setStartTime(date);
			return true;
		}
		return false;
	}

	protected Session newSession(String line) {
		if (line == null) {
			return null;
		}
		
		if (!line.startsWith("SQLLog for")) {
			return null;
		}
		
		Session session = new Session();

		String poolName = StringUtils.getFirstFound(line, "SQLLog for (.*)-MavenBuild", false);
		String machineName = StringUtils.getFirstFound(line, "-MavenBuild:(.*)", false);
		
		Pool pool = new Pool();
		pool.setName(poolName);
		
		Machine machine = new Machine();
		machine.setName(machineName);
		machine.setPool(pool);
		
		pool.setMachine(machine);
		
		session.setPool(pool);
		
		return session;
	}
}
