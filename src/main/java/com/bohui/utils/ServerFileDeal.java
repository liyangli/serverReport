package com.bohui.utils;

import com.bohui.entity.Monitor;
import com.bohui.entity.Server;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 频道解析处理。主要根据对应文件进行解析内容获取对应前端下所有频道数据
 * User: liyangli
 * Date: 2016/4/13
 * Time: 17:35
 */
public enum  ServerFileDeal {
    DEAL;

    private String filePath = "E:\\Trinity\\ProtocolXml\\xmlCache";
//private String filePath = "E:\\demo\\xmlCache";
    public void findServerByMonitor(final List<Monitor> monitors) throws  Exception{
        for(Monitor monitor :monitors){
            List<Server> list = new ArrayList<Server>();
            String id = String.valueOf(monitor.getId());
            File serverFile = findServerFile(id);
            if(serverFile == null){
                continue;
            }
            //开始真正xml文件进行解析
            parseServerFile(list,serverFile);
            Collections.sort(list, new Comparator<Server>() {
                @Override
                public int compare(Server o1, Server o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            monitor.setServers(list);
        }

    }

    /**
     * 解析具体内容，把相关频道信息进行统计设定上去
     * @param list
     * @param serverFile
     */
    private void parseServerFile(List<Server> list, File serverFile) throws Exception{
        SAXBuilder builder = new SAXBuilder();
        InputStream file = new FileInputStream(serverFile);
        Document document = builder.build(file);//获得文档对象
        Element root = document.getRootElement();//获得根节点
        Element returnInfoEle = root.getChild("ReturnInfo");
        Element channelScanQueryEle = returnInfoEle.getChild("ChannelScanQuery");
        List<Element> channelScanEles = channelScanQueryEle.getChildren("ChannelScan");
        for(Element channelScanEle:channelScanEles){
            List<Element> channelEles = channelScanEle.getChildren("Channel");
            for(Element channelEle:channelEles){
                list.add(new Server(channelEle));
            }
        }
    }


    public static void main(String[] argus){
        String nn = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Msg Version=\"1\" MsgID=\"942\" Type=\"MonUp\" DateTime=\"2016-04-14 21:41:37\" SrcCode=\"110000N01\" DstCode=\"110000X01\" ReplyID=\"942\" Priority=\"1\">\n" +
                "<Response Type=\"DeviceInfoGet\" Value=\"0\" Desc=\"OK\" />\n" +
                "<ResponseInfo>\n" +
                "<Device InURL=\"\" OutURL=\"udp://@230.111.166.60:5501\" ID=\"148\" IP=\"10.10.10.9\" TaskID=\"696\">\n" +
                "<Item Name=\"输入参数\" Desc=\"输入流名称：熊猫6M 输入流URL： 输入流接收网卡：10.7.1.193\" />\n" +
                "<Item Name=\"输出参数\" Desc=\"输出流名称：归一熊猫6M 输出流URL：udp://@230.111.166.60:5501 输出流接收网卡：10.10.10.9\" />\n" +
                "<Item Name=\"统计信息\" Desc=\"&lt;a data=&apos;696&apos; class=&apos;smtz&apos;&gt;码率统计&lt;a&gt;\" />\n" +
                "</Device>\n" +
                "<Device InURL=\"\" OutURL=\"\" ID=\"84\" IP=\"10.10.11.63\" TaskID=\"\">\n" +
                "<Item Name=\"输入参数\" Desc=\"输入流名称： 输入流URL： 输入流接收网卡：\" />\n" +
                "<Item Name=\"输出参数\" Desc=\"输出流名称： 输出流URL： 输出流接收网卡：\" />\n" +
                "<Item Name=\"统计信息\" Desc=\"&lt;a data=&apos;&apos; class=&apos;smtz&apos;&gt;码率统计&lt;a&gt;\" />\n" +
                "</Device>\n" +
                "<Device InURL=\"\" OutURL=\"udp://@230.142.166.0:5501\" ID=\"162\" IP=\"10.10.10.125\" TaskID=\"794\">\n" +
                "<Item Name=\"输入参数\" Desc=\"输入流名称：输出源熊猫6M 输入流URL： 输入流接收网卡：10.10.10.125\" />\n" +
                "<Item Name=\"输出参数\" Desc=\"输出流名称：备输出熊猫6M 输出流URL：udp://@230.142.166.0:5501 输出流接收网卡：10.10.10.125\" />\n" +
                "<Item Name=\"统计信息\" Desc=\"&lt;a data=&apos;794&apos; class=&apos;smtz&apos;&gt;码率统计&lt;a&gt;\" />\n" +
                "</Device>\n" +
                "</ResponseInfo>\n" +
                "</Msg>";
    }

    /**
     * 进行获取指定文件
     * @param monitorID
     * @return
     */
    private File findServerFile(final String monitorID){

        File file = new File(filePath);
        if(!file.exists()){
            System.out.println("对应配置信息均不存在。。。确认安装对应Server");
            return null;
        }
        File[] serverFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.indexOf((monitorID+"_ChannelScanQuery.xml")) != -1){
                    return true;
                }
                return false;
            }
        });
        if(serverFiles.length == 0){
            return null;
        }
        File serverFile = serverFiles[0];
        return serverFile;
    }
}
