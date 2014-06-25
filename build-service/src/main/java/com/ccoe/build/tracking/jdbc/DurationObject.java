package com.ccoe.build.tracking.jdbc;

/**
 * Created with IntelliJ IDEA.
 * User: mmao
 * Date: 12/3/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DurationObject {
    private int sessionId;
    private int downloadDuration;
    private int buildDuration;

    public int getSessionId() {
        return sessionId;
    }
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    public int getDownloadDuration() {
        return downloadDuration;
    }
    public void setDownloadDuration(int downloadDuration) {
        this.downloadDuration = downloadDuration;
    }
    public int getBuildDuration() {
        return buildDuration;
    }
    public void setBuildDuration(int buildDuration) {
        this.buildDuration = buildDuration;
    }
}
