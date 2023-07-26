package com.zl.blockchain.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Es层的接口常量
 */
public class API {


    public static  String Blocksize="Blocksize";

    /**
     * Es设置
     */
    public static  String EsNumber="EsNumber"; //边缘服务器名称
    public static  String Esip="Esip";//自身边缘服务器ip
//    public static  String Esip="192.168.137.112";//自身边缘服务器ip
    public static String EsPort="EsPort"; //Es层的服务器端口9003

    public static String CloundIp="CloundIp";//主云中心ip地址
    public static  String Cport="Cport";//主云中心端口

    /**
     *  CA设置
     */
    public static  String CAPublicKey="CAPublicKey"; //CA机构的公钥
    public static  String Caip="Caip"; //Ca的ip地址
    public static String CAPort="CAPort"; //CA的服务器端口

    /**
     * Iot设置
     */
    public static String Iotip="Iotip";//Iot层的ip地址
    public static String IotPort="IotPort";  //Iot层的服务器端口9001

    /**
     * 网关设置
     */
    public static  String GateWayip="GateWayip"; //Gateway层的ip地址
    public static String GateWayPort="GateWayPort"; //Gateway层的端口8082



    public static  final  String IottoCaReuest="/Iot_CA"; //iot层请求证书
    public static final String GatewaytoEsRequest="/Gateway_Es_Request";  //网关层向Es层的分配信息url
    public static  final  String IottoGatewayMessageRequest="/Iot_Gateway_messagerequest"; //Iot层向网关层发送任务请求信息
    public static final String IottoGatewayConfirmMessageRequst="/Iot_Gateway_confirmmessagerequest"; //Iot层向网关层发送确认信息
    public static final String EstoIotrequest="/Es_Iot_request"; // Es层向Iot层发送数据请求
    public static final  String EstoIotdatarequest="/Es_Iot_datarequest"; // Es层处理完Iot的数据之后，返回Iot设备确认信息
    public static final String PING = "/ping";
    public static final String GET_NODES = "/get_nodes";
    public static final String GET_BLOCKCHAIN_HEIGHT = "/get_blockchain_height";
    public static final String POST_BLOCKCHAIN_HEIGHT = "/post_blockchain_height";
    public static final String GET_BLOCK = "/get_block";
    public static final String POST_BLOCK = "/post_block";
    public static final String GET_UNCONFIRMED_TRANSACTIONS = "/get_unconfirmed_transactions";
    public static final String POST_TRANSACTION = "/post_transaction";



    public static  final String POST_COMMITTEE="/post_committee";
    public static  final  String BLOCK_UNLOAD="/block_unload"; //卸载区块
    public static  final String EsFull="/Es_full";//ES节点已经满了

    public static Committee committee=null;

    //收到的来自委员会的区块数量
    public static HashMap<Long, Long>number=new HashMap<Long,Long>();

    //收到的区块的ip地址（来自同一ip地址的同一区块只接收一次）
    public static List<String>ipandblock=new ArrayList<>();

    //挖矿节点挖出了区块，但不会马上存入本地
    public static BlockDto blockDto=null;

    //当前是否为主节点
    public static Boolean Master=false;
    public static Boolean NewBlock=false;

    public Committee getCommittee() {
        return committee;
    }
    public void setCommittee(Committee committee) {
        this.committee = committee;
    }
}
