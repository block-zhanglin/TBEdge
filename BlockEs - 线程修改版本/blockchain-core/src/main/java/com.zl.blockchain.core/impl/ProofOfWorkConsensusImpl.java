package com.zl.blockchain.core.impl;

import com.zl.blockchain.core.BlockchainDatabase;
import com.zl.blockchain.core.Consensus;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.BlockDto;
import com.zl.blockchain.setting.BlockSetting;

/**

 *
 */

public class ProofOfWorkConsensusImpl extends Consensus {

    /**
     * 检验共识（区块链数据库、区块）
     * @param blockchainDatabase
     * @param
     * @return
     */
    @Override
    public boolean checkConsensus(BlockchainDatabase blockchainDatabase, BlockDto blockDto) {

        long limit=Integer.valueOf(BlockSetting.CommitteeCount)*1/3+1; //需要收到委员会中作恶节点数目+1
        //System.out.println("共识界限"+limit);

        if(API.number.containsKey(blockDto.getTimestamp())){  //判断已经是否收到过该区块
            if(API.number.get(blockDto.getTimestamp())+1>=limit){  //如果收到了2/3以上的节点相同区块
                API.number.remove(blockDto.getTimestamp());//hashmap中删除该blockdto
                //System.out.println("成功收到2/3以上的相同区块");
                return true;//则达成共识，存入本地
            }else {
                API.number.put(blockDto.getTimestamp(),API.number.get(blockDto.getTimestamp())+1);
                //System.out.println(blockDto.getPreviousHash()+"已经收到几次"+API.number.get(blockDto.getTimestamp()));
            }
        }else {
            if(1L>=limit){
                return true;
            }else {
                API.number.put(blockDto.getTimestamp(), 1L);//第一次收到该区块
                //System.out.println(blockDto.getPreviousHash()+"第一次接收改区块");
            }
        }
        return false;
    }
}
