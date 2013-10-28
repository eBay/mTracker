package com.ebay.build.udc;
/*
 * This class stores a copied blacklist of IFilterConstants.java 
 * from com.ebay.uede.adoptiontracking.filters
*/

/**
 * @author yiqushen
 *
 */
public interface IFilterConstants
{
   //20101028#kevin shen skip blacklist
	 public static final String[] FEATURE_BLACKLIST={"Toggle Breakpoint","Java Editor Ruler Single-Click",
                                             "&In-Place Editor", "%AddBreakpoint.label",
                                             "com.android.ide.eclipse.adt.debug.launching.LaunchShortcut.run",
     	                                     "org.maven.ide.eclipse.actions.LifeCyclePackage.run"};

}
