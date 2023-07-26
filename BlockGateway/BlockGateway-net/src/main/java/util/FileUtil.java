package util;

import java.io.*;

/**
 *
 */
public class FileUtil { //文件操作类


    /**
     *创建新文件---在（parent，child）：若parent为空则直接利用child创建文件；若parent不为空，在parent下创建child名文件
     */
    public static String newPath(String parent, String child) {
        return new File(parent,child).getAbsolutePath();
    }


    /**
     *makeDirectory 创建目录
     * @param path
     */
    public static void makeDirectory(String path) {
        File file = new File(path);
        if(file.exists()){
            return;
        }
        boolean isMakeDirectorySuccess = file.mkdirs();
        if(!isMakeDirectorySuccess){
            SystemUtil.errorExit("create directory failed. path is "+path + ".",null);
        }
    }

    /**
     * deleteDirectory 删除目录
     * @param path
     */
    public static void deleteDirectory(String path) {
        File file = new File(path);
        if(file.isDirectory()){
            File[] childrenFiles = file.listFiles();
            if(childrenFiles != null){
                for (File childFile:childrenFiles){
                    deleteDirectory(childFile.getAbsolutePath());
                }
            }
        }
        boolean isDeleteDirectorySuccess = file.delete();
        if(!isDeleteDirectorySuccess){
            SystemUtil.errorExit("delete directory failed. path is "+path + ".",null);
        }
    }


    /**
     *读取指定地址的文件
     * @param path
     * @return
     */
    public static String read(String path) {
        File file = new File(path);
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileStream, "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String text = "";
            String line;
            while((line = br.readLine()) != null){
                text += line;
                text+="&";
            }
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fileStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
