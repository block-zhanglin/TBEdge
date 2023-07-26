package com.zl.blockchain.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 网络工具类
 */
public class NetUtil {

    /**通过请求地址 requestUrl 获取到回复
     *
     * @param requestUrl
     * @param requestBody
     * @return
     */
    public static String get(String requestUrl, String requestBody) {
        OutputStreamWriter out = null;
        BufferedReader br = null;
        try {
            URL url = new URL(requestUrl); //得到访问地址的url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  //得到网络访问对象的java.net.httpurlconnection
            connection.setDoOutput(true);  //设置是否向httpconnection输出
            connection.setDoInput(true);   //设置是否从httpconnection读入
            connection.setUseCaches(false); //设置是否使用缓冲
            connection.setInstanceFollowRedirects(true);  //设置实例是否自动执行http重定向
            connection.setRequestMethod("GET");   //设置请求方式
            connection.setRequestProperty("Accept", "application/json"); //设置请求头
            connection.setRequestProperty("Content-Type", "application/json"); //设置使用标准编码格式编码参数的名-值对
            connection.setReadTimeout(60000);  //设置读取时间60秒
            connection.setConnectTimeout(60000);   //设置超时时间60秒
            connection.connect();  //

            /**
             * 处理输入输出
             */
            out = new OutputStreamWriter(connection.getOutputStream(),StandardCharsets.UTF_8); //获得输出流
            out.append(requestBody);  //输出流中加入requestBody
            out.flush();
            out.close();

            InputStream is;
            int responseCode = connection.getResponseCode(); //获取响应状态码    必须调用getresponsecode方法，单独调用connect方法只是建立数据，并不会传递数据。
            if (responseCode == 200) {  //响应状态码是200，有响应
                is = connection.getInputStream(); //获取连接中的响应信息
            } else {
                is = connection.getErrorStream();
            }

            StringBuilder data = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));  //从流中读取响应信息
            String line;
            while ((line = br.readLine()) != null) {
                data.append(line); //将响应信息写入string中
            }
            return data.toString();

        } catch (Exception e){
            //default return value null
            return null;
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
               System.out.println("s");
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("s");
            }
        }
    }

}
