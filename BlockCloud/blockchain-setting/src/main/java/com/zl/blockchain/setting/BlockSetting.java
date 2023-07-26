package com.zl.blockchain.setting;

/**
 * 区块设置
 */
public class BlockSetting {

    //区块最多包含的交易数量
    public static final long BLOCK_MAX_TRANSACTION_COUNT = 600;

    //区块的最大字符数量：用于限制区块的大小
    public static final long BLOCK_MAX_CHARACTER_COUNT = 1024 * 1024;


    public static    long Init_Interblockcount;

    //卸载任务的间隔区块
    public static  long INTER_BLOCK_COUNT;




    //一个区间接收到的请求卸载数量初始值为1
    public  static long unloadcount=1;


    //委员会中包含几个成员
    public  static  String CommitteeCount ="CommitteeCount";


}
