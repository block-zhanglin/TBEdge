package com.zl.blockIot.net.configuration;

public class NetcoreConfigurationImpl implements NetcoreConfiguration{

    private int port; //服务器端口号

    private String EquipmentNumber;//设备编号


    public NetcoreConfigurationImpl(int port,String e){
        this.port=port;
        this.EquipmentNumber=e;
    }

    @Override
    public int getport() {
        return port;
    }

    @Override
    public String getEquipmentNumber() {
        return EquipmentNumber;
    }


}
