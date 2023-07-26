package com.zl.blockIot.net.dao;

public class ToCaresponse {

    //公钥
    private String publickkey;

    //信息
    private  String name;

    //签名
    private  String sig;


    public String getName() {
        return name;
    }
    public String getPublickkey() {
        return publickkey;
    }
    public String getSig() {
        return sig;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPublickkey(String publickkey) {
        this.publickkey = publickkey;
    }
    public void setSig(String sig) {
        this.sig = sig;
    }

}
