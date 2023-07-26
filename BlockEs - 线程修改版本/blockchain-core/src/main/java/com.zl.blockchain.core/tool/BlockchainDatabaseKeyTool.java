package com.zl.blockchain.core.tool;

import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.StringUtil;

/**
 * 区块链数据库主键工具类
 *
 * 数据库主键，指的是一个列或多列的组合，其值能唯一地标识表中的每一行，通过它可强制表的实体完整性；主键主要是用于其他表的外键关联，以及本记录的修改与删除。当创建或更改表时可通过定义PRINARY KEY约束来创建主键。
 * 一个表只能有一个primary key约束，而且primary key约束中的列不能接受空值。由于primary key约束确保唯一数据，所以经常用来定义表示列。
 *
 * 该类主要给区块、交易、交易输出、地址加上前缀
 */
public class BlockchainDatabaseKeyTool {

    //区块链标识：它对应的值是区块链的高度
    private static final String BLOCKCHAIN_HEIGHT_KEY = "A";

    //区块链标识：它对应的值是区块链的交易高度
    private static final String BLOCKCHAIN_TRANSACTION_HEIGHT_KEY = "B";

    //哈希标识：哈希(区块哈希、交易哈希)的前缀
    private static final String HASH_PREFIX_FLAG = "D";

    //区块标识：存储区块链高度到区块的映射
    private static final String BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG = "E";

    //区块标识：存储区块Hash到区块高度的映射
    private static final String BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG = "F";

    //交易标识：存储交易高度到交易的映射
    private static final String TRANSACTION_HEIGHT_TO_TRANSACTION_PREFIX_FLAG = "G";

    //交易标识：存储交易哈希到交易高度的映射
    private static final String TRANSACTION_HASH_TO_TRANSACTION_HEIGHT_PREFIX_FLAG = "H";

    //地址标识：存储地址
    private static final String ADDRESS_PREFIX_FLAG = "O";

    //截止标识
    private static final String END_FLAG = "#" ;

    //竖线分隔符
    private static final String VERTICAL_LINE_FLAG = "|" ;

    //拼装数据库Key的值
    public static byte[] buildBlockchainHeightKey() {
        String stringKey = BLOCKCHAIN_HEIGHT_KEY + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildHashKey(String hash) {
        String stringKey = HASH_PREFIX_FLAG + hash + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildAddressKey(String address) {
        String stringKey = ADDRESS_PREFIX_FLAG + address + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildBlockHeightToBlockKey(long blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG + StringUtil.valueOfUint64(blockHeight) + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildBlockHashToBlockHeightKey(String blockHash) {
        String stringKey = BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG + blockHash + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
    public static byte[] buildTransactionHashToTransactionHeightKey(String transactionHash) {
        String stringKey = TRANSACTION_HASH_TO_TRANSACTION_HEIGHT_PREFIX_FLAG + transactionHash + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }


    public static byte[] buildBlockchainTransactionHeightKey() {
        String stringKey = BLOCKCHAIN_TRANSACTION_HEIGHT_KEY + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }

    public static byte[] buildTransactionHeightToTransactionKey(long transactionHeight) {
        String stringKey = TRANSACTION_HEIGHT_TO_TRANSACTION_PREFIX_FLAG + transactionHeight + END_FLAG;
        return ByteUtil.stringToUtf8Bytes(stringKey);
    }
}