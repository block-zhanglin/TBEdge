package com.zl.blockIot.net.Server;


/**
 * IOT层的接口常量
 */
public class API {

    /**
     * IOT设置
     */
    public static  String IotName="IotName"; //IOT设备的名字
    public static  String IotPort="IotPort";  //Iot层的服务器端口9001

    /**
     * CA设置
     */
    public static String CAPublicKey="CAPublicKey"; //CA机构的公钥
    public static String Caip="Caip"; //Ca的ip地址
    public static String CAPort="CAPort"; //CA的服务器端口

    /**
     * Gateway设置
     */
    public static   String GateWayip="GateWayip"; //    该iot设备对应的网关ip地址
    public static   String GateWayPort="GateWayPort"; //Gateway层的服务器端口9002


    /**
     * Es设置
     */
    public static String Esip="Esip"; //该IOT设备对应的Es层ip地址
    public static String EsPort="EsPort"; //Es层的服务器端口9003




    public static  final  String IottoCaReuest="/Iot_CA"; //iot层请求证书
    public static final String GatewaytoEsRequest="/Gateway_Es_Request";  //网关层向Es层的分配信息url
    public static  final  String GatewaytoEsRequestConfirm="/Gateway_Es_requestconfirm"; //网关层向Es层的确认信息url
    public static  final  String IottoGatewayMessageRequest="/Iot_Gateway_messagerequest"; //Iot层向网关层发送任务请求信息
    public static final String IottoGatewayConfirmMessageRequst="/Iot_Gateway_confirmmessagerequest"; //Iot层向网关层发送确认信息
    public static final String EstoIotrequest="/Es_Iot_request"; // Es层向Iot层发送数据请求
    public static final  String EstoIotdatarequest="/Es_Iot_datarequest"; // Es层处理完Iot的数据之后，返回Iot设备确认信息

}
