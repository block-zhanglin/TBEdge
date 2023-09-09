package com.zl.blockchain.core.tool;

import com.zl.blockchain.crypto.MerkleTreeUtil;
import com.zl.blockchain.crypto.Sha256Util;
import com.zl.blockchain.dto.*;

import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块dto工具类
 *

 */
public class BlockDtoTool {

    /**
     * 计算区块的Hash值
     *
     * 利用区块的时间戳、前一区块的hash、merkle树的根hash值
     */
    public static String calculateBlockHash(BlockDto blockDto) {
        byte[] bytesTimestamp = ByteUtil.uint64ToBytes(blockDto.getTimestamp());   //获取时间戳
        byte[] bytesPreviousBlockHash = ByteUtil.hexStringToBytes(blockDto.getPreviousHash());  //获取前一区块的hash
        byte[] bytesMerkleTreeRoot = ByteUtil.hexStringToBytes(calculateBlockMerkleTreeRoot(blockDto)); //获取merkle树根hash

        byte[] bytesBlockHeader = ByteUtil.concatenate3(bytesTimestamp,bytesPreviousBlockHash,bytesMerkleTreeRoot);  //将时间戳、前一区块hash、merkle树根hash进行连接
        byte[] bytesBlockHash = Sha256Util.doubleDigest(bytesBlockHeader);   //利用sha256进行双重hash摘要
        return ByteUtil.bytesToHexString(bytesBlockHash); //将字节数字转为16进制字符串
    }

    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(BlockDto blockDto) {
        List<TransactionDto> transactions = blockDto.getTransactions(); //从blockdto类获取所有的交易 (一个个的transactiondto)
        List<byte[]> bytesTransactionHashs = new ArrayList<>(); //transaction字节数组 列表
        if(transactions != null){
            for(TransactionDto transactionDto : transactions) {
                String transactionHash = TransactionDtoTool.calculateTransactionHash(transactionDto);  //计算交易hash
                byte[] bytesTransactionHash = ByteUtil.hexStringToBytes(transactionHash);   //将交易hash 转为字节数组
                bytesTransactionHashs.add(bytesTransactionHash);
            }
        }
        if(MerkleTreeUtil.calculateMerkleTreeRoot(bytesTransactionHashs)==null){
            return null;
        }
        return ByteUtil.bytesToHexString(MerkleTreeUtil.calculateMerkleTreeRoot(bytesTransactionHashs));  //计算交易的根hash值
    }

    /**
     * 简单的校验两个区块是否相等

     */
    public static boolean isBlockEquals(BlockDto block1, BlockDto block2) {
        String block1Hash = calculateBlockHash(block1);
        String block2Hash = calculateBlockHash(block2);
        return StringUtil.equals(block1Hash, block2Hash);
    }
}
