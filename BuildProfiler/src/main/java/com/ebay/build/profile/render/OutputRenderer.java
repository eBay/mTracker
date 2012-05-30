package com.ebay.build.profile.render;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.ebay.build.profile.util.Timer;
import com.ebay.build.profiler.profile.MojoProfile;
import com.ebay.build.profiler.profile.PhaseProfile;
import com.ebay.build.profiler.profile.ProjectProfile;
import com.ebay.build.profiler.profile.SessionProfile;
import com.google.gson.Gson;

public class OutputRenderer {
	
	SessionProfile sessionProfile;
	
	public OutputRenderer(SessionProfile profile) {
		this.sessionProfile = profile;
	}
	
	public void renderToScreen() {
		for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
		      render(pp.getProjectName());
		      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
		        render("  " + phaseProfile.getPhase() + " " + Timer.formatTime(phaseProfile.getElapsedTime()));
		        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
		          render("    " + mp.getId() + Timer.formatTime(mp.getElapsedTime())); 
		        }
		      }
		      render("");
		    }
	}
	
	public void renderToHTML() {
		
	}
	
	public void renderToJSON() {
		ProfileOutputJSON json = new ProfileOutputJSON();
		Map<String, Long> projects = json.getTimingSlice();
		Map<String, LinkedList<Long>> plugin = json.getPlugin();
		int prjCnt = 0;
		for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
		      projects.put(pp.getProjectName(), pp.getElapsedTime());
		      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
		        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
		        	String key = mp.getId() + " (" + phaseProfile.getPhase() + ")";
		        	
		        	if(plugin.containsKey(key)) {
		        		LinkedList<Long> linkedList = plugin.get(key);
		        		linkedList.add(mp.getElapsedTime());
		        	} else {
		        		LinkedList<Long> value = new LinkedList<Long>();
		        		for(int i=0;i<prjCnt;i++) {
		        			value.add(0L);
		        		}
		        		value.add(mp.getElapsedTime());
		        		plugin.put(key , value);
		        	}
		        }
		      }
		      prjCnt++;
		      for(Entry<String,LinkedList<Long>> data : plugin.entrySet()) {
		    	  if(data.getValue().size() < prjCnt) {
		    		  plugin.get(data.getKey()).add(0L);
		    	  }
		      }
		    }
		json.setTimingSlice(projects);
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(json));
		
	}
	
	private void render(String s) {
	    System.out.println(s);
	  }
	
	
}
