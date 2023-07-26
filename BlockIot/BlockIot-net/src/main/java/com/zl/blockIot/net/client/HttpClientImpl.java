package com.zl.blockIot.net.client;

import com.zl.blockIot.net.Server.API;
import com.zl.blockIot.net.dao.*;
import util.JsonUtil;
import util.NetUtil;

public class HttpClientImpl implements  HttpClient{

    private  int maxTaskNum; //Iot客户端不断的向网关进行任务请求的最大值

    private String GatewayIp;

    private  String CaIp;

    public HttpClientImpl(){
    }

    public void setMaxTaskNum(int maxTaskNum) {
        this.maxTaskNum = maxTaskNum;
    }

    public int getMaxTaskNum() {
        return maxTaskNum;
    }

    public String getCaIp() {
        return CaIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.GatewayIp = gatewayIp;
    }

    public String getGatewayIp() {
        return GatewayIp;
    }

    public void setCaIp(String caIp) {
        CaIp = caIp;
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

    @Override
    public IottoGatewayMessageResponse IOTTO_GATEWAY_MESSAGE_RESPONSE(IottoGatewayMessageRequest iottoGatewayMessageRequest) {
        try{
            String requesturl=getGatewayUrl(API.IottoGatewayMessageRequest);
            String requestBody= JsonUtil.toString(iottoGatewayMessageRequest);
            String response= NetUtil.get(requesturl,requestBody);
            return JsonUtil.toObject(response, IottoGatewayMessageResponse.class);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 构建向网关层的url
     * @param api
     * @return
     */
    private String getGatewayUrl(String api){
        return "http://"+GatewayIp+":"+ Integer.parseInt(API.GateWayPort)+api;
    }

    /**
     * 构建向Ca的url
     * @param api
     * @return
     */
    private  String getCaUrl(String api){
        return "http://"+CaIp+":"+ Integer.parseInt(API.CAPort)+api;
    }

}
