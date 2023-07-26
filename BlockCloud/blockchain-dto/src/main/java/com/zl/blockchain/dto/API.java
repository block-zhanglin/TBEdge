package com.zl.blockchain.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Es层的接口常量
 */
public class API {

    public static String CloundName="CloundName"; //云中心名字
    public static String Cip="Cip";//云服务器ip地址
    public static String Cport="Cport"; //云中心的服务器端口9005

    public static  String MainCip="MainCip";//主云中心ip地址


    public static   String CAPublicKey="CAPublicKey"; //CA机构的公钥
    public static   String Caip="Caip"; //Ca的ip地址
    public static  String  CAPort="CAPort"; //CA的服务器端口
    public static   String  ESPort="ESPort"; //CA的服务器端口



    public static  final  String IottoCaReuest="/Iot_CA"; //iot(或Gateway或Es)层请求证书
    public static final String PING = "/ping";
    public static final String GET_NODES = "/get_nodes";
    public static final String GET_BLOCKCHAIN_HEIGHT = "/get_blockchain_height";
    public static final String POST_BLOCKCHAIN_HEIGHT = "/post_blockchain_height";
    public static final String GET_BLOCK = "/get_block";
    public static final String POST_BLOCK = "/post_block";
    public static  final  String BLOCK_UNLOAD="/block_unload"; //卸载区块
    public static  final String EsFull="/Es_full";//ES节点已经满了
    public static List<String>Esfullip=new ArrayList<>(); //同一Es节点的卸载请求只接受一次
}
