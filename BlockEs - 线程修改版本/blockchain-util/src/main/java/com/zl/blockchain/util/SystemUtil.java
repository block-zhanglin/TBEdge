package com.zl.blockchain.util;

import java.awt.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *系统工具类
 */
public class SystemUtil {
    /**
     * 判断系统是否为windows系统
     * @return
     */
    public static boolean isWindowsOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * 判断是否为mac系统
     * @return
     */
    public static boolean isMacOperateSystem(){ //判断是否为mac系统
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }


    /**
     * 判断是否为linux系统
     * @return
     */
    public static boolean isLinuxOperateSystem(){ //判断是否为linux系统
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    /**
     * 打印错误日志
     * @param message
     * @param exception
     */
    public static void errorExit(String message, Exception exception) { //打印错误日志
        LogUtil.error("system error occurred, and exited, please check the error！"+message,exception);
        System.exit(1);
    }

    /**
     * 获取系统根目录
     * @return
     */
    public static String systemRootDirectory() {  //系统根目录
        Path currentWorkingDir = Paths.get("").toAbsolutePath();
        return currentWorkingDir.getParent().normalize().toString();
    }

    /**
     * 默认浏览器
     * @param url
     */
    public static void callDefaultBrowser(String url){   //默认浏览器
        try {
            //default solution : open browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
            if(isWindowsOperateSystem()){
                Runtime rt = Runtime.getRuntime();
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            }else if(isMacOperateSystem()){
                Runtime rt = Runtime.getRuntime();
                rt.exec("open " + url);
            }else {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("xdg-open " + url);
            }
        } catch (Exception e) {
            LogUtil.error("system not support call default browser.",e);
        }
    }

}
