package com.zl.blockchain.crypto.model;

import java.io.Serializable;

/**
 * 数字货币账户
 *
 * 数字货币的账户(账户在区块链领域被称为钱包)由账号(账号在区块链领域被称为地址)、密码(密码在区块链领域被称为私钥)组成。
 * 私钥可以推导出公钥，公钥不能逆推出私钥。
 * 公钥可以推导出公钥哈希，公钥哈希不能逆推出公钥。
 * 公钥哈希可以推导出地址，地址可以逆推出公钥哈希。
 *私钥->公钥->公钥hash<->地址
 *
 */

public class Account implements Serializable {

    //私钥
    private String privateKey;

    //公钥
    private String publicKey;



    public Account(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }


    //获取私钥
    public String getPrivateKey() {
        return privateKey;
    }

    //获取公钥
    public String getPublicKey() {
        return publicKey;
    }


}
