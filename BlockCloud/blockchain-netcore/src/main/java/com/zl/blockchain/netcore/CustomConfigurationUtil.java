package com.zl.blockchain.netcore;

import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.setting.BlockSetting;
import com.zl.blockchain.setting.NetworkSetting;
import com.zl.blockchain.util.FileUtil;
import com.zl.blockchain.util.SystemUtil;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;

public class CustomConfigurationUtil {

    private  static  HashMap<String,String> config=new HashMap<>();


    //读取文件中的数据
    public static void readDate(){
        String path=null;
        if(SystemUtil.isWindowsOperateSystem()){
            path="C:\\CloundCustomConfiguration.txt";
        }else if(SystemUtil.isMacOperateSystem()){ //判断是否为mac操作系统
            path= "/CloundCustomConfiguration.txt";
        }else if(SystemUtil.isLinuxOperateSystem()){ //判断是否为linux操作系统
            path= "/CloundCustomConfiguration.txt";
        }else{
            path= "/CloundCustomConfiguration.txt";
        }
        String s= FileUtil.read(path);
        String[]ss=s.split("&");
        for(String x:ss){
            String []ss1=x.split("=");
            config.put(ss1[0],ss1[1]);
        }
    }

    public static void writeAPI(){

        API.CloundName=config.get(API.CloundName);
        API.Cip=getIpAddress();
        API.Cport=config.get(API.Cport);
        API.MainCip=config.get(API.MainCip);
        API.CAPublicKey=config.get(API.CAPublicKey);
        API.Caip=config.get(API.Caip);
        API.CAPort= config.get(API.CAPort);
        API.ESPort=config.get(API.ESPort);
        BlockSetting.INTER_BLOCK_COUNT=Long.valueOf(config.get("INTER_BLOCK_COUNT"));
        BlockSetting.Init_Interblockcount=Long.valueOf(config.get("INTER_BLOCK_COUNT"));
        System.out.println(BlockSetting.INTER_BLOCK_COUNT);
        BlockSetting.CommitteeCount=config.get(BlockSetting.CommitteeCount);
        NetworkSetting.SEED_NODES[0]=config.get("SEED_NODES");
        NetworkSetting.Es_SEED_NODES[0]=config.get("Es_SEED_NODES");

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




