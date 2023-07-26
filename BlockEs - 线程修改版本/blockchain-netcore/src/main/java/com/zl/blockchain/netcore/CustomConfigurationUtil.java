package com.zl.blockchain.netcore;

import com.zl.blockchain.dto.API;
import com.zl.blockchain.setting.BlockSetting;
import com.zl.blockchain.setting.NetworkSetting;
import com.zl.blockchain.util.FileUtil;
import com.zl.blockchain.util.SystemUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

public class CustomConfigurationUtil {

    private  static  HashMap<String,String> config=new HashMap<>();


    //读取文件中的数据
    public static void readDate(){
        String path=null;
        if(SystemUtil.isWindowsOperateSystem()){
            path="C:\\EsCustomConfiguration.txt";
        }else if(SystemUtil.isMacOperateSystem()){ //判断是否为mac操作系统
            path= "/home/ubuntu/EsCustomConfiguration.txt";
        }else if(SystemUtil.isLinuxOperateSystem()){ //判断是否为linux操作系统
            path= "/home/ubuntu/EsCustomConfiguration.txt";
        }else{
            path= "/home/ubuntu/EsCustomConfiguration.txt";
        }
        String s= FileUtil.read(path);
        System.out.println(s);
        String[]ss=s.split("&");
        for(String x:ss){
            String []ss1=x.split("=");
            config.put(ss1[0],ss1[1]);
        }
    }

    public static void writeAPI(){

        API.Blocksize=config.get(API.Blocksize);

        BlockSetting.CommitteeTime=config.get(BlockSetting.CommitteeTime);
        BlockSetting.CommitteeCount=config.get(BlockSetting.CommitteeCount);
        NetworkSetting.SEED_NODES[0]=config.get("SeedNode");

//        API.EsNumber="Es192.168.137.112";
//        API.Esip=config.get(API.Esip);
//        API.Esip="192.168.137.112";
        API.Esip=getIpAddress(); //获取本机的ip地址
        API.EsNumber="Es"+API.Esip;


        API.EsPort=config.get(API.EsPort);

        API.CAPublicKey=config.get(API.CAPublicKey);
        API.Caip=config.get(API.Caip);
        API.CAPort=config.get(API.CAPort);

        API.Iotip=config.get(API.Iotip);
        API.IotPort=config.get(API.IotPort);

        API.GateWayip=config.get(API.GateWayip);
        API.GateWayPort=config.get(API.GateWayPort);
        API.CloundIp=config.get(API.CloundIp);
        API.Cport=config.get(API.Cport);
        Random random=new Random();
        BlockSetting.MaxBlock=random.nextInt(80)+20;
    }

    public static void run(){
        readDate();
        writeAPI();
    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }

}




