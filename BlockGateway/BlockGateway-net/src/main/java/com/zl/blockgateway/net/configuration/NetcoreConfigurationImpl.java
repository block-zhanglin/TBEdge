package com.zl.blockgateway.net.configuration;

public class NetcoreConfigurationImpl implements NetcoreConfiguration {

    private int port;//服务器端口号

    private String GatewayNumber;//设备编号

    @Override
    public int getport() {
        return port;
    }

    @Override
    public String getGatewayNumber() {
        return GatewayNumber;
    }

    public NetcoreConfigurationImpl(int port,String number){

        this.port=port;
        this.GatewayNumber=number;

    }

}
