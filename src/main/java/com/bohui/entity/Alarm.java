package com.bohui.entity;

import java.util.Date;
import java.util.Map;

/**
 * 故障告警
 * User: liyangli
 * Date: 2016/4/11
 * Time: 19:54
 */
public class Alarm {

    private String monitorName;
    private String serverName;
    private Date startTime;
    private Date endTime;

    private Long increase;

    public Alarm(Map<String, Object> map) {
         this.monitorName = (String)map.get("monitorName");
         this.serverName = (String)map.get("serverName");
         this.startTime = (Date)map.get("startTime");
         this.endTime = (Date)map.get("endTime");
    }


    public Long getIncrease() {
        return endTime.getTime()-startTime.getTime();
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "monitorName='" + monitorName + '\'' +
                ", serverName='" + serverName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
