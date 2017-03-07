package com.bohui.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分中心对象
 * User: liyangli
 * Date: 2016/4/11
 * Time: 16:28
 */
public class District {

    private Integer id;
    private String name;

    //前端
    private List<Monitor> monitors = new ArrayList<Monitor>();

    public District(Map<String, Object> map) {
        this.id = (Integer)map.get("nDistrictID");
        this.name = (String)map.get("strAliasName");
    }


    public List<Monitor> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<Monitor> monitors) {
        this.monitors = monitors;
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
        return "District{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monitors=" + monitors +
                '}';
    }
}
