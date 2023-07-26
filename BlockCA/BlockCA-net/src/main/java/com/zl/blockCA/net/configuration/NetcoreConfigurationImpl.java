package com.zl.blockCA.net.configuration;

public class NetcoreConfigurationImpl implements NetcoreConfiguration{

    private int port; //服务器端口号
    public NetcoreConfigurationImpl(int port){
        this.port=port;
    }

    @Override
    public int getport() {
        return port;
    }



}
