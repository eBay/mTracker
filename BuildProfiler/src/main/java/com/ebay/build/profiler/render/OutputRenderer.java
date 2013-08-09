package com.ebay.build.profiler.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.ebay.build.profiler.profile.DiscoveryProfile;
import com.ebay.build.profiler.profile.MojoProfile;
import com.ebay.build.profiler.profile.PhaseProfile;
import com.ebay.build.profiler.profile.ProjectProfile;
import com.ebay.build.profiler.profile.SessionProfile;
import com.ebay.build.profiler.util.Timer;


public class OutputRenderer {

	SessionProfile sessionProfile;
	DiscoveryProfile discoveryProfile;

	public OutputRenderer(SessionProfile sessionProfile,
			DiscoveryProfile discoveryProfile) {
		this.sessionProfile = sessionProfile;
		this.discoveryProfile = discoveryProfile;
	}

	public void renderToScreen() {
		render("Raptor Build Profile Output :");
		render("-----------------------------------------------------------");
		render("Project Discovery : " + Timer.formatTime(discoveryProfile.getElapsedTime()));
		render("");
		render("  PreDownload :"+Timer.formatTime(sessionProfile.getPdProfile().getElapsedTime()));
		render("");
		
		for (ProjectProfile pp : sessionProfile.getProjectProfiles()) {
			render(pp.getProjectName() + " "
					+ Timer.formatTime(pp.getElapsedTime()));
			for (PhaseProfile phaseProfile : pp.getPhaseProfile()) {
				render("  " + phaseProfile.getPhase() + " "
						+ Timer.formatTime(phaseProfile.getElapsedTime()));
				for (MojoProfile mp : phaseProfile.getMojoProfiles()) {
					render("    " + mp.getId()
							+ Timer.formatTime(mp.getElapsedTime()));
				}
			}
			render("");
		}
		
		// collect the amount of time each plugin took overall
		HashMap<String,Long> mojotimes = new HashMap<String,Long>();
		for (ProjectProfile pp : sessionProfile.getProjectProfiles()) {
			for (PhaseProfile phaseProfile : pp.getPhaseProfile()) {
				for (MojoProfile mp : phaseProfile.getMojoProfiles()) {
					if( mojotimes.containsKey(mp.getId() )){
						Long count = mojotimes.get(mp.getId());
						count = count + mp.getElapsedTime();
						mojotimes.put(mp.getId(), count);
					}else{
						mojotimes.put(mp.getId(), mp.getElapsedTime());
					}
				}
			}
		}
		
		Long totaltime = 0L;
		int maxlength = 70;
		for( String mojo : mojotimes.keySet() ){
			Long mojotime = mojotimes.get(mojo);
			totaltime = totaltime + mojotime;
			if( mojo.length() > maxlength ){
				maxlength = mojo.length() + 1;
			}
		}
		
		render("===========================================");
		render("Time spent by each mojo, Total : " + totaltime + " ms");
		render("===========================================");
		
		for( String mojo : mojotimes.keySet() ){
			Long mojotime = mojotimes.get(mojo);
			//render(" " + mojo + " : " + Timer.formatTime(mojotime) );
			Float top = new Float(mojotime);
			Float bottom = new Float(totaltime);
			float percent =  top / bottom;
			percent = percent * 100;
			String format= "%-"+ maxlength +"s : %7d%2s %5.2f%1s \n";
			System.out.format( format, mojo, mojotime, "ms", percent, "%" );
		}
	}


	public void renderToHTML() {

	}

	public void renderToJSON() {
		ProfileOutputJSON json = new ProfileOutputJSON();

		Map<String, Long> projectSlice = json.getProjectSlicing();
		Map<String, LinkedList<Long>> projectToPluginSlice = json
				.getProjectToPluginSlicing();
		Map<String, LinkedList<Long>> projectToPhaseSlice = json
				.getProjectToPhaseSlicing();

		int prjCnt = 0;

		for (ProjectProfile pp : sessionProfile.getProjectProfiles()) {
			projectSlice.put(pp.getProjectArtifactId(), pp.getElapsedTime());
			
			for (PhaseProfile phaseProfile : pp.getPhaseProfile()) {
				
				long phaseTime = 0L;
				for (MojoProfile mp : phaseProfile.getMojoProfiles()) {
					String key = mp.getPluginArtifactID();
					phaseTime = phaseTime + mp.getElapsedTime();

					if (projectToPluginSlice.containsKey(key)) {
						LinkedList<Long> linkedList = projectToPluginSlice
								.get(key);
						if(linkedList.size() == prjCnt+1) {
							long temp = linkedList.getLast();
							linkedList.removeLast();
							linkedList.add(temp + mp.getElapsedTime());
						} else {
							linkedList.add(mp.getElapsedTime());
						}
					} else {
						LinkedList<Long> value = new LinkedList<Long>();
						for (int i = 0; i < prjCnt; i++) {
							value.add(0L);
						}
						value.add(mp.getElapsedTime());
						projectToPluginSlice.put(key, value);
					}
				}

				String phase = phaseProfile.getPhase();

				if (projectToPhaseSlice.containsKey(phase)) {
					LinkedList<Long> linkedList = projectToPhaseSlice
							.get(phase);
					if(linkedList.size() == prjCnt+1) {
						long temp = linkedList.getLast();
						linkedList.removeLast();
						linkedList.add(temp + phaseTime);
					} else {
						linkedList.add(phaseTime);
					}
				} else {
					LinkedList<Long> value = new LinkedList<Long>();
					for (int i = 0; i < prjCnt; i++) {
						value.add(0L);
					}
					value.add(phaseTime);
					projectToPhaseSlice.put(phase, value);
				}
			}

			prjCnt++;
			for (Entry<String, LinkedList<Long>> data : projectToPluginSlice
					.entrySet()) {
				if (data.getValue().size() < prjCnt) {
					projectToPluginSlice.get(data.getKey()).add(0L);
				}
			}
			for (Entry<String, LinkedList<Long>> data : projectToPhaseSlice
					.entrySet()) {
				if (data.getValue().size() < prjCnt) {
					projectToPhaseSlice.get(data.getKey()).add(0L);
				}
			}
		}

		json.setProjectSlicing(projectSlice);
		json.setProjectToPluginSlicing(projectToPluginSlice);
		json.setProjectToPhaseSlicing(projectToPhaseSlice);
		json.toCSV();

	}

	private void render(String s) {
		System.out.println(s);
	}

}
