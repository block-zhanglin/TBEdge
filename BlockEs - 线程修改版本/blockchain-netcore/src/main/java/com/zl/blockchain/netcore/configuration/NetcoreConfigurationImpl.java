package com.zl.blockchain.netcore.configuration;

import com.zl.blockchain.dto.API;
import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.FileUtil;
import com.zl.blockchain.util.KvDbUtil;

public class NetcoreConfigurationImpl implements NetcoreConfiguration {

    private int port= Integer.valueOf(API.EsPort);//服务器端口号

    private String EsNumber=API.EsNumber;//设备编号

    private String netCorePath;  //netcore储存地址

    private static final String NETCORE_CONFIGURATION_DATABASE_NAME = "NetCoreConfigurationDatabase"; //netcore配置数据库

    //区块搜索器"是否是自动搜索新区块"状态存入到数据库时的主键
    private static final String AUTO_SEARCH_BLOCK_OPTION_KEY = "IS_AUTO_SEARCH_BLOCK";

    //区块搜索器"是否是自动搜索新区块"开关的默认状态
    private static final boolean AUTO_SEARCH_BLOCK_OPTION_DEFAULT_VALUE = true;


    //在区块链网络中自动搜寻新的区块的间隔时间。10分钟
     private static final long SEARCH_BLOCKS_TIME_INTERVAL = 1000 * 60 *10;




    //区块广播时间间隔。 如果自己是主节点则每5秒广播一次自己的最高区块
    private static final long BLOCK_BROADCASTER_TIME_INTERVAL = 1000 * 5;


    //区块高度广播时间间隔 1分钟广播一次自己的最高高度
    private static final long BLOCKCHAIN_HEIGHT_BROADCASTER_TIME_INTERVAL = 1000 * 20;


    //节点搜索器'是否自动搜索节点'状态存入到数据库时的主键
    private static final String AUTO_SEARCH_NODE_OPTION_KEY = "IS_AUTO_SEARCH_NODE";

    //节点搜索器'是否自动搜索节点'开关的默认状态
    private static final boolean AUTO_SEARCH_NODE_OPTION_DEFAULT_VALUE = true;


    //在区块链网络中自动搜寻新的节点的间隔时间。20秒
    private static final long SEARCH_NODE_TIME_INTERVAL = 1000 * 20;

    //广播自己节点的时间间隔。 20秒
    private static final long NODE_BROADCAST_TIME_INTERVAL = 1000 *20;

    //清理死亡节点的时间间隔。10分钟
    private static final long NODE_CLEAN_TIME_INTERVAL = 1000 * 60 * 10;


    //在区块链网络中自动搜索节点的区块链高度的时间间隔。  每隔1分钟自动搜索节点的区块链高度
    private static final long SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL = 1000 * 20;


    //定时将种子节点加入本地区块链网络的时间间隔。 每隔1分钟种子节点加入一次区块链网络
    private static final long ADD_SEED_NODE_TIME_INTERVAL = 1000 * 20;


    //两个区块链有分叉时，区块差异数量大于这个值，则硬分叉了。
    private static final long HARD_FORK_BLOCK_COUNT = 100000000;

    //在区块链网络中搜寻未确认交易的间隔时间。2分钟
    private static final long SEARCH_UNCONFIRMED_TRANSACTIONS_TIME_INTERVAL = 1000 * 60 * 2;

    /**
     * 节点配置类实现
     * @param netCorePath 节点数据存储地址
     */
    public NetcoreConfigurationImpl(String netCorePath) {
        FileUtil.makeDirectory(netCorePath); //创建节点数据存储目录
        this.netCorePath = netCorePath;
    }

    /**
     * 获取节点数据储存地址
     * @return
     */
    @Override
    public String getNetCorePath() {
        return netCorePath; //返回节点数据储存地址
    }

    /**
     *  是否自动搜索区块
     * @return
     */
    @Override
    public boolean isAutoSearchBlock() {
        byte[] bytesConfigurationValue = getConfigurationValue(ByteUtil.stringToUtf8Bytes(AUTO_SEARCH_BLOCK_OPTION_KEY));
        if(bytesConfigurationValue == null){
            return AUTO_SEARCH_BLOCK_OPTION_DEFAULT_VALUE;
        }
        return ByteUtil.utf8BytesToBoolean(bytesConfigurationValue);
    }

    /**
     * 启动自动搜寻区块
     */
    @Override
    public void activeAutoSearchBlock() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(AUTO_SEARCH_BLOCK_OPTION_KEY),ByteUtil.booleanToUtf8Bytes(true));
    }

    /**
     * 关闭自动搜寻区块
     */
    @Override
    public void deactiveAutoSearchBlock() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(AUTO_SEARCH_BLOCK_OPTION_KEY),ByteUtil.booleanToUtf8Bytes(false));
    }

    /**
     * 是否自动搜寻节点
     * @return
     */
    @Override
    public boolean isAutoSearchNode() {
        byte[] bytesConfigurationValue = getConfigurationValue(ByteUtil.stringToUtf8Bytes(AUTO_SEARCH_NODE_OPTION_KEY));
        if(bytesConfigurationValue == null){
            return AUTO_SEARCH_NODE_OPTION_DEFAULT_VALUE;
        }
        return ByteUtil.utf8BytesToBoolean(bytesConfigurationValue);
    }

    /**
     * 启动自动搜寻节点
     */
    @Override
    public void activeAutoSearchNode() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(AUTO_SEARCH_NODE_OPTION_KEY),ByteUtil.booleanToUtf8Bytes(true));
    }

    /**
     * 关闭自动搜寻节点
     *
     */
    @Override
    public void deactiveAutoSearchNode() {
        addOrUpdateConfiguration(ByteUtil.stringToUtf8Bytes(AUTO_SEARCH_NODE_OPTION_KEY),ByteUtil.booleanToUtf8Bytes(false));
    }
    /**
     * 获取节点搜寻时间间隔
     * @return
     */
    @Override
    public long getNodeSearchTimeInterval() {
        return SEARCH_NODE_TIME_INTERVAL;
    }

    /***
     * 获取区块链高度搜寻时间间隔
     * @return
     */
    @Override
    public long getBlockchainHeightSearchTimeInterval() {
        return SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL;
    }

    /**
     * 获取区块搜寻时间间隔
     * @return
     */
    @Override
    public long getBlockSearchTimeInterval() {
        return SEARCH_BLOCKS_TIME_INTERVAL;
    }

    /**
     * 获取区块链高度广播时间间隔
     * @return
     */
    @Override
    public long getBlockchainHeightBroadcastTimeInterval() {
        return BLOCKCHAIN_HEIGHT_BROADCASTER_TIME_INTERVAL;
    }

    /**
     * 获取区块广播时间间隔
     * @return
     */
    @Override
    public long getBlockBroadcastTimeInterval() {
        return BLOCK_BROADCASTER_TIME_INTERVAL;
    }

    /**
     * 获取种子节点定时加入区块链时间间隔
     * @return
     */
    @Override
    public long getSeedNodeInitializeTimeInterval() {
        return ADD_SEED_NODE_TIME_INTERVAL;
    }

    /**
     * 获取节点广播时间间隔
     * @return
     */
    @Override
    public long getNodeBroadcastTimeInterval() {
        return NODE_BROADCAST_TIME_INTERVAL;
    }

    /**
     * 两个区块链有分叉时，区块差异数量大于这个值，则硬分叉了
     * @return
     */
    @Override
    public long getHardForkBlockCount() {
        return HARD_FORK_BLOCK_COUNT;
    }

    /**
     * 获得未确认交易搜寻时间间隔
     * @return
     */
    @Override
    public long getUnconfirmedTransactionsSearchTimeInterval() {
        return SEARCH_UNCONFIRMED_TRANSACTIONS_TIME_INTERVAL;
    }

    /**
     * 获取清理死亡节点的时间间隔
     * @return
     */
    @Override
    public long getNodeCleanTimeInterval() {
        return NODE_CLEAN_TIME_INTERVAL;
    }





    @Override
    public int getport() {
        return port;
    }

    @Override
    public String getEsNumber() {
        return EsNumber;
    }

    /**
     * 获取配置文件数据库中的 value
     * @param configurationKey 配置文件中的 key
     * @return
     */
    private byte[] getConfigurationValue(byte[] configurationKey) {
        byte[] bytesConfigurationValue = KvDbUtil.get(getNetCoreConfigurationDatabasePath(), configurationKey); //从kv数据库中获取<key,value>
        return bytesConfigurationValue; //返回value
    }

    /**
     * 更新配置文件数据库中的<key,value>
     * @param configurationKey key
     * @param configurationValue value
     */
    private void addOrUpdateConfiguration(byte[] configurationKey, byte[] configurationValue) {
        KvDbUtil.put(getNetCoreConfigurationDatabasePath(), configurationKey, configurationValue); //kv数据库更新<key,value>
    }

    /**
     * 获取netcore配置数据库地址
     * @return
     */
    private String getNetCoreConfigurationDatabasePath(){
        return FileUtil.newPath(netCorePath, NETCORE_CONFIGURATION_DATABASE_NAME);
    }

}
