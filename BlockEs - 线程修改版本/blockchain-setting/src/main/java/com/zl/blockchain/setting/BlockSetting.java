package com.zl.blockchain.setting;

/**
 * 区块设置
 */
public class BlockSetting {

    //区块最多包含的交易数量
    public static final long BLOCK_MAX_TRANSACTION_COUNT = 100000;


    //每间隔X分钟发布一次新的委员会
    public static String CommitteeTime="CommitteeTime";

    //委员会中包含几个成员
    public static  String CommitteeCount ="CommitteeCount";


    //Es节点服务器可以容纳得最大区块数
    public static   int MaxBlock;


}
