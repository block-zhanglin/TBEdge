package com.zl.blockchain.netcore;


import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.setting.BlockSetting;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;
import com.zl.blockchain.dto.*;
import com.zl.blockchain.util.TimeUtil;


import javax.swing.text.Style;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.StreamSupport;

public class BlockUnload {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;
    private EsBlockSearcher esBlockSearcher;
    private Account account;
    private ToCaresponse toCaresponse;
    private BlockchainHeightSearcher blockchainHeightSearcher;

    public BlockUnload(NetcoreConfiguration netcoreConfiguration,NodeService nodeService,BlockchainCore blockchainCore,EsBlockSearcher esBlockSearcher,Account account,ToCaresponse toCaresponse,BlockchainHeightSearcher blockchainHeightSearcher){
        this.netCoreConfiguration=netcoreConfiguration;
        this.nodeService=nodeService;
        this.blockchainCore=blockchainCore;
        this.esBlockSearcher=esBlockSearcher;
        this.account=account;
        this.toCaresponse=toCaresponse;
        this.blockchainHeightSearcher=blockchainHeightSearcher;
    }

    public void start(){
        try{
//            System.out.println(API.Cip);
//            System.out.println(API.MainCip);
            if(API.Cip.equals(API.MainCip)){
                while (true){
                    System.out.println("尝试卸载1");

                    /**
                     * 每次卸载时候需要进行一次Es层节点区块数量更新
                     */
                    blockchainHeightSearcher.searchBlockchainHeight();

                    /**
                     * 获取Es层的最长区块链高度
                     */
                    Long MaxEsblockHeight=0L;
                    Node n=new Node();
                    List<Node> nodes=nodeService.queryAllESNodes();

                    if(nodes.size()==0 || nodes==null){
                        System.out.println("还没有Es节点");
                        ThreadUtil.millisecondSleep(60*1000);
                        continue;
                    }

                    for(Node node:nodes){
                        if(node.getBlockchainHeight()>MaxEsblockHeight){
                            MaxEsblockHeight=node.getBlockchainHeight(); //es层的最长区块链数量
                            n=node; //对应的node节点
                            node.setBlockchainHeight(0);
                        }
                    }

                    //为了确保Es层的最长链已经被多数节点共识
                    int flag=0;
                    int mask=0;//出现了错误需要取消本次卸载
                    while (flag<=Integer.valueOf(BlockSetting.CommitteeCount )*1/3){
                        for(Node node:nodes){
                            if(node.getBlockchainHeight()==MaxEsblockHeight){
                                flag+=1;
                            }
                        }
                        if(mask>6){
                            break;
                        }
                        mask+=1;
                        ThreadUtil.millisecondSleep(1000*10);
                    }

                    if(mask>6){
                        continue;
                    }

                    System.out.println("本地区块链长度"+blockchainCore.getBlockchainDatabase().queryBlockchainHeight()+"-------"+"ES层区块链中的区块数量"+MaxEsblockHeight);


                    //当ES层区块链最长链大时，主云中心进行区块卸载区块（间隔区块数）或收到的请求卸载节点达到系统的容错界限时
                    if( MaxEsblockHeight>BlockSetting.INTER_BLOCK_COUNT || BlockSetting.unloadcount>Integer.valueOf(BlockSetting.CommitteeCount )*1/3){
                        long s=blockchainCore.getBlockchainDatabase().queryBlockchainHeight();
                        esBlockSearcher.searchNodesBlocks(n,s+1,s+1+BlockSetting.INTER_BLOCK_COUNT);

                        for(Node node:nodes){
                            HttpClient httpClient=new HttpClientImpl(node.getIp());
                            blockunloadrequest blockunloadrequest=new blockunloadrequest();
                            blockunloadrequest.setOriginheight(s);
                            blockunloadrequest.setEndheight(s+BlockSetting.INTER_BLOCK_COUNT);

                            blockunloadrequest.setTimestap(TimeUtil.millisecondTimestamp()); //设置当前的时间戳
                            //设置签名
                            blockunloadrequest.setSig(AccountUtil.signature(account.getPrivateKey(),String.valueOf(blockunloadrequest.getTimestap()+blockunloadrequest.getOriginheight()+blockunloadrequest.getEndheight()).getBytes(StandardCharsets.UTF_8)));
                            blockunloadrequest.setToCaresponse(toCaresponse); //设置证书
                            blockunloadresponse blockunloadresponse=httpClient.blockunload(blockunloadrequest);
                        }

                        int fc=Integer.valueOf(BlockSetting.CommitteeCount )*1/6;//期望值

                        if(BlockSetting.unloadcount==1){
                            BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT;
                        }else {
                            if(BlockSetting.unloadcount>fc){
                                BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT-((BlockSetting.unloadcount-fc)/fc)*BlockSetting.Init_Interblockcount;
                            }
                            if(BlockSetting.unloadcount<fc){
                                BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT+((fc-BlockSetting.unloadcount)/fc)* BlockSetting.Init_Interblockcount;
                            }
                            if(BlockSetting.unloadcount==fc||BlockSetting.unloadcount==1){
                                BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT;
                            }
                        }
//                        if(BlockSetting.unloadcount==1){
//                            BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT;
//                        }else {
//                            if(BlockSetting.unloadcount>Integer.valueOf(BlockSetting.CommitteeCount )*1/3){
//                                BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT*Integer.valueOf(BlockSetting.CommitteeCount ) *1/3/BlockSetting.unloadcount;
//                            }
//                            if(BlockSetting.unloadcount<Integer.valueOf(BlockSetting.CommitteeCount ) *1/3){
//                                BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT*2;
//                            }
//                            if(BlockSetting.unloadcount==Integer.valueOf(BlockSetting.CommitteeCount ) *1/3||BlockSetting.unloadcount==1){
//                                BlockSetting.INTER_BLOCK_COUNT=BlockSetting.INTER_BLOCK_COUNT;
//                            }
//                        }
                        System.out.println("计算卸载间隔"+BlockSetting.INTER_BLOCK_COUNT);
                        BlockSetting.unloadcount=1;//重置Es的卸载请求

                    }else {
                        System.out.println("没达到间隔区块");
                    }
                    ThreadUtil.millisecondSleep(10*1000);
                }
            }else {
                System.out.println("不是主节点");
            }
        }catch (Exception e){
            LogUtil.error("区块卸载任务出错",e);
        }
    }

}
