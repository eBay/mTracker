package com.ccoe.build.profile;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import com.ccoe.build.profiler.profile.DiscoveryProfile;
import com.ccoe.build.profiler.profile.MojoProfile;
import com.ccoe.build.profiler.profile.PhaseProfile;
import com.ccoe.build.profiler.profile.ProjectProfile;
import com.ccoe.build.profiler.profile.SessionProfile;
import com.ccoe.build.profiler.render.OutputRenderer;


public class LifecycleProfilerTest {

	@Test
  public void testSessionProfile() {
    
    SessionProfile s = new SessionProfile(null, null , false);
    
    ProjectProfile p0 = new ProjectProfile(null, project("g0", "a0", "v0"), null);
    p0.setElapsedTime(2323);
    PhaseProfile ph0 = new PhaseProfile(null, "phase0", null);
    MojoProfile m0 = new MojoProfile(null, mojoExecution("goal0","m0"), null);
    m0.setElapsedTime(3000);
    ph0.addMojoProfile(m0);
    MojoProfile m00 = new MojoProfile(null, mojoExecution("goal00","m00"), null);
    m00.setElapsedTime(5492009);
    ph0.addMojoProfile(m00);
    p0.addPhaseProfile(ph0);
    s.addProjectProfile(p0);
    
    ProjectProfile p1 = new ProjectProfile(null, project("g1", "a1", "v1"), null);
    p1.setElapsedTime(2355);
    PhaseProfile ph1 = new PhaseProfile(null, "phase1", null);
    MojoProfile m1 = new MojoProfile(null, mojoExecution("goal1", "m1"), null);
    m1.setElapsedTime(2500);
    ph1.addMojoProfile(m1);
    p1.addPhaseProfile(ph1);
    s.addProjectProfile(p1);

    ProjectProfile p2 = new ProjectProfile(null, project("g2", "a2", "v2"), null);
    PhaseProfile ph2 = new PhaseProfile(null, "phase2", null);
    MojoProfile m2 = new MojoProfile(null, mojoExecution("goal2","m2"), null);
    m2.setElapsedTime(5000);
    ph2.addMojoProfile(m2);
    p2.addPhaseProfile(ph2);
    s.addProjectProfile(p2);
    
    DiscoveryProfile discoveryProfile = new DiscoveryProfile();
    discoveryProfile.setElapsedTime(500);
    
    OutputRenderer r = new OutputRenderer(s, discoveryProfile);
    r.renderToScreen();
    r.renderToJSON();
  }
  
  protected MavenProject project(String g, String a, String v) {
    MavenProject p = new MavenProject();
    p.setGroupId(g);
    p.setArtifactId(a);
    p.setVersion(v);
    return p;
  }
  
  protected MojoExecution mojoExecution(String goal, String executionId) {
    Plugin p = new Plugin();
    p.setGroupId("groupId");
    p.setArtifactId("artifactId" + System.nanoTime());
    p.setVersion("version");
    MojoExecution me = new MojoExecution(p, goal, executionId);
    return me;        
  }
}