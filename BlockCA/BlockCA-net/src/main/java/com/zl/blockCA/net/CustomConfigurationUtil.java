package com.zl.blockCA.net;



import com.zl.blockCA.net.Server.API;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import util.BlockSetting;
import util.FileUtil;
import util.SystemUtil;

import java.util.HashMap;

public class CustomConfigurationUtil {

    private  static  HashMap<String,String> config=new HashMap<>();


    //读取文件中的数据
    public static void readDate(){
        String path=null;
        if(SystemUtil.isWindowsOperateSystem()){
            path="C:\\CaCustomConfiguration.txt";
        }else if(SystemUtil.isMacOperateSystem()){ //判断是否为mac操作系统
            path= "/CaCustomConfiguration.txt";
        }else if(SystemUtil.isLinuxOperateSystem()){ //判断是否为linux操作系统
            path= "/CaCustomConfiguration.txt";
        }else{
            path= "/CaCustomConfiguration.txt";
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

        API.CAPort=config.get(API.CAPort);
        API.EsPort=config.get(API.EsPort);
        API.init=config.get(API.init);
        BlockSetting.CommitteeTime=config.get(BlockSetting.CommitteeTime);
        BlockSetting.CommitteeCount=config.get(BlockSetting.CommitteeCount);



    }

    public static void run(){
        readDate();
        writeAPI();
    }
}
