package util;

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
            dataRootPath = "C:\\blockchain-CA-java\\";
        }else if(SystemUtil.isMacOperateSystem()){ //判断是否为mac操作系统
            dataRootPath = "/tmp/blockchain-CA-java/";
        }else if(SystemUtil.isLinuxOperateSystem()){ //判断是否为linux操作系统
            dataRootPath = "/tmp/blockchain-CA-java/";
        }else{
            dataRootPath = "/tmp/blockchain-CA-java/";
        }
        FileUtil.makeDirectory(dataRootPath);  //创建目录
        return dataRootPath;
    }

}
