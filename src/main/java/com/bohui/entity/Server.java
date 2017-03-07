package com.bohui.entity;

import org.jdom.Element;

import java.util.Map;

/**
 * 频道实体对象
 * User: liyangli
 * Date: 2016/4/11
 * Time: 19:06
 */
public class Server {

    private Integer id;
    private String name;

    public Server(Map<String, Object> map) {
        this.id = (Integer)map.get("nAPID");
        this.name = (String)map.get("strProName");
    }

    public Server(Element channelEle) {
        this.id =  Integer.parseInt(channelEle.getAttributeValue("AudioPID"));
        this.name =  channelEle.getAttributeValue("Program");
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

}
