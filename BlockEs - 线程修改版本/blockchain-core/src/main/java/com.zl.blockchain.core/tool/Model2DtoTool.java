package com.zl.blockchain.core.tool;

import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * model转dto工具
 *
 * model转成dto工具
 */
public class Model2DtoTool {

    /**
     * 区块转换为blockdto
     * @param block
     * @return
     */
    public static BlockDto block2BlockDto(Block block) {
        List<TransactionDto> transactionDtos = new ArrayList<>();
        if(block.getTransactions()==null){
            return null;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                TransactionDto transactionDto = transaction2TransactionDto(transaction);  //将transaction转换为transactiondto
                transactionDtos.add(transactionDto);
            }
        }

        BlockDto blockDto = new BlockDto();  //构建blockdto
        blockDto.setTimestamp(block.getTimestamp());  //设置时间戳
        blockDto.setPreviousHash(block.getPreviousHash()); //设置前一区块hash
        blockDto.setTransactions(transactionDtos); //设置transactiondto
        blockDto.setEsCA(block.getToCaresponse());
        blockDto.setSig(block.getSig());
        blockDto.setCommittee(block.getCommittee());
        return blockDto;
    }

    /**
     * transaction 转换为transactiondto
     * @param transaction
     * @return
     */
    public static TransactionDto transaction2TransactionDto(Transaction transaction) {
        TransactionDto transactionDto=new TransactionDto();
        transactionDto.setTaskNumber(transaction.getTaskNumber());
        transactionDto.setEsNumber(transaction.getEsNumber());
        transactionDto.setDatahash(transaction.getDatahash());
        transactionDto.setDatalocation(transaction.getDatalocation());
        transactionDto.setaBoolean(transaction.getaBoolean());
        transactionDto.setEsSig(transaction.getEsSig());
        transactionDto.setEsCA(transaction.getEsCA());
        transactionDto.setIotSig(transaction.getIotSig());
        transactionDto.setIotCA(transaction.getIotCA());
        return transactionDto;
    }


}
