package com.zl.blockCA.net.client;


import com.zl.blockCA.net.Server.API;
import com.zl.blockCA.net.dao.*;
import util.JsonUtil;
import util.NetUtil;

public class HttpClientImpl implements HttpClient {

    private String ip;//节点的ip地址

    public HttpClientImpl(String ip){
        this.ip=ip;
    }


    @Override
    public PingResponse pingNode(PingRequest request) {
        try {
            String requestUrl = getUrl(API.PING); //获取url
//            System.out.println(requestUrl);
            String requestBody = JsonUtil.toString(request); //请求头
            String responseHtml = NetUtil.get(requestUrl,requestBody); //返回体
            return JsonUtil.toObject(responseHtml,PingResponse.class); //得到的string类型的数据转换为object
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request) {
        try {
            String requestUrl = getUrl(API.GET_BLOCKCHAIN_HEIGHT);
            String requestBody = JsonUtil.toString(request);
            String responseHtml = NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml, GetBlockchainHeightResponse.class);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public PostCommitteeResponse postcommittee(PostCommitteeRequest request) {
        try{
            String requestUrl=getUrl(API.POST_COMMITTEE);
            String requestBody=JsonUtil.toString(request);
            String responseHtml=NetUtil.get(requestUrl,requestBody);
            return JsonUtil.toObject(responseHtml,PostCommitteeResponse.class);
        }catch (Exception e){
            System.out.println("委员会发布出错");
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


}
