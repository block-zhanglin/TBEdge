package com.zl.blockgateway.net.configuration;


/**
 * NetCore 配置：
 *  该类对netcore模块的配置进行统一的管理。在这里可以持久化配置信息。理论上，BlockchainNetCore模块的任何地方需要配置参数，都可以从该类获取
 */
public interface NetcoreConfiguration {

    /**
     * 获取服务器端口号
     * @return
     */
    int getport();

    /**
     * 获取网关设备编号
     * @return
     */
    String getGatewayNumber();


}

