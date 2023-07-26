package com.zl.blockchain.blockIot.crypto.model;

import java.io.Serializable;

/**
 * IOT账户:每个Iot设备都有自己唯一的账户
 *  ①私钥：对Iot发出的所有信息进行签名
 *  ②公钥：其他设备利用公钥验证签名
 *私钥-->公钥
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
    //endregion
}
