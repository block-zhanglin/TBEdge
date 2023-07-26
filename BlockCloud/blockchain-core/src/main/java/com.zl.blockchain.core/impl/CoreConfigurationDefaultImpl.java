package com.zl.blockchain.core.impl;

import com.zl.blockchain.core.CoreConfiguration;
import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.FileUtil;
import com.zl.blockchain.util.KvDbUtil;

/**
 * 配置文件类默认实现
 *
 */
public class CoreConfigurationDefaultImpl extends CoreConfiguration {

    //配置数据库名字
    private static final String CONFIGURATION_DATABASE_NAME = "ConfigurationDatabase";

    //'矿工是否是激活状态'存入到数据库时的主键
    private static final String MINE_OPTION_KEY = "IS_MINER_ACTIVE";

    //'矿工可挖的最高区块高度'存入到数据库时的主键
    private static final String MINER_MINE_MAX_BLOCK_HEIGHT_KEY = "MINER_MINE_MAX_BLOCK_HEIGHT";

    //'矿工是否是激活状态'的默认值
    private static final boolean MINE_OPTION_DEFAULT_VALUE = false;

    //这个时间间隔更新一次正在被挖矿的区块的交易。如果时间太长，可能导致新提交的交易延迟被确认。
    public static final long MINE_TIMESTAMP_PER_ROUND = 1000 * 10;

    /**
     * 构造函数（corepath）
     * @param corePath
     */
    public CoreConfigurationDefaultImpl(String corePath) { //配置文件默认实现
        FileUtil.makeDirectory(corePath);
        this.corePath = corePath;
    }


    /**
     * 获取corepath
     * @return
     */
    @Override
    public String getCorePath() { //获取
        return corePath; //BlockchainCore数据存放路径
    }


    /**
     * 获取配置数据库地址
     * @return
     */
    private String getConfigurationDatabasePath(){
        return FileUtil.newPath(corePath, CONFIGURATION_DATABASE_NAME);
    }

    /**
     * 获取配置数据库中指定key的值
     * @param configurationKey
     * @return
     */
    private byte[] getConfigurationValue(byte[] configurationKey) {
        return KvDbUtil.get(getConfigurationDatabasePath(), configurationKey);
    }

    /**
     * 更新配置数据库的<key，value>
     * @param configurationKey
     * @param configurationValue
     */
    private void addOrUpdateConfiguration(byte[] configurationKey, byte[] configurationValue) {
        KvDbUtil.put(getConfigurationDatabasePath(), configurationKey, configurationValue);
    }
}
