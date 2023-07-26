package com.zl.blockchain.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider; //BouncyCastle轻量级密码术包
import java.security.Provider;
import java.security.Security;

/**
 * JavaCryptographyExtension Provider工具类(JCE是java加密扩展包  )
 */

public class JavaCryptographyExtensionProviderUtil {


    public static synchronized void addBouncyCastleProvider(){
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if(provider == null){
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }

    }


    public static String getBouncyCastleProviderName(){
        return BouncyCastleProvider.PROVIDER_NAME;
    }

}
