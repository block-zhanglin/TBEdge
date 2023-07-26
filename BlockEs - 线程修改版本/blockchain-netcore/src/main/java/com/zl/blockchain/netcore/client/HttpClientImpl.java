package com.zl.blockchain.netcore.client;

import com.zl.blockchain.dto.*;
import com.zl.blockchain.util.JsonUtil;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.NetUtil;

public class HttpClientImpl implements HttpClient{

    private String ip;//节点的ip地址

    private  String IotIp; //Iot层ip

    private String  GatewayIp; //网关层ip

    private  String CaIp; //层ip

    public HttpClientImpl(String ip){
        this.ip=ip;
    }

    public HttpClientImpl(){

    }

    public void setCaIp(String caIp) {
        CaIp = caIp;
    }

    public String getCaIp() {
        return CaIp;
    }

    public void setIotIp(String iotIp) {
        IotIp = iotIp;
    }
    public void setGatewayIp(String gatewayIp) {
        GatewayIp = gatewayIp;
    }

    public String getIotIp() {
        return IotIp;
    }
    public String getGatewayIp() {
        return GatewayIp;
    }


    public void setIp(String ip) {  //设置节点的ip地址
        this.ip = ip;
    }

    public String getIp() { //获取节点的ip地址
        return ip;
    }

    @Override
    public ToCaresponse IOTTO_CARESPONSE(ToCarequest iottoCarequest) {
        try{
            String requesturl=getCaUrl(API.IottoCaReuest);
            String requestBody= JsonUtil.toString(iottoCarequest);
            String response= NetUtil.get(requesturl,requestBody);
            return JsonUtil.toObject(response, ToCaresponse.class);

        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * Es向Iot发出数据请求【任务编号、数据大小、Es编号】
     */
    @Override
    public EstoIotResponse estoiotrequest(EstoIotrequest estoIotrequest) {
        try {
            String requesturl=getiotUrl(API.EstoIotrequest);
            String requestBody= JsonUtil.toString(estoIotrequest);
            String response= NetUtil.get(requesturl,requestBody);
            return JsonUtil.toObject(response, EstoIotResponse.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Es处理完任务之后，向Iot设备返回确认信息【任务编号、Es编号、数据hash、数据地址】
     */
    @Override
    public EstoIotdataResponse estoiotdatarequest(EstoIotdatarequest estoIotdatarequest) {
        try{
            String requesturl=getiotUrl(API.EstoIotdatarequest);
            String requestBody=JsonUtil.toString(estoIotdatarequest);
            String response=NetUtil.get(requesturl,requestBody);
            return JsonUtil.toObject(response, EstoIotdataResponse.class);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 提交交易
     * @param request  交易的request
     * @return
     */
    @Override
    public PostTransactionResponse postTransaction(PostTransactionRequest request) {
        try{
            String requestUrl = getUrl(API.POST_TRANSACTION);  //构建url http://+ip+:+8888+post_transaction
            String requestBody = JsonUtil.toString(request);   //请求头：request头转换为string
            String responseHtml = NetUtil.get(requestUrl,requestBody);  //返回html：
            return JsonUtil.toObject(responseHtml,PostTransactionResponse.class); //将得到的string类型的数据转为object类型
        }catch (Exception e){
            LogUtil.error("client error",e);
            return null;
        }
    }

    /**
     * ping 节点
     * @param request
     * @return
     */
    @Override
    public PingResponse pingNode(PingRequest request) {
        try {
            String requestUrl = getUrl(API.PING); //获取url
            String requestBody = JsonUtil.toString(request); //请求头
            String responseHtml = NetUtil.get(requestUrl,requestBody); //返回体
            return JsonUtil.toObject(responseHtml,PingResponse.class); //得到的string类型的数据转换为object
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 根据区块高度获取区块
     * @param request 获取区块请求
     * @return
     */
    @Override
    public GetBlockResponse getBlock(GetBlockRequest request) {

        try {
            String requestUrl = getUrl(API.GET_BLOCK); //获取url
            String requestBody = JsonUtil.toString(request); //请求头 （区块高度）
            String responseHtml = NetUtil.get(requestUrl,requestBody);  //返回体
            return JsonUtil.toObject(responseHtml,GetBlockResponse.class);//得到的string类型的数据转换为object
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 获取节点
     * @param request 获取节点请求
     * @return
     */
    @Override
    public GetNodesResponse getNodes(GetNodesRequest request) {
        try {
            String requestUrl = getUrl(API.GET_NODES); //获取url
            String requestBody = JsonUtil.toString(request); //请求头
            String responseHtml = NetUtil.get(requestUrl,requestBody); //返回体
            return JsonUtil.toObject(responseHtml, GetNodesResponse.class); //得到的String类型的数据转换为object
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 提交区块
     * @param request 提交区块请求
     * @return
     */
    @Override
    public PostBlockResponse postBlock(PostBlockRequest request) {
        try {
            String requestUrl = getUrl(API.POST_BLOCK);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml,PostBlockResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 提交区块链高度
     * @param request 提交区块链高度请求
     * @return
     */
    @Override
    public PostBlockchainHeightResponse postBlockchainHeight(PostBlockchainHeightRequest request) {
        try {
            String requestUrl = getUrl(API.POST_BLOCKCHAIN_HEIGHT);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, PostBlockchainHeightResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 获取区块链高度
     * @param request 获取区块链高度请求
     * @return
     */
    @Override
    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request) {
        try {
            String requestUrl = getUrl(API.GET_BLOCKCHAIN_HEIGHT);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetBlockchainHeightResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 获取未确认交易
     * @param getUnconfirmedTransactionsRequest get获取未确认交易请求
     * @return
     */
    @Override
    public GetUnconfirmedTransactionsResponse getUnconfirmedTransactions(GetUnconfirmedTransactionsRequest getUnconfirmedTransactionsRequest) {
        try {
            String requestUrl = getUrl(API.GET_UNCONFIRMED_TRANSACTIONS);
            String requestBody = JsonUtil.toString(getUnconfirmedTransactionsRequest);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetUnconfirmedTransactionsResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 节点的存储空间满了,向云中心提出卸载请求
     * @param esfullrequest
     * @return
     */
    @Override
    public Esfullresponse Esfull(Esfullrequest esfullrequest) {
        try{
            String requestUrl=getCloundUrl(API.EsFull);
            String requestBody = JsonUtil.toString(esfullrequest);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, Esfullresponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }


    /**
     * 构建url
     * @param api  节点接口常量
     * @return
     */
    private String getUrl(String api) {
        return "http://" + ip + ":" + Integer.valueOf(API.EsPort) + api;
    }


    /**
     * 构建向iot层的url
     * @param api
     * @return
     */
    private String getiotUrl(String api){
        return "http://"+IotIp+":"+ Integer.valueOf(API.IotPort)+api;
    }

    /**
     * 构建向网关层的url
     * @param api
     * @return
     */
    private String getGatewayIp(String api){
        return "http://"+GatewayIp+":"+ Integer.valueOf(API.GateWayPort)+api;
    }


    /**
     * 构建向Ca的url
     * @param api
     * @return
     */
    private  String getCaUrl(String api){
        return "http://"+CaIp+":"+ Integer.valueOf(API.CAPort)+api;
    }

    /**
     * 构建向Clound的url
     * @param api
     * @return
     */
    private  String getCloundUrl(String api){
        return "http://"+API.CloundIp+":"+ Integer.valueOf(API.Cport)+api;
    }
}
