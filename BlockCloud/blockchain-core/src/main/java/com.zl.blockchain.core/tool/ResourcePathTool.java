package com.zl.blockchain.core.tool;

import com.zl.blockchain.util.FileUtil;
import com.zl.blockchain.util.SystemUtil;

/**
 * 资源路径工具类
 *  
 */
public class ResourcePathTool {

    /**
     * 获取区块链数据存放目录
     */
    public static String getDataRootPath() {
        String dataRootPath;
        if(SystemUtil.isWindowsOperateSystem()){ //判断是否为windows操作系统
            dataRootPath = "C:\\Clound-blockchain-java\\";
        }else if(SystemUtil.isMacOperateSystem()){ //判断是否为mac操作系统
            dataRootPath = "/tmp/Clound-blockchain-java/";
        }else if(SystemUtil.isLinuxOperateSystem()){ //判断是否为linux操作系统
            dataRootPath = "/tmp/Clound-blockchain-java/";
        }else{
            dataRootPath = "/tmp/Clound-blockchain-java/";
        }
        FileUtil.makeDirectory(dataRootPath);  //创建目录
        return dataRootPath;
    }

    /**
     * 获取测试区块链数据存放目录
     */
    public static String getTestDataRootPath() {
        String dataRootPath;
        if(SystemUtil.isWindowsOperateSystem()){
            dataRootPath = "C:\\Clound-blockchain-java-test\\";
        }else if(SystemUtil.isMacOperateSystem()){
            dataRootPath = "/tmp/Clound-blockchain-java-test/";
        }else if(SystemUtil.isLinuxOperateSystem()){
            dataRootPath = "/tmp/Clound-blockchain-java-test/";
        }else{
            dataRootPath = "/tmp/Clound-blockchain-java-test/";
        }
        FileUtil.makeDirectory(dataRootPath);
        return dataRootPath;
    }
}
