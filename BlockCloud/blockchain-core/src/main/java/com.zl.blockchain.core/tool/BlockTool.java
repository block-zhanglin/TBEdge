package com.zl.blockchain.core.tool;

import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.dto.*;

import com.zl.blockchain.setting.GenesisBlockSetting;
import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.StringUtil;
import com.zl.blockchain.util.StringsUtil;
import com.zl.blockchain.util.TimeUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.zl.blockchain.dto.*;


/**
 * 区块工具类
 *
 * 计算block的相关信息，首先先将block转换为blockdto，然后利用blockdtotool计算响应值
 */
public class BlockTool {

    /**
     * 计算区块的Hash值
     */
    public static String calculateBlockHash(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return BlockDtoTool.calculateBlockHash(blockDto);
    }


    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return BlockDtoTool.calculateBlockMerkleTreeRoot(blockDto);
    }


    /**
     * 区块新产生的哈希是否存在重复
     */
    public static boolean isExistDuplicateNewHash(Block block) {
        List<String> newHashs = new ArrayList<>();
        String blockHash = block.getHash();
        newHashs.add(blockHash);
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction : transactions){
                String transactionHash = transaction.getTransactionHash();
                newHashs.add(transactionHash);
            }
        }
        return StringsUtil.hasDuplicateElement(newHashs);
    }


    /**
     * 校验区块的前区块哈希
     */
    public static boolean checkPreviousBlockHash(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return StringUtil.equals(GenesisBlockSetting.HASH,currentBlock.getPreviousHash());
        } else {
            return StringUtil.equals(previousBlock.getHash(),currentBlock.getPreviousHash());
        }
    }


    /**
     * 检验区块的hash是否正确
     * @param block
     * @return
     */
    public static  boolean checkBlockHash(Block block){
        String  blockhash=calculateBlockHash(block);
        return StringUtil.equals(blockhash,block.getHash());

    }

    /**
     * 检验区块的merkleRoot是否正确
     * @param block
     * @return
     */
    public static  boolean checkMerkle(Block block){
        String merkle=calculateBlockMerkleTreeRoot(block);
        return StringUtil.equals(merkle,block.getMerkleTreeRoot());
    }

    /**
     * 校验区块高度的连贯性
     */
    public static boolean checkBlockHeight(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            System.out.println(currentBlock.getHeight());
            return (GenesisBlockSetting.HEIGHT +1) == currentBlock.getHeight();
        } else {
            return (previousBlock.getHeight()+1) == currentBlock.getHeight();
        }
    }


    /**
     * 校验区块的时间
     * 区块时间戳一定要比当前时间戳小。挖矿是个技术活，默认矿工有能力将自己机器的时间调整正确，所以矿工不应该穿越到未来挖矿。
     * 区块时间戳一定要比前一个区块的时间戳大。
     */
    public static boolean checkBlockTimestamp(Block previousBlock, Block currentBlock) {
        if(currentBlock.getTimestamp() > TimeUtil.millisecondTimestamp()){
            return false;
        }
        if(previousBlock == null){
            return true;
        } else {
            return currentBlock.getTimestamp() > previousBlock.getTimestamp();
        }
    }


    /**
     * 获取区块中交易的数量
     */
    public static long getTransactionCount(Block block) {
        List<Transaction> transactions = block.getTransactions();
        return transactions == null?0:transactions.size();
    }


    /**
     * 简单的校验两个区块是否相等
     * 注意：这里没有严格校验,例如没有校验区块中的交易是否完全一样
     * ，所以即使这里认为两个区块相等，实际上这两个区块还是有可能不相等的。
     */
    public static boolean isBlockEquals(Block block1, Block block2) {
        return StringUtil.equals(block1.getHash(), block2.getHash());
    }

    /**
     * 获取下一个区块的高度
     */
    public static long getNextBlockHeight(Block currentBlock) {
        long nextBlockHeight = currentBlock==null? GenesisBlockSetting.HEIGHT+1:currentBlock.getHeight()+1;
        return nextBlockHeight;
    }


    /**
     * 验证区块的签名
     * @param block
     * @return
     */
    public static  boolean checkBlockSig(Block block){
        ToCaresponse toCaresponse=block.getToCaresponse(); //获取block中的证书
        //验证证书
        Boolean b= AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8), ByteUtil.hexStringToBytes(toCaresponse.getSig()));
        if(b){
            //通过证书中的公钥验证签名
            byte[]bb=block.getHash().getBytes(StandardCharsets.UTF_8); //获取被签名的数据（区块的hash）
            Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),bb,ByteUtil.hexStringToBytes(block.getSig()));
            if(c){

                return  true;
            }
            return false;
        }
        return false;
    }

}
