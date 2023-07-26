package com.zl.blockIot.net.configuration;


/**
 * NetCore 配置：
 *
 */
public interface NetcoreConfiguration {

    /**
     * 获取服务器端口号
     * @return
     */
    int getport();

    /**
     * 获取设备编号
     * @return
     */
    String getEquipmentNumber();


}
