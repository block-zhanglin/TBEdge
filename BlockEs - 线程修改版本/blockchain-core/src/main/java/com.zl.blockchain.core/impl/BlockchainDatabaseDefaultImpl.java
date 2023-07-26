package com.zl.blockchain.core.impl;

import com.zl.blockchain.core.BlockchainDatabase;
import com.zl.blockchain.core.Consensus;
import com.zl.blockchain.core.CoreConfiguration;
import com.zl.blockchain.core.UnconfirmedTransactionDatabase;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.enums.BlockchainAction;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.core.tool.BlockTool;
import com.zl.blockchain.core.tool.BlockchainDatabaseKeyTool;
import com.zl.blockchain.core.tool.TransactionDtoTool;
import com.zl.blockchain.core.tool.TransactionTool;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.BlockDto;
import com.zl.blockchain.dto.TransactionDto;
import com.zl.blockchain.setting.GenesisBlockSetting;
import com.zl.blockchain.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 区块链数据库
 * 注意这是一个线程不安全的实现，在并发的情况下，不保证功能的正确性
 */
public class BlockchainDatabaseDefaultImpl extends BlockchainDatabase {

    //region 变量与构造函数
    private static final String BLOCKCHAIN_DATABASE_NAME = "BlockchainDatabase"; //区块链数据库名字

    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); //读写锁

    /**
     * 区块链数据库默认实现
     * @param coreConfiguration 配置
     * @param consensus 共识
     */
    public BlockchainDatabaseDefaultImpl(CoreConfiguration coreConfiguration, Consensus consensus, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {
        super(consensus);
        this.coreConfiguration = coreConfiguration;
        this.unconfirmedTransactionDatabase=unconfirmedTransactionDatabase;
    }
    //endregion



    //region 区块增加与删除
    @Override
    public boolean addBlockDto(BlockDto blockDto) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            //检验共识
            if(!consensus.checkConsensus(this,blockDto)){
                //LogUtil.debug("未收到2/3的节点的区块");
                ThreadUtil.millisecondSleep(1000*3);
                return false;
            }

            Block block = blockDto2Block(blockDto);//blockdto 转换为block
            boolean checkBlock = checkBlock(block);//检验block
            if(!checkBlock){ //if block校验失败
                return false;  //返回false
            }
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(block, BlockchainAction.ADD_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(), kvWriteBatch);

            /**
             * 加入区块成功后从本地的未确认数据库删除区块的交易
             */
            List<TransactionDto>tr=blockDto.getTransactions();
            for(TransactionDto transaction : tr){
                String transactionHash = TransactionDtoTool.calculateTransactionHash(transaction);
                unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
            }

            //收到了上一次发布的区块得到共识后加入区块链后，激活矿工
            coreConfiguration.activeMiner();
            API.blockDto=null;
//            API.NewBlock=false;

            return true;
        }catch (Exception e){
            //LogUtil.debug("add block error.");
            return false;
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean MineraddBlockDto(BlockDto blockDto) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            //因为本身是主节点，挖出的区块。不需要其他节点共识
            Block block = blockDto2Block(blockDto);//blockdto 转换为block
            boolean checkBlock = checkBlock(block);//检验block
            if(!checkBlock){ //if block校验失败
                return false;  //返回false
            }
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(block, BlockchainAction.ADD_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(), kvWriteBatch);

            /**
             * 加入区块成功后从本地的未确认数据库删除区块的交易
             */
            List<TransactionDto>tr=blockDto.getTransactions();
            for(TransactionDto transaction : tr){
                String transactionHash = TransactionDtoTool.calculateTransactionHash(transaction);
                unconfirmedTransactionDatabase.deleteByTransactionHash(transactionHash);
            }


            return true;
        }catch (Exception e){
            //LogUtil.debug("add block error.");
            return false;
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean TryMineraddBlockDto(BlockDto blockDto) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            //因为本身是主节点，挖出的区块。不需要其他节点共识
            Block block = blockDto2Block(blockDto);//blockdto 转换为block
            boolean checkBlock = checkBlock(block);//检验block
            if(!checkBlock){ //if block校验失败
                return false;  //返回false
            }
            return true;
        }catch (Exception e){
            LogUtil.debug("add block error.");
            return false;
        }finally {
            writeLock.unlock();
        }
    }


    @Override
    public boolean unloadaddBlock(Block block) {
        KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(block, BlockchainAction.ADD_BLOCK);
        KvDbUtil.write(getBlockchainDatabasePath(), kvWriteBatch);
        return true;
    }



    /**
     * 删除当前区块
     */
    @Override
    public void deleteTailBlock() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            Block tailBlock = queryTailBlock();
            if(tailBlock == null){
                return;
            }
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainAction.DELETE_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(),kvWriteBatch);
        }finally {
            writeLock.unlock();
        }
    }

    /**
     * 删除区块
     * @param blockHeight
     */
    @Override
    public void deleteBlocks(long blockHeight) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            while (true){
                Block tailBlock = queryTailBlock(); //查询当前区块链的最后一个区块
                if(tailBlock == null){ //如果为空
                    return;
                }
                if(tailBlock.getHeight() < blockHeight){ //如果当前区块链的最后一个区块小于我们要删除的区块
                    return;
                }
                KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainAction.DELETE_BLOCK);
                KvDbUtil.write(getBlockchainDatabasePath(),kvWriteBatch);
            }
        }finally {
            writeLock.unlock();
        }
    }
    //endregion



    //region 校验区块、交易
    @Override
    public boolean checkBlock(Block block) {


        //校验业务
        Block previousBlock = queryTailBlock();


        //校验区块高度的连贯性
        if(!BlockTool.checkBlockHeight(previousBlock,block)){
            LogUtil.debug("区块写入的区块高度出错！");
            return false;
        }

        //校验区块的前区块哈希
        if(!BlockTool.checkPreviousBlockHash(previousBlock,block)){
            LogUtil.debug("区块写入的前区块哈希出错！");
            return false;
        }

        //检验区块的哈希
        if(!BlockTool.checkBlockHash(block)){
            LogUtil.debug("区块hash错误！");
            return false;
        }


        //检验区块的merkleTreeRoot
        if(!BlockTool.checkMerkle(block)){
            LogUtil.debug("区块merkleRoot错误！");
            return false;
        }

        //校验区块时间
        if(!BlockTool.checkBlockTimestamp(previousBlock,block)){
            LogUtil.debug("区块生成的时间太滞后！");
            return false;
        }

        //校验新产生的哈希
        if(!checkBlockNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常！");
            return false;
        }

//        //检验区块的签名
       if(!BlockTool.checkBlockSig(block)){
           LogUtil.debug("区块签名错误！");
           return false;
       }

        //从交易角度校验每一笔交易
        for(Transaction transaction : block.getTransactions()){
            boolean transactionCanAddToNextBlock = checkTransaction(transaction);
            if(!transactionCanAddToNextBlock){
                LogUtil.debug("区块数据异常，交易异常！");
                return false;
            }
        }

        return true;
    }

    /**
     * 检验交易
     * @param transaction 交易
     * @return
     */
    @Override
    public boolean checkTransaction(Transaction transaction) {

        //业务校验
        //校验新产生的哈希
        if(!checkTransactionNewHash(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常！");
            return false;
        }

        //检验交易的hash
        if(!TransactionTool.checkTransactionHash(transaction)){
            LogUtil.debug("交易hash错误！");
            return false;
        }
        //检验交易的签名
        if(!TransactionTool.checkTransactionSig(transaction)){
            LogUtil.debug("交易签名错误！");
            return false;
        }
        return true;
    }
    //endregion


    /**
     * 查询区块链高度
     * @return
     */
    //region 普通查询
    @Override
    public long queryBlockchainHeight() {
        byte[] bytesBlockchainHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainHeightKey());
        if(bytesBlockchainHeight == null){
            return GenesisBlockSetting.HEIGHT;
        }
        return ByteUtil.bytesToUint64(bytesBlockchainHeight);
    }

    /**
     * 查询当前区块链交易高度
     * @return
     */
    @Override
    public long queryBlockchainTransactionHeight() {
        byte[] byteTotalTransactionCount = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.bytesToUint64(byteTotalTransactionCount);
    }
    //endregion



    //region 区块查询
    /**
     * 查询当前区块
     * @return
     */
    @Override
    public Block queryTailBlock() {
        long blockchainHeight = queryBlockchainHeight();
        if(blockchainHeight <= GenesisBlockSetting.HEIGHT){
            return null;
        }
        return queryBlockByBlockHeight(blockchainHeight);
    }

    /**
     * 通过区块高度查询区块
     * @param blockHeight 区块高度
     * @return
     */
    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        byte[] bytesBlock = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeTool.decode(bytesBlock,Block.class);
    }

    /**
     * 通过区块hash查询区块[先查到区块高度，在查找区块]
     *
     * @param blockHash 区块hash
     * @return
     */
    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        byte[] bytesBlockHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(blockHash));
        if(bytesBlockHeight == null){
            return null;
        }
        return queryBlockByBlockHeight(ByteUtil.bytesToUint64(bytesBlockHeight));
    }
    //endregion



    //region 交易查询

    /**
     * 通过交易hash查询交易
     * @param transactionHash 交易hash
     * @return
     */
    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        byte[] transactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transactionHash));
        if(transactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.bytesToUint64(transactionHeight));
    }

    /**
     * 通过交易高度查询交易
     * @param transactionHeight 交易高度
     * @return
     */
    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        byte[] byteTransaction = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transactionHeight));
        if(byteTransaction == null){
            return null;
        }
        return EncodeDecodeTool.decode(byteTransaction,Transaction.class);
    }
    //endregion


    //region 拼装WriteBatch
    /**
     * 根据区块信息组装WriteBatch对象
     */
    private KvDbUtil.KvWriteBatch createBlockWriteBatch(Block block, BlockchainAction blockchainAction) {
        KvDbUtil.KvWriteBatch kvWriteBatch = new KvDbUtil.KvWriteBatch();

        storeHash(kvWriteBatch,block, blockchainAction); //存储已使用hash

        storeBlockchainHeight(kvWriteBatch,block, blockchainAction); //存储区块链高度
        storeBlockchainTransactionHeight(kvWriteBatch,block, blockchainAction); //存储区块链总的交易高度

        storeBlockHeightToBlock(kvWriteBatch,block, blockchainAction); //存储区块链高度到区块的映射
        storeBlockHashToBlockHeight(kvWriteBatch,block, blockchainAction);//存储区块哈希到区块高度的映射

        storeTransactionHeightToTransaction(kvWriteBatch,block, blockchainAction); //存储交易高度到交易的映射
        storeTransactionHashToTransactionHeight(kvWriteBatch,block, blockchainAction);//存储交易哈希到交易高度的映射

        return kvWriteBatch;
    }

    /**
     * 存储交易高度到交易的映射
     */
    private void storeTransactionHeightToTransaction(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                //更新区块链中的交易序列号数据
                byte[] transactionHeightToTransactionKey = BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transaction.getTransactionHeight());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHeightToTransactionKey, EncodeDecodeTool.encode(transaction));
                } else {
                    kvWriteBatch.delete(transactionHeightToTransactionKey);
                }
            }
        }
    }
    /**
     * 存储交易哈希到交易高度的映射
     */
    private void storeTransactionHashToTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                byte[] transactionHashToTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transaction.getTransactionHash());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHashToTransactionHeightKey, ByteUtil.uint64ToBytes(transaction.getTransactionHeight()));
                } else {
                    kvWriteBatch.delete(transactionHashToTransactionHeightKey);
                }
            }
        }
    }
    /**
     * 存储区块链的高度
     */
    private void storeBlockchainHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockchainHeightKey = BlockchainDatabaseKeyTool.buildBlockchainHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.uint64ToBytes(block.getHeight()));
        }else{
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.uint64ToBytes(block.getHeight()-1));
        }
    }
    /**
     * 存储区块哈希到区块高度的映射
     */
    private void storeBlockHashToBlockHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHashBlockHeightKey = BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(block.getHash());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHashBlockHeightKey, ByteUtil.uint64ToBytes(block.getHeight()));
        }else{
            kvWriteBatch.delete(blockHashBlockHeightKey);
        }
    }
    /**
     * 存储区块链中总的交易高度
     */
    private void storeBlockchainTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        long transactionCount = queryBlockchainTransactionHeight();
        byte[] bytesBlockchainTransactionCountKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey();
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.uint64ToBytes(transactionCount + BlockTool.getTransactionCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.uint64ToBytes(transactionCount - BlockTool.getTransactionCount(block)));
        }
    }

    /**
     * 存储区块链高度到区块的映射
     */
    private void storeBlockHeightToBlock(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHeightKey = BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(block.getHeight());
        if(BlockchainAction.ADD_BLOCK == blockchainAction){
            kvWriteBatch.put(blockHeightKey, EncodeDecodeTool.encode(block));
        }else{
            kvWriteBatch.delete(blockHeightKey);
        }
    }

    /**
     * 存储已使用的哈希
     */
    private void storeHash(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainAction blockchainAction) {
        byte[] blockHashKey = BlockchainDatabaseKeyTool.buildHashKey(block.getHash()); //拼装数据库 HASH_PREFIX_FLAG + hash + END_FLAG
        if(BlockchainAction.ADD_BLOCK == blockchainAction){ //如果区块动作是ADD_BLOCK的话
            kvWriteBatch.put(blockHashKey, blockHashKey); //kvwriteBatch写入<blockhashkey,blockhashkey>
        } else {
            kvWriteBatch.delete(blockHashKey);  // kvWriteBatch删除blockhashkey
        }
        List<Transaction> transactions = block.getTransactions(); //block获取交易
        if(transactions != null){ //如果交易不为空
            for(Transaction transaction:transactions){ //循环所有的交易
                byte[] transactionHashKey = BlockchainDatabaseKeyTool.buildHashKey(transaction.getTransactionHash());
                if(BlockchainAction.ADD_BLOCK == blockchainAction){
                    kvWriteBatch.put(transactionHashKey, transactionHashKey); // kvWriteBatch中写入<transactionhashkey,transactionhashkey>
                } else {
                    kvWriteBatch.delete(transactionHashKey);
                }
            }
        }
    }



    //endregion
    @Override
    public   String getBlockchainDatabasePath(){
        return FileUtil.newPath(coreConfiguration.getCorePath(), BLOCKCHAIN_DATABASE_NAME);
    }

    //region 新产生的哈希相关
    /**
     * 校验区块新产生的哈希
     */
    private boolean checkBlockNewHash(Block block) {
        //校验哈希作为主键的正确性
        //新产生的哈希不能有重复
        if(BlockTool.isExistDuplicateNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希有重复。");
            return false;
        }

        //新产生的哈希不能被区块链使用过了
        //校验区块Hash是否已经被使用了
        String blockHash = block.getHash();
        if(isHashUsed(blockHash)){
            LogUtil.debug("区块数据异常，区块Hash已经被使用了。");
            return false;
        }

        //校验每一笔交易新产生的Hash是否正确
        List<Transaction> blockTransactions = block.getTransactions();
        if(blockTransactions != null){
            for(Transaction transaction:blockTransactions){
                if(!checkTransactionNewHash(transaction)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 区块中校验新产生的哈希
     */
    private boolean checkTransactionNewHash(Transaction transaction) {
        //校验哈希作为主键的正确性
        //校验交易Hash是否已经被使用了
        String transactionHash = transaction.getTransactionHash();
        if(isHashUsed(transactionHash)){
            LogUtil.debug("交易数据异常，交易Hash已经被使用了。");
            return false;
        }
        return true;
    }

    /**
     * 哈希是否已经被区块链系统使用了？
     */
    private boolean isHashUsed(String hash){
        byte[] bytesHash = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildHashKey(hash));
        return bytesHash != null;
    }
    //endregion





    //region dto to model
    public Block blockDto2Block(BlockDto blockDto) {

        String previousBlockHash = blockDto.getPreviousHash(); //获取到前一个区块的hash
        Block previousBlock = queryBlockByBlockHash(previousBlockHash); //通过区块hash获取到区块

        Block block = new Block(); //创建区块
        block.setTimestamp(blockDto.getTimestamp()); //设置区块时间戳
        block.setPreviousHash(previousBlockHash); //设置区块前一个区块hash
        block.setSig(blockDto.getSig());//设置区块的签名
        block.setToCaresponse(blockDto.getEsCA()); //设置区块的证书
        block.setCommittee(blockDto.getCommittee());//设置委员会

        long blockHeight = BlockTool.getNextBlockHeight(previousBlock); //获取前一个区块的下一个区块的高度=====即当前区块的高度
        block.setHeight(blockHeight); //设置区块的高度

        List<Transaction> transactions = transactionDtos2Transactions(blockDto.getTransactions()); //将transactiondto转化为transaction
        block.setTransactions(transactions); //设置区块的transaction

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(block); //计算区块的merkleTree根
        block.setMerkleTreeRoot(merkleTreeRoot);//设置区块的merkleTreeRoot

        String blockHash = BlockTool.calculateBlockHash(block);//计算区块的hash
        block.setHash(blockHash);//设置区块的hash

        fillBlockProperty(block); //补充区块属性

        return block;
    }

    /**
     * transactionDto转换为Transactions
     * @param transactionDtos
     * @return
     */
    private List<Transaction> transactionDtos2Transactions(List<TransactionDto> transactionDtos) {
        List<Transaction> transactions = new ArrayList<>();
        if(transactionDtos != null){
            for(TransactionDto transactionDto:transactionDtos){
                Transaction transaction = transactionDto2Transaction(transactionDto);
                transactions.add(transaction);
            }
        }
        return transactions;
    }
    public Transaction transactionDto2Transaction(TransactionDto transactionDto) {

        Transaction transaction = new Transaction();
        transaction.setTaskNumber(transactionDto.getTaskNumber());
        transaction.setEsNumber(transactionDto.getEsNumber());
        transaction.setDatahash(transactionDto.getDatahash());
        transaction.setDatalocation(transactionDto.getDatalocation());
        transaction.setaBoolean(transactionDto.getaBoolean());
        transaction.setEsSig(transactionDto.getEsSig());
        transaction.setEsCA(transactionDto.getEsCA());
        transaction.setIotSig(transactionDto.getIotSig());
        transaction.setIotCA(transactionDto.getIotCA());
        transaction.setTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
        return transaction;
    }

    /**
     * 补充区块的属性
     */
    private void fillBlockProperty(Block block) {
        long transactionIndex = 0; //交易的序列号
        long transactionHeight = queryBlockchainTransactionHeight(); //交易的高度
        long blockHeight = block.getHeight();//区块高度
        String blockHash = block.getHash(); //区块的hash
        List<Transaction> transactions = block.getTransactions(); //区块的交易
        long transactionCount = BlockTool.getTransactionCount(block); //区块的交易数量
        block.setTransactionCount(transactionCount); //设置区块的交易数量
        block.setPreviousTransactionHeight(transactionHeight); //设置：上一个区块最后一笔交易在所有交易中的高度
        if(transactions != null){
            for(Transaction transaction:transactions){
                transactionIndex++;
                transactionHeight++;
                transaction.setBlockHeight(blockHeight);
                transaction.setTransactionIndex(transactionIndex);
                transaction.setTransactionHeight(transactionHeight);
            }
        }
    }
    //endregion
}