package com.bohui;

import com.alibaba.druid.support.json.JSONUtils;
import com.bohui.dao.MonitorDao;
import com.bohui.entity.Alarm;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/")
public class HelloController {
    private Logger logger  = LoggerFactory.getLogger(HelloController.class);
    @Autowired
    private MonitorDao monitorDao;
	@RequestMapping(value = "hello",method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Hello world!");
		return "hello";
	}


    /**
     * 获取所有前端,包含分中心
     * @return
     */
    @RequestMapping("/findAllMonitor")
    @ResponseBody
    public List findAllMonitor(){
        List list = monitorDao.findAllMonitor();
        return list;
    }

    /**
     * 获取所有前端,包含分中心
     * @return
     */
    @RequestMapping("/findAlarmTypes")
    @ResponseBody
    public List findAlarmTypes(){
        List list = monitorDao.findAlarmTypes();
        return list;
    }
    /**
     * 获取所有频道信箱
     * @return
     */
    @RequestMapping(value = "/findServer")
    @ResponseBody
    public List findServer(String monitor){
        List list = null;
        try {
            list = monitorDao.findServers(monitor);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("出现错误了，错误信息为：{}",e.toString());
        }
        return list;
    }

    /**
     * 获取所有频道信箱
     * @return
     */
    @RequestMapping(value = "/export")
    @ResponseBody
    public Map<String,String> export(String moniterIds,String serverIds,String alarmTypeIds,String startTime,String endTime,@RequestParam( required = false,defaultValue = "")String filePath){
        List<Alarm> errors = monitorDao.searchAlarm(moniterIds, serverIds,alarmTypeIds, startTime, endTime);
        //开始组装对应导出操作。。。
        //后台进行生成相关文件。
        Map<String,String> map = new HashMap<String, String>();
        String flag = "true";
        String msg = "";
        try {
            export(errors,filePath);
        } catch (Exception e) {
            flag = "flase";
            msg = "导出时出错了，错误异常为："+e.toString();
            System.out.println("导出文件时出错了，错误异常为："+e.toString());
//            e.printStackTrace();
        }
        map.put("flag",flag);
        map.put("msg",msg);
        return map;
    }

    /**
     * 执行真正导出操作
     * @param alarms
     * @param filePath
     */
    private void export(List<Alarm> alarms, String filePath) throws Exception{
        Map<String,Map<String,Long>> map = new HashMap<String, Map<String, Long>>();//key:前端，val:频道和告警时长
        for(Alarm alarm:alarms){
            Map<String,Long> serverMap = map.get(alarm.getMonitorName());
            if(serverMap == null){
                serverMap = new HashMap<String, Long>();
            }
            //先相同的频道进行计算对应差值 getIncrease
            String serverName = alarm.getServerName();
            Long increase = serverMap.get(serverName);
            if(increase == null){
                increase =  alarm.getIncrease();
            }else {
                increase += alarm.getIncrease();
            }
            serverMap.put(serverName,increase);
            map.put(alarm.getMonitorName(),serverMap);
        }
        //组装好了，可以导出数据了
        realExport(map,filePath);
    }

    /**
     * 真正开始导出操作
     * @param map
     * @param filePath
     */
    private void realExport(Map<String, Map<String, Long>> map, String filePath) throws Exception{
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet s = wb.createSheet();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//上下居中
        style.setBorderBottom((short)1);//下边框
        style.setBorderLeft((short) 1);//左边框
        style.setBorderRight((short) 1);//右边框
        style.setBorderTop((short) 1);
        createFirstRow(wb,s);
        createSecondRow(s, style);
        createThreeRow(s, style);
        createContentRow(s, style, map);
        FileOutputStream out = findOutStream(filePath);
        wb.write(out);
        out.close();
    }

