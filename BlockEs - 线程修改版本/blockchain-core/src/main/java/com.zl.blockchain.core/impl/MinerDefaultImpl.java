package com.zl.blockchain.core.impl;

import com.zl.blockchain.core.BlockchainDatabase;
import com.zl.blockchain.core.CoreConfiguration;
import com.zl.blockchain.core.Miner;
import com.zl.blockchain.core.UnconfirmedTransactionDatabase;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.core.tool.BlockTool;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.core.tool.TransactionDtoTool;
import com.zl.blockchain.core.tool.TransactionTool;
import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.*;
import com.zl.blockchain.setting.BlockSetting;
import com.zl.blockchain.setting.GenesisBlockSetting;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;
import com.zl.blockchain.util.TimeUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * 挖矿、将挖取的区块放入区块链中
 *
 */
public class MinerDefaultImpl extends Miner {


    /**矿工类构造函数
     * @param coreConfiguration 配置文件

     * @param blockchainDatabase 区块链数据库
     * @param unconfirmedTransactionDatabase 未确认数据库
     */
    public MinerDefaultImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {
        super(coreConfiguration, blockchainDatabase, unconfirmedTransactionDatabase); //调用父类的构造方法
    }
    //endregion


    @Override
    /**
     * 启用矿工
     * 矿工有两种状态：活动状态和非活动状态
     * 若矿工处于活动状态，则会进行挖矿劳作
     * 若矿工处于非活动状态、则不会进行任何工作
     */
    public void start(Account account, ToCaresponse toCaresponse) {

        while (true){  //一直循环


            ThreadUtil.millisecondSleep(10*1000); //防止空跑，浪费CPU。线程睡眠，百分之一秒

            long blockChainHeight = blockchainDatabase.queryBlockchainHeight(); // 获取当前区块链数据库的高度
            LogUtil.debug(String.valueOf(blockChainHeight));
            //当前委员会
            Committee committee= API.committee;
            if(committee==null){
                continue;
            }
            List<Node> nodeList=committee.getNodes();//节点
            List<String>ips=new ArrayList<>();
            for(Node node:nodeList){
                ips.add(node.getIp());
            }

            //如果当前委员会中不包含该节点，则跳过循环
            if(!ips.contains(API.Esip)){
                API.Master=false;
                continue;
            }

            int blockposition=ips.indexOf(toCaresponse.getIp());
//            System.out.println("在委员会中的顺序"+blockposition);
//            System.out.println("委员会发布时间"+committee.getBegintime()+"委员会时间"+BlockSetting.CommitteeTime);
            long blockmintime=committee.getBegintime()+blockposition* Long.valueOf(BlockSetting.CommitteeTime)/Integer.valueOf(BlockSetting.CommitteeCount);  //可广播区块时间下界
            long blockmaxtime=committee.getBegintime()+(blockposition+1)*Long.valueOf(BlockSetting.CommitteeTime)/Integer.valueOf(BlockSetting.CommitteeCount); //可区块时间上界
//            System.out.println("上界"+blockmaxtime+"下界"+blockmintime);
            if(TimeUtil.millisecondTimestamp()>=blockmintime&&TimeUtil.millisecondTimestamp()<=blockmaxtime){
//                System.out.println("当前时刻"+TimeUtil.millisecondTimestamp());
                API.Master=true;
                //同步当前的最长区块链
            }else {
                API.Master=false;
                continue;
            }

            if(!isActive()){  //判断矿工是否处于活动状态
                System.out.println("矿工未激活");
                continue;   //如果矿工不处于活动状态，则跳过以下代。下一个循环
            }
            if(blockChainHeight >= coreConfiguration.getMinerMineMaxBlockHeight()){  //判断“当前区块链的高度”是否大于“矿工最大被允许的挖矿高度”
                continue;
            }

            /**
             * 判断未确认数据库中是否有数据
             */
            List<TransactionDto> ts = new ArrayList<>();
            ts=unconfirmedTransactionDatabase.selectTransactions(1,20);
            if(ts.size()==0){
                System.out.println("交易无");
                continue;
            }

            Block block = buildMiningBlock(blockchainDatabase,unconfirmedTransactionDatabase); //构建挖矿区块

            /**
             * 如果区块为空
             */
            if(block==null){
                continue;
            }

            long startTimestamp = TimeUtil.millisecondTimestamp(); //获取开始时间戳

            while (true){
                if(!isActive()){ //判断矿工是否处于活动状态
                    break;  //如果矿工不处于活动状态，则退出该while循环
                }
                //在挖矿的期间，可能收集到新的交易。每隔一定的时间，重新组装挖矿中的区块，这样新收集到交易就可以被放进挖矿中的区块了。
                if(TimeUtil.millisecondTimestamp()-startTimestamp > coreConfiguration.getMinerMineTimeInterval()){  //
                    break;
                }

                //计算区块哈希
                block.setHash(BlockTool.calculateBlockHash(block)); //计算区块hash
                /**
                 * 构建区块的证书和签名
                 */
                block.setToCaresponse(toCaresponse);
                block.setSig(AccountUtil.signature(account.getPrivateKey(),block.getHash().getBytes(StandardCharsets.UTF_8)));

                /**
                 * 如果委员会中包含该节点
                 */
                if(ips.contains(API.Esip)){
                    //判断区块中的时间戳是否在该ip地址的规定 委员会区块时间中
                    int position=ips.indexOf(toCaresponse.getIp());

                    long mintime=committee.getBegintime()+position* Long.valueOf(BlockSetting.CommitteeTime)/Integer.valueOf(BlockSetting.CommitteeCount);  //发区块时间下界
                    long maxtime=committee.getBegintime()+(position+1)*Long.valueOf(BlockSetting.CommitteeTime)/Integer.valueOf(BlockSetting.CommitteeCount); //

                    if(block.getTimestamp()>=mintime&&block.getTimestamp()<=maxtime){
                        //System.out.println("可以挖矿");
//                        System.out.println("当前时刻"+TimeUtil.millisecondTimestamp()+mintime+maxtime);
                        block.setCommittee(committee);//区块设置委员会
                        BlockDto blockDto = Model2DtoTool.block2BlockDto(block); //业务模型转换

                        boolean isAddBlockToBlockchainSuccess = blockchainDatabase.TryMineraddBlockDto(blockDto); //尝试将区块（矿）放入区块链中
                        if(!isAddBlockToBlockchainSuccess){  //判断区块是否放入区块链中

                            LogUtil.debug("挖矿成功，但是无法放入区块链中。"); //若失败则、log书写区块放入区块链失败
                        }
                        LogUtil.debug("祝贺您！挖矿成功---等待加入区块链中！！！区块高度:"+block.getHeight()+",区块哈希:"+block.getHash());//log书写区块高度、区块hash

//                        /**
//                         * 挖矿成功后从本地的未确认数据库删除区块的交易
//                         */
//                        if(isAddBlockToBlockchainSuccess){
//                            List<TransactionDto>tr=blockDto.getTransactions();
//                            for(TransactionDto transaction : tr){
//                                String transactionHash = TransactionDtoTool.calculateTransactionHash(transaction);
//                                unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
//                            }
//                        }

                        API.blockDto=blockDto; //挖矿挖出的区块
                        //关闭矿工，等待
                        coreConfiguration.deactiveMiner();
                        API.NewBlock=true;
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 停用矿工
     */
    @Override
    public void deactive() {
        coreConfiguration.deactiveMiner();
    }

    /**
     * 激活矿工
     */
    @Override
    public void active() {
        coreConfiguration.activeMiner();
    }

    /**
     *矿工是否位激活状态
     * @return 返回一个boolean
     */
    @Override
    public boolean isActive() {
        return coreConfiguration.isMinerActive();
    }

    /**
     *
     * 设置矿工可挖掘的最高区块高度
     * @param maxHeight
     */
    @Override
    public void setMinerMineMaxBlockHeight(long maxHeight) {
         coreConfiguration.setMinerMineMaxBlockHeight(maxHeight);
    }

    /**
     * 获取矿工可挖掘的最高区块高度
     * @return
     */
    @Override
    public long getMinerMineMaxBlockHeight( ) {
        return coreConfiguration.getMinerMineMaxBlockHeight();
    }


    /**
     * 构建挖矿区块
     * @param blockchainDatabase
     * @param unconfirmedTransactionDatabase 未确认交易数据库
     * @return
     */
    private Block buildMiningBlock(BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {

        Block tailBlock = blockchainDatabase.queryTailBlock(); //当前区块查询
        Block block = new Block(); // 创建区块

        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。
        long timestamp = TimeUtil.millisecondTimestamp();//获取当前时间戳
        block.setTimestamp(timestamp); //区块设置时间戳


        if(tailBlock == null){ //如果当前区块为空
            block.setHeight(GenesisBlockSetting.HEIGHT +1); //区块高度设置为创世区块加一
            block.setPreviousHash(GenesisBlockSetting.HASH); //区块设置区块的前者hash为创世区块hash
        } else {
            block.setHeight(tailBlock.getHeight()+1); //区块设置高度为当前区块加1
            block.setPreviousHash(tailBlock.getHash());//区块设置前一hash为当前区块hash
        }
        List<Transaction> packingTransactions = packingTransactions(blockchainDatabase,unconfirmedTransactionDatabase); //确认打包那些交易
        block.setTransactions(packingTransactions);//区块设置交易为“打包的交易”

        String  merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(block); //构建区块的交易merkel树根hash
        block.setMerkleTreeRoot(merkleTreeRoot); //区块设置merkle树的根hash


        if(merkleTreeRoot==null){
            return null;
        }
        return block;  //返回无nonce区块
    }


    /**
     * 确认打包哪些'未确认交易'
     */
    private List<Transaction> packingTransactions(BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {

        long bs= Long.parseLong(API.Blocksize);
        //获取一部分未确认交易，最优的方式是获取所有未确认的交易进行处理，但是数据处理起来会很复杂，因为项目是helloworld的，所以简单的拿一部分数据即可。
        List<TransactionDto> forMineBlockTransactionDtos = unconfirmedTransactionDatabase.selectTransactions(1,bs);//从未确认数据库中批量提取交易从（1，1000）

        List<Transaction> transactions = new ArrayList<>(); //创建交易列表
        List<Transaction> backupTransactions = new ArrayList<>(); //创建备份交易列表

        if(forMineBlockTransactionDtos != null){ //未确认交易不为空
            for(TransactionDto transactionDto:forMineBlockTransactionDtos){
                try {
                    Transaction transaction = blockchainDatabase.transactionDto2Transaction(transactionDto);  //transactiondto转换为transaction
                    transactions.add(transaction);
                } catch (Exception e) {
                    String transactionHash = TransactionDtoTool.calculateTransactionHash(transactionDto);
                    LogUtil.error("类型转换异常,将从挖矿交易数据库中删除该交易["+transactionHash+"]。",e);
                    unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
                }
            }
        }

        backupTransactions.clear(); //备份交易列表清除
        backupTransactions.addAll(transactions);
        transactions.clear();  //交易列表清除
        for(Transaction transaction : backupTransactions){
            boolean checkTransaction = blockchainDatabase.checkTransaction(transaction); //验证交易
            if(checkTransaction){
                transactions.add(transaction); //交易列表添加确认成功后的交易
            }else {
                String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                System.out.println(transactionHash);
                LogUtil.debug("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
                unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
            }
        }
        return transactions;
    }

}
