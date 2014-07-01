/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

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
