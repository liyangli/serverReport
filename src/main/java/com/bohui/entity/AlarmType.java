package com.bohui.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 告警类型，进行设定具体告警。进行通过树形结构进行组装
 * User: liyangli
 * Date: 2016/4/27
 * Time: 09:38
 */
public class AlarmType {

    private Integer id;
    private String name;
    private boolean flag = true ;//表明是否为最后一级

    private List<AlarmType>  list = new ArrayList<AlarmType>();


    public AlarmType(Integer type, String pName, boolean b) {
           this.id = type;
        this.name = pName;
        this.flag = b;
    }

    public AlarmType(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public List<AlarmType> getList() {
        return list;
    }

    public void setList(List<AlarmType> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmType)) return false;

        AlarmType alarmType = (AlarmType) o;

        if (!id.equals(alarmType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        return result;
    }
}
