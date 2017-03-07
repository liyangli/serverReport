package com.bohui.dao;

import com.bohui.entity.*;
import com.bohui.utils.ServerFileDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: liyangli
 * Date: 2016/4/11
 * Time: 15:18
 */
@Component
public class MonitorDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 分中心和前端
     * @return
     */
    public List<District> findAllMonitor(){
        //获取所有分中心、前端。如果没有前端的分中心则不进行显示
        List<District> districts = allDistrict();
        if(districts.isEmpty()){
            return districts;
        }
        List<Monitor> monitors = allMonitor();
        if(monitors.isEmpty()){
            return districts;
        }
        for(District district: districts){
            Integer id = district.getId();
            List<Monitor> monitorList = district.getMonitors();
            for(Monitor monitor :monitors){
               if(monitor.getDistrictID().equals(id)){
                   monitorList.add(monitor);
               }
            }
            //设定好后进行排序重新设定上去
            Collections.sort(monitorList,new Comparator<Monitor>() {
                @Override
                public int compare(Monitor o1, Monitor o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
//            district.setMonitors(monitorList);
        }
        Collections.sort(districts,new Comparator<District>() {
            @Override
            public int compare(District o1, District o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return districts;
    }

    /**
     * 获取所有频道信箱
     * @return
     * @param monitor
     */
    public List<Monitor> findServers(String monitor) throws Exception{
        //直接从对应文件中获取对应频道相关内容
        List<Monitor> list = findMonitor(monitor);
        ServerFileDeal.DEAL.findServerByMonitor(list);
        return list;
    }

    private List<Monitor> findMonitor(String ids){
        List<Monitor> list = new ArrayList<Monitor>();
        String sql = "select * from  tbDevice" ;
        if(null != ids && !ids.isEmpty()){
            sql += " where nDeviceID in ("+ids+")";
        }
        List<Map<String,Object>>  devices = jdbcTemplate.queryForList(sql);

        for(Map<String,Object> map : devices){
            list.add(new Monitor(map));
        }
        return list;
    }

    private List<Monitor> allMonitor(){
        return findMonitor(null);
    }

    //获取所有分中心
    private List<District> allDistrict(){
        List<Map<String,Object>>  list = jdbcTemplate.queryForList("select * from  tbDistrict");
        List<District> districts = new ArrayList<District>();
        for(Map<String,Object> map:list){
            District district = new District(map);
            districts.add(district);
        }
        return districts;
    }

    public List<Alarm> searchAlarm( String moniterIds, String serverIds,String alarmTypeIds, String startTime, String endTime) {
        List<Alarm> alarms = new ArrayList<Alarm>();
        String sql = "select d.strAliasName as monitorName,e.strProName as serverName,tmErrBegin as startTime,tmErrEnd as endTime from tbError e,tbDevice d where 1=1 ";
       /* if(!moniterIds.isEmpty()){
            sql += " and e.nDeviceID in ("+moniterIds+")";
        }*/
        if(!serverIds.isEmpty()){
            sql += " and (";
            String[] ids = serverIds.split(";");
            boolean flag = false;

            for(String id:ids){
                if(flag){
                    sql += " or ";
                }else{
                    flag = true;
                }
                String[] monitorAndServers = id.split(":");
                String monitorId = monitorAndServers[0];
                String sids = monitorAndServers[1];
                sql += "(e.nDeviceID ="+monitorId+" and e.nAPID in ("+sids+"))";
            }
            sql +=  ")";
//            sql += " and e.nAPID in ("+serverIds+")";
        }
        sql += " and e.nDeviceId=d.nDeviceId ";
        if(alarmTypeIds != null && !alarmTypeIds.isEmpty()){
            sql += " and nErrorTypeID in ("+alarmTypeIds+")";
        }


        sql += " and e.tmErrBegin >='"+startTime+"'";
        sql += " and e.tmErrEnd <= '"+endTime +"'";
        System.out.println(sql);
        List<Map<String,Object>>  list = jdbcTemplate.queryForList(sql);
        for(Map<String,Object> map:list){
            Alarm alarm = new Alarm(map);
            alarms.add(alarm);
        }
        return alarms;
    }

    private Map<Integer,String> firstAlarmType = new HashMap<Integer, String>();

    {
        firstAlarmType.put(1,"音视频故障");
        firstAlarmType.put(2,"290一级故障");
        firstAlarmType.put(3,"290二级故障");
        firstAlarmType.put(4,"290三级故障");
        firstAlarmType.put(5,"指标故障");
        firstAlarmType.put(6,"");
        firstAlarmType.put(7,"设备故障");
    }
    /**
     * 获取所有告警类型
     * @return
     */
    public List findAlarmTypes() {
        List<AlarmType> datas = new ArrayList<AlarmType>();
        String sql = "select nETID,strETName,nETType from tbErrorType";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
        Map<AlarmType,List<AlarmType>>  typeMap = new HashMap<AlarmType, List<AlarmType>>();
        for(Map<String,Object> map:list){
            Integer id = (Integer)map.get("nETID");
            String name = (String)map.get("strETName");
            Integer type = (Integer)map.get("nETType");
            String pName = firstAlarmType.get(type);
            AlarmType at = new AlarmType(type,pName,false);
            List<AlarmType> ats = typeMap.get(at);
            if(ats == null){
                ats = new ArrayList<AlarmType>();
                typeMap.put(at,ats);
            }
            ats.add(new AlarmType(id,name));
        }
        Set<Map.Entry<AlarmType,List<AlarmType>>>  set = typeMap.entrySet();
        for(Map.Entry<AlarmType,List<AlarmType>> entry:set){
            AlarmType at = entry.getKey();
            at.setList(entry.getValue());
            datas.add(at);
        }

        return datas;
    }
}
