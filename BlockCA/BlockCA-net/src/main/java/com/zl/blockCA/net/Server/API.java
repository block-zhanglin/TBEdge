package com.zl.blockCA.net.Server;


/**
 * CA层的接口常量
 */
public class API {




    public static String CAPort="CAPort"; //CA的服务器端口
    public static String EsPort="EsPort"; //Es层的服务器端口9003

    public static String init="init";//Ca初始化等待时间


    public static final String PING = "/ping";
    public static final String GET_BLOCKCHAIN_HEIGHT = "/get_blockchain_height";
    public static  final String POST_COMMITTEE="/post_committee";
    public static final String IottoCA="/Iot_CA"; //iot层(或网关层或Es层)向CA请求证书

}
