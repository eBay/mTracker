package com.ccoe.build.core.readers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ccoe.build.core.model.Phase;
import com.ccoe.build.core.model.Plugin;
import com.ccoe.build.core.model.Project;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.utils.StringUtils;

public class LineProcessor implements ReaderProcessor {

	public Session process(String payload) {
		List<Session> sessions = new ArrayList<Session>();
		
		List<String> lines = Arrays.asList(payload.split("\n"));
		Session session = null;
		
		boolean hasException = false;
		
		for (String line : lines) {
			 
			if (skipLine(line)) {
				continue;
			}
			
			if ((session = newSession(line)) != null) {
				sessions.add(session);
				continue;
			} else {
				if (!sessions.isEmpty()) {
					session = sessions.get(sessions.size() - 1);
				} else {
					// no sessions left, should end the process.
					break;
				}
			}
			
			if (catchException(line, session)) {
				hasException = !hasException;
				continue;
			}
			
			if (hasException) {
				session.addException(line);
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
			
			if (phaseStart(line, session)) {
				continue;
			}
			
			if (phaseEnd(line, session)) {
				continue;
			}
			if (pluginAtom(line, session)) {
				continue;
			}
			if (sessionEnd(line, session)) {
				continue;
			}
		}
		if (sessions.size() > 0) {
			return sessions.get(0);
		}
		return null;
	}
	
	private boolean catchException(String line, Session session) {
		return line.trim().equals("-----------------EXCEPTION MESSAGE-----------------");
	}

	protected boolean sessionEnd(String line, Session session) {
		List<String> found = StringUtils.getFound(line, "\\d+\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\s+URL\\s+Session\\s+(.*)\\s+(\\d+)\\s+\\[(.*)\\]", false);
		if (found.size() == 3) {
			session.setGoals(found.get(2));
			return true;
		}
		return false;
	}

	protected boolean skipLine(String line) {
		if (StringUtils.isEmpty(line)) {
			return true;
		}
		String[] patterns = new String[]{"\\s+------",
				"Label:\\s+unset;",
				"Environment:\\s+"};
		
		for (int i = 0; i < patterns.length; i++) {
			if (!StringUtils.isEmpty(StringUtils.getFirstFound(line, patterns[i], false))) {
				return true;
			}
		}
		
		return false;
	}

	protected boolean pluginAtom(String line, Session session) {
		String phasePattern = "(\\d+)\\s+A(\\d{2}:\\d{2}:\\d{2})\\.\\d+\\s*Plugin\\s+(.*)\\s+(\\d+)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(line, phasePattern, false);
		
		if (found.size() == 6) {
			//String id = found.get(0);
			String timeString = found.get(1);
			String name = found.get(2);
			String status = found.get(3);
			String duration = found.get(4);
			String payload = found.get(5);
			
			Plugin plugin = new Plugin();
			plugin.setPluginKey(name);
			String[] gav = name.split(":");
			plugin.setGroupId(gav[0]);
			plugin.setArtifactId(gav[1]);
			plugin.setVersion(gav[2]);
			
			plugin.setDuration(Long.parseLong(duration));
			plugin.setStartTime(StringUtils.setTime(session.getStartTime(), timeString));
			plugin.setStatus(status);
			plugin.setPayload(payload);
			
			Phase phase = session.getCurrentProject().getLastPhase();
			phase.getPlugins().add(plugin);
			
			plugin.setPhaseName(phase.getName());
			// TODO parse playload
			return true;
		}
		return false;
	}

	protected boolean phaseEnd(String line, Session session) {
		String phasePattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s*Phase\\s+(.*)\\s+(.*)\\s+(\\d+)";
		List<String> found = StringUtils.getFound(line, phasePattern, false);
		if (found.size() == 4) {
			//String id = found.get(0);
			String name = found.get(1);
			String status = found.get(2);
			String duration = found.get(3);
			
			Phase phase = session.getCurrentProject().getLastPhase();
			
			if (!name.equals(phase.getName())) {
				return false;
			}
			phase.setDuration(Long.parseLong(duration));
			phase.setStatus(status);
			
			return true;
		}
		return false;
	}

	protected boolean phaseStart(String line, Session session) {
		String phasePattern = "(\\d+)\\s+t(\\d{2}:\\d{2}:\\d{2})\\.\\d+\\s*Phase\\s+(.*)";
		List<String> found = StringUtils.getFound(line, phasePattern, false);
		
		if (found.size() == 3) {
			//String id = found.get(0);
			String timeString = found.get(1);
			String name = found.get(2).trim();
			
			Phase phase = new Phase();
			phase.setName(name);
			
			phase.setStartTime(StringUtils.setTime(session.getStartTime(), timeString));

			Project project = session.getCurrentProject();
			project.getPhases().add(phase);
			return true;
		}
		return false;
	}

	protected boolean projectEnd(String line, Session session) {
		String prjPattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s*Project\\s+(.*)\\s+(.*)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(line, prjPattern, false);
		if (found.size() == 5) {
			//String id = found.get(0);
			String name = found.get(1);
			String status = found.get(2);
			String duration = found.get(3);
			String payload = found.get(4);
			
			Project project = session.getProjects().get(name);
			if (project == null) {
				// TODO : throw exception here, the transaction should end?
				return false;
			}
			project.setDuration(Long.parseLong(duration));
			project.setStatus(status);
			//project.setPool(session.getPool());
			
			// TODO: parsePayload here
			project.setPayload(payload);
			ProcessHelper.praseProjectPayload(payload, project);
			return true;
		}
		return false;
	}

	protected boolean projectStart(String line, Session session) {
		String prjPattern = "(\\d+)\\s+t(\\d{2}:\\d{2}:\\d{2})\\.\\d+\\s*Project\\s+(.*)";
		
		List<String> found = StringUtils.getFound(line, prjPattern, false);
		
		if (found.size() == 3) {
			//String id = found.get(0);
			String timeString = found.get(1);
			String name= found.get(2).trim();
			
			Project project = session.getProjects().get(name);
				
			if (project == null) {
				project = new Project();
				session.getProjects().put(name, project);
			}

			project.setName(name);
			project.setStartTime(StringUtils.setTime(session.getStartTime(), timeString));
			
			session.setCurrentProject(project);
			return true;
		}
		return false;
	}
	
	protected boolean transactionEnd(String line, Session session) {
		String tEndPattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s+Environment\\s+\\w+\\s+(\\d+)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(line, tEndPattern, false);
		
		if (found.size() == 4) {
			String id = found.get(0);
			String status = found.get(1);
			String duration = found.get(2);
			String payload = found.get(3);
		
			if ("0".equals(id)) {
				session.setDuration(Long.parseLong(duration));
				session.setStatus(status);
				ProcessHelper.parseSessionPayLoad(payload, session);
				return true;
			}
		}
		
		return false;
	}

	protected boolean transactionStart(String line, Session session) {
		String tStartPattern = "(\\d+)\\s+t\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s+Environment\\s+(\\w+)";
		// 0  t17:41:38.58	Environment	RIDE
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
		if (StringUtils.isEmpty(StringUtils.getFirstFound(line, "\\d*\\s*SQLLog for", false))) {
			return null;
		}
		
		Session session = new Session();

		List<String> found = StringUtils.getFound(line, "\\d*\\s*SQLLog\\sfor\\s+(.*)-MavenBuild:(.*)", false);
		
		//Pool pool = new Pool();
		//pool.setName(found.get(0));
		
		session.setAppName(found.get(0));
		session.setMachineName(found.get(1));
		
//		Machine machine = new Machine();
//		machine.setName(found.get(1));
//		machine.setPool(pool);
		
		//pool.setMachine(machine);
		
		//session.setPool(pool);
		
		return session;
	}
}