    /**
     * 创建对应内容。
     * @param s
     * @param style
     */
    private void createContentRow(HSSFSheet s,HSSFCellStyle style,Map<String,Map<String,Long>> map){
        int rowStart = 4;
        Set<Map.Entry<String,Map<String,Long>>> set = map.entrySet();
        s.setColumnWidth(1, 10000);
        s.setColumnWidth(2, 10000);
        s.setColumnWidth(3, 10000);
        s.setColumnWidth(4,10000);
        for(Map.Entry<String,Map<String,Long>> entry:set){
            String monitorName = entry.getKey();
            HSSFRow row = s.createRow(rowStart);
            HSSFCell cell = row.createCell(1);

            HSSFRichTextString richText = new HSSFRichTextString(monitorName);
            cell.setCellValue(richText);
            cell.setCellStyle(style);
            Map<String,Long> serverMap = entry.getValue();
            Set<Map.Entry<String,Long>> serverSet = serverMap.entrySet();
            int start = rowStart;
            for(Map.Entry<String,Long> serverEntry:serverSet){
                //相当于每行
                if(rowStart != start){
                    row = s.createRow(rowStart);
                    cell = row.createCell(1);
                    richText = new HSSFRichTextString("");
                    cell.setCellValue(richText);
                    cell.setCellStyle(style);
                }
                String serverName = serverEntry.getKey();
                Long increase = serverEntry.getValue();
                cell = row.createCell(2);
                 richText = new HSSFRichTextString(serverName);
                cell.setCellValue(richText);
                cell.setCellStyle(style);
                cell = row.createCell(3);
                 richText = new HSSFRichTextString(String.valueOf(increase/1000));
                cell.setCellValue(richText);
                cell.setCellStyle(style);
                cell = row.createCell(4);
                richText = new HSSFRichTextString("");
                cell.setCellValue(richText);
                cell.setCellStyle(style);
                rowStart ++;
            }

            if(rowStart > start + 1){
                CellRangeAddress cra=new CellRangeAddress(start, rowStart-1, 1, 1);
                //在sheet里增加合并单元格
                s.addMergedRegion(cra);
            }
        }
    }

    private void createThreeRow(HSSFSheet s,HSSFCellStyle style){
        HSSFRow headRow = s.createRow(3);//数据
        String[] heards = {"地区","频道","停播时长(秒)","备注"};
        for(int i=1;i<heards.length+1;i++){
            HSSFCell cell = headRow.createCell(i);
            HSSFRichTextString richText = new HSSFRichTextString(heards[i-1]);
            cell.setCellValue(richText);
            cell.setCellStyle(style);
        }
    }

    private void createSecondRow(HSSFSheet s,HSSFCellStyle style){
        HSSFRow firsRow = s.createRow(2);//第二行数据
        HSSFCell firstCell = firsRow.createCell(1);
        String desc = "日期：";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        desc += sdf.format(new Date());
        HSSFRichTextString richText = new HSSFRichTextString(desc);
        firstCell.setCellValue(richText);
        firstCell.setCellStyle(style);

        HSSFCell cell = firsRow.createCell(2);
        richText = new HSSFRichTextString("");
        cell.setCellValue(richText);
        cell.setCellStyle(style);
         cell = firsRow.createCell(3);
        richText = new HSSFRichTextString("网络维护管理部");
        cell.setCellValue(richText);
        cell.setCellStyle(style);

         cell = firsRow.createCell(4);
        richText = new HSSFRichTextString("");
        cell.setCellValue(richText);
        cell.setCellStyle(style);

        CellRangeAddress cra=new CellRangeAddress(2, 2, 1, 2);
        CellRangeAddress cra1=new CellRangeAddress(2, 2, 3, 4);
        //在sheet里增加合并单元格
        s.addMergedRegion(cra);
        s.addMergedRegion(cra1);
    }

    /**
     * 创建第一行描述信息
     * @param wb
     * @param s
     */
    private void createFirstRow(HSSFWorkbook wb, HSSFSheet s){
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//上下居中
        HSSFRow firsRow = s.createRow(1);//首行数据
        HSSFCell firstCell = firsRow.createCell(1);
        style.setBorderBottom((short)1);//下边框
        style.setBorderLeft((short) 1);//左边框
        style.setBorderRight((short) 1);//右边框
        style.setBorderTop((short) 1);
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 16);//字号
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);//加粗
        style.setFont(font);
        HSSFRichTextString richText = new HSSFRichTextString("江西有线安全播出统计表");
        firstCell.setCellValue(richText);
        firstCell.setCellStyle(style);
        for(int i=2;i<=4;i++){
            firstCell = firsRow.createCell(i);
             richText = new HSSFRichTextString("");
            firstCell.setCellValue(richText);
            firstCell.setCellStyle(style);
        }
        CellRangeAddress cra=new CellRangeAddress(1, 1, 1, 4);
        //在sheet里增加合并单元格
        s.addMergedRegion(cra);
    }

    private FileOutputStream findOutStream(String userPath) throws Exception{
        if(userPath.isEmpty()){
            try {
                userPath = URLDecoder.decode(HelloController.class.getResource("/").getPath(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("在解析对应文件路径时出错了，确认所安装路径下有中文；异常信息为："+e.getMessage());
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = sdf.format(new Date())+".xls";
        String filePath = userPath + fileName ;
        try {
            File file = new File(filePath);
            if(!file.exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            return out;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw  e;
        }
    }
}