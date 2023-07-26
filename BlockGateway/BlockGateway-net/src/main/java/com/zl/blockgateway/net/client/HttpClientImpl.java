package com.zl.blockgateway.net.client;


import com.zl.blockgateway.net.Server.API;
import com.zl.blockgateway.net.dao.*;
import util.JsonUtil;
import util.NetUtil;

public class HttpClientImpl implements HttpClient {

    private  String IotIp; //Iot层的ip

    private String  EsIp; //Es层的ip

    private  String CaIp;

    public HttpClientImpl(){
    }

    public void setEsIp(String esIp) {
        EsIp = esIp;
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

    public String getEsIp() {
        return EsIp;
    }
    public String getIotIp() {
        return IotIp;
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
     * 接受来自IOT设备的请求信息后，分配信息到ES【任务编号、数据大小、Es编号】
     *
     */
    @Override
    public GatewaytoEsMessageResponse GATEWAYTO_ES_MESSAGE_REQUEST(GatewaytoEsMessageRequest gatewaytoEsMessageRequest) {
        try{
            String requestUrl=getEsUrl(API.GatewaytoEsRequest);
            String requestBody=JsonUtil.toString(gatewaytoEsMessageRequest);
            String response=NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(response,GatewaytoEsMessageResponse.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建向Iot层的url
     */
    private String getIotUrl(String api){return "http://"+IotIp+":"+ Integer.valueOf(API.IotPort)+api;
    }

    /**
     * 构建向Es层的url
     */
    private String getEsUrl(String  api){
        return "http://"+EsIp+":"+Integer.valueOf(API.EsPort)+api;
    }

    /**
     * 构建向Ca的url
     * @param api
     * @return
     */
    private  String getCaUrl(String api){
        return "http://"+CaIp+":"+ Integer.valueOf(API.CAPort)+api;
    }

}
