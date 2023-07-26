package util;

import com.zl.blockgateway.net.Server.API;

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
            path="C:\\GateWayCustomConfiguration.txt";
        }else if(SystemUtil.isMacOperateSystem()){ //判断是否为mac操作系统
            path= "/home/ubuntu/GateWayCustomConfiguration.txt";
        }else if(SystemUtil.isLinuxOperateSystem()){ //判断是否为linux操作系统
            path= "/home/ubuntu/GateWayCustomConfiguration.txt";
        }else{
            path= "/GateWayCustomConfiguration.txt";
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
        API.GateWayName="GateWay"+getIpAddress();
        API.GateWayPort=config.get(API.GateWayPort);

        API.CAPublicKey=config.get(API.CAPublicKey);
        API.Caip=config.get(API.Caip);
        API.CAPort=config.get(API.CAPort);

        API.Iotip=config.get(API.Iotip);
        API.IotPort=config.get(API.IotPort);

        API.Esip=config.get(API.Esip);
        API.EsPort=config.get(API.EsPort);
        API.EsName="Es"+getIpAddress();
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
