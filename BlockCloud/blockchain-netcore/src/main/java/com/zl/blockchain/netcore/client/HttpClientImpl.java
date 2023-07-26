package com.zl.blockchain.netcore.client;

import com.zl.blockchain.dto.*;
import com.zl.blockchain.util.JsonUtil;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.NetUtil;

public class HttpClientImpl implements HttpClient{

    private String ip;//节点的ip地址

    private  String CaIp;

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
    public void setIp(String ip) {  //设置节点的ip地址
        this.ip = ip;
    }
    public String getIp() { //获取节点的ip地址
        return ip;
    }




    //向CA发出证书申请
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
     * ping 节点(分开为云中心节点和ES节点)
     * @param request
     * @return
     */
    @Override
    public PingResponse pingNode(PingRequest request,int i) {
        try {
            String requestUrl="";
            if(i==0){
              requestUrl = getUrl(API.PING); //获取url
            }
            if(i==1){
                requestUrl=getEsUrl(API.PING);//获取Es层url
            }
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
    public GetBlockResponse getBlock(GetBlockRequest request,int i) {

        try {
            String requestUrl="";
            if(i==0){
                requestUrl = getUrl(API.GET_BLOCK); //获取url
            }if(i==1){
                requestUrl = getEsUrl(API.GET_BLOCK); //获取url
            }
            String requestBody = JsonUtil.toString(request); //请求头 （区块高度）
            String responseHtml = NetUtil.get(requestUrl,requestBody);  //返回体
            return JsonUtil.toObject(responseHtml,GetBlockResponse.class);//得到的string类型的数据转换为object
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    /**
     * 获取节点(分开为云中心节点和ES节点)
     * @param request 获取节点请求
     * @return
     */
    @Override
    public GetNodesResponse getNodes(GetNodesRequest request,int i) {
        try {
            String requestUrl="";
            if(i==0){
                requestUrl = getUrl(API.GET_NODES); //获取url
            }
            if(i==1){
                requestUrl = getEsUrl(API.GET_NODES); //获取url
            }
            System.out.println(requestUrl);
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
    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request,int i) {
        try {
            String requestUrl="";
            if(i==0){
                requestUrl = getUrl(API.GET_BLOCKCHAIN_HEIGHT);
            }
           if(i==1){
               requestUrl = getEsUrl(API.GET_BLOCKCHAIN_HEIGHT);
           }
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetBlockchainHeightResponse.class);
        }catch (Exception e){
            LogUtil.error("client error.",e);
            return null;
        }
    }

    @Override
    public blockunloadresponse blockunload(blockunloadrequest blockunloadrequest) {
        String requestUrl=getEsUrl(API.BLOCK_UNLOAD);
        String requestBody = JsonUtil.toString(blockunloadrequest);
        String responseHtml = NetUtil.get(requestUrl,requestBody);
        return JsonUtil.toObject(responseHtml, blockunloadresponse.class);
    }


    /**
     * 构建url
     * @param api  节点接口常量
     * @return
     */
    private String getUrl(String api) {
        return "http://" + ip + ":" + Integer.valueOf(API.Cport) + api;
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
     * 构建向Es层的url
     */
    private String getEsUrl(String api){
        return "http://" + ip + ":" + Integer.valueOf(API.ESPort) + api;
    }

}
