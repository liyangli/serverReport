package com.bohui.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: liyangli
 * Date: 2016/4/11
 * Time: 15:20
 */
public class Monitor {
    //前端ID
    private Integer id;
    //前端名称
    private String name;
    private Integer districtID;

    private List<Server> servers = new ArrayList<Server>();

    public Monitor(Map<String, Object> map) {
        this.id = (Integer)map.get("nDeviceID");
        this.districtID = (Integer)map.get("nDistrictID");
        this.name = (String)map.get("strAliasName");
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public Integer getDistrictID() {
        return districtID;
    }

    public void setDistrictID(Integer districtID) {
        this.districtID = districtID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", districtID=" + districtID +
                '}';
    }
}
