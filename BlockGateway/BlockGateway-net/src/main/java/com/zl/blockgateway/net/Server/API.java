package com.zl.blockgateway.net.Server;

public class API {

    /**
     * 网关设置
     */
    public static   String GateWayName="GateWayName"; //网关层名字
    public static   String GateWayPort="GateWayPort"; //网关层的服务器端口9002

    /**
     * CA证书机构设置
     */
    public static  String CAPublicKey="CAPublicKey"; //CA机构的公钥
    public static  String Caip="Caip"; //Ca的ip地址
    public static  String CAPort="CAPort"; //CA的服务器端口

    /**
     * Iot层设计
     */
    public static   String Iotip="Iotip";//Iot层的ip地址
    public static   String IotPort="IotPort";  //Iot层的服务器端口

    /**
     * Es层设置
     */
    public static  String EsName="EsName";//对应的Es服务器的名字
    public static  String Esip="Esip";//Es层的ip地址
    public static  String EsPort="EsPort"; //Es层的服务器端口8083


    public static  final  String IottoCaReuest="/Iot_CA"; //gateway层请求证书 *
    public static final String GatewaytoEsRequest="/Gateway_Es_Request";  //网关层向Es层的分配信息url
    public static  final  String GatewaytoEsRequestConfirm="/Gateway_Es_requestconfirm"; //网关层向Es层的确认信息url
    public static  final  String IottoGatewayMessageRequest="/Iot_Gateway_messagerequest"; //Iot层向网关层发送任务请求信息 *
    public static final String IottoGatewayConfirmMessageRequst="/Iot_Gateway_confirmmessagerequest"; //Iot层向网关层发送确认信息
    public static final String EstoIotrequest="/Es_Iot_request"; // Es层向Iot层发送数据请求
    public static final  String EstoIotdatarequest="/Es_Iot_datarequest"; // Es层处理完Iot的数据之后，返回Iot设备确认信息
}
