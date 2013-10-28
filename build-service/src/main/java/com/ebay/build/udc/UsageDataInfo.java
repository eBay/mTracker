/*
 * Created on Aug 18, 2010 Copyright (c) eBay, Inc. 2010 All rights reserved.
 */

package com.ebay.build.udc;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author bishen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageDataInfo
{

    public static final String KIND_COMMAND     = "command";    //$NON-NLS-1$
    public static final String KIND_PERSPECTIVE = "perspective"; //$NON-NLS-1$
    public static final String KIND_VIEW        = "view";       //$NON-NLS-1$
    public static final String KIND_EDITOR      = "editor";     //$NON-NLS-1$
    public static final String KIND_BUNDLE      = "bundle";     //$NON-NLS-1$
    public static final String KIND_LOG         = "log";        //$NON-NLS-1$
    public static final String KIND_WORKBENCH   = "workbench";  //$NON-NLS-1$
    public static final String KIND_SYSINFO     = "sysinfo";    //$NON-NLS-1$
    public static final String KIND_IDE         = "ide";        //$NON-NLS-1$
    public static final String KIND_ACTION      = "action";     //$NON-NLS-1$
    public static final String KIND_WIZARD      = "wizard";     //$NON-NLS-1$
    public static final String KIND_DIALOG      = "dialog";     //$NON-NLS-1$
    public static final String KIND_UNKNOWN     = "unknown";    //$NON-NLS-1$

    private String 			   ideType;
    private String 			   ideVersion;
    private long 			   sessionId;
    private String             host;
    private String             user;
    private String             kind;
    private String             what;
    private String             description;
    private String             bundleId;
    private String             bundleVersion;
    private long               when;
    private int                duration;
    private int                size;
    private int                quantity;
    private String             exception;
    private String             properties;
    private String 			   sessionProperties;

    public UsageDataInfo()
    {
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return this.host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return this.user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the kind
     */
    public String getKind()
    {
        return this.kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(String kind)
    {
        this.kind = kind;
    }

    /**
     * @return the what
     */
    public String getWhat()
    {
        return this.what;
    }

    /**
     * @param what the what to set
     */
    public void setWhat(String what)
    {
        this.what = what;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the bundleId
     */
    public String getBundleId()
    {
        return this.bundleId;
    }

    /**
     * @param bundleId the bundleId to set
     */
    public void setBundleId(String bundleId)
    {
        this.bundleId = bundleId;
    }

    /**
     * @return the bundleVersion
     */
    public String getBundleVersion()
    {
        return this.bundleVersion;
    }

    /**
     * @param bundleVersion the bundleVersion to set
     */
    public void setBundleVersion(String bundleVersion)
    {
        this.bundleVersion = bundleVersion;
    }

    /**
     * @return the when
     */
    public long getWhen()
    {
        return this.when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen(long when)
    {
        this.when = when;
    }

    /**
     * @return the duration
     */
    public int getDuration()
    {
        return this.duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size)
    {
        this.size = size;
    }

    /**
     * @return the quantity
     */
    public int getQuantity()
    {
        return this.quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    /**
     * @return the exception
     */
    public String getException()
    {
        return this.exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(String exception)
    {
        this.exception = exception;
    }

    /**
     * @return the properties
     */
    public String getProperties()
    {
        return this.properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(String properties)
    {
        this.properties = properties;
    }

	public String getIdeType() {
		return ideType;
	}

	public void setIdeType(String ideType) {
		this.ideType = ideType;
	}

	public String getIdeVersion() {
		return ideVersion;
	}

	public void setIdeVersion(String ideVersion) {
		this.ideVersion = ideVersion;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionProperties() {
		return sessionProperties;
	}

	public void setSessionProperties(String sessionProperties) {
		this.sessionProperties = sessionProperties;
	}
    
	
    

}
