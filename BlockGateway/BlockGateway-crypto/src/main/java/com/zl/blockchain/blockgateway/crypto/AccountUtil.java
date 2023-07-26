package com.zl.blockchain.blockgateway.crypto;

import com.zl.blockchain.blockgateway.crypto.model.Account;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * 账户工具类
 */

public class AccountUtil {

    private static final ECDomainParameters CURVE; //
    private static final SecureRandom SECURE_RANDOM; //安全随机
    private static final boolean COMPRESSED = true; //压缩种子
    private static final BigInteger HALF_CURVE_ORDER;
    private static final byte VERSION = 0x00;
    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }


    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        SECURE_RANDOM = new SecureRandom();
        HALF_CURVE_ORDER = CURVE.getN().shiftRight(1);
    }

    /**
     * 私钥生成账户
     */
    public static Account accountFromPrivateKey(String privateKey) {
        try {
            BigInteger bigIntegerPrivateKey = decodePrivateKey0(privateKey);
            byte[] bytesPublicKey = publicKeyFromPrivateKey0(bigIntegerPrivateKey);
            String publicKey = encodePublicKey0(bytesPublicKey);
            Account account = new Account(privateKey,publicKey);
            return account;
        } catch (Exception e) {
            LogUtil.error("从私钥恢复账户失败。",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 由编码私钥解码出原始私钥
     */
    private static BigInteger decodePrivateKey0(String privateKey) {
        BigInteger bigIntegerPrivateKey = new BigInteger(privateKey,16);
        return bigIntegerPrivateKey;
    }

    /**
     * 由原始私钥推导出原始公钥
     */
    private static byte[] publicKeyFromPrivateKey0(BigInteger bigIntegerPrivateKey) {
        byte[] bytePublicKey = CURVE.getG().multiply(bigIntegerPrivateKey).getEncoded(COMPRESSED);
        return bytePublicKey;
    }

    /**
     * 将原始公钥进行编码操作，生成编码公钥
     */
    private static String encodePublicKey0(byte[] bytesPublicKey) {
        String publicKey = ByteUtil.bytesToHexString(bytesPublicKey);
        return publicKey;
    }



    /**
     * 签名
     */
    public static String signature(String privateKey, byte[] bytesMessage) {
        try {
            BigInteger bigIntegerPrivateKey = decodePrivateKey0(privateKey);
            byte[] bytesSignature = signature0(bigIntegerPrivateKey,bytesMessage);
            return ByteUtil.bytesToHexString(bytesSignature);
        } catch (Exception e) {
            LogUtil.debug("签名出错。");
            throw new RuntimeException(e);
        }
    }
    private static byte[] signature0(BigInteger privateKey, byte[] message) {
        try {
            ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey, CURVE);
            signer.init(true, ecPrivateKeyParameters);
            BigInteger[] signature = signer.generateSignature(message);
            BigInteger s = signature[1];
            if (!isCanonical(s)) {
                s = CURVE.getN().subtract(s);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new ASN1Integer(signature[0]));
            seq.addObject(new ASN1Integer(s));
            seq.close();
            return bos.toByteArray();
        } catch (Exception e) {
            LogUtil.debug("签名出错。");
            throw new RuntimeException(e);
        }
    }
    /**
     * 这里是为了解决交易延展性攻击。
     * Returns true if the S component is "low", that means it is below {@link ECKey#HALF_CURVE_ORDER}. See <a
     * href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">BIP62</a>.
     * 参考：bitcoinj-core-0.15.10.jar org.bitcoinj.core.ECKey.isCanonical()
     */
    private static boolean isCanonical(BigInteger s) {
        return s.compareTo(HALF_CURVE_ORDER) <= 0;
    }

    /**
     * 验证签名
     */
    public static boolean verifySignature(String publicKey, byte[] bytesMessage, String bytesSignature) {
        try {
            byte[] bytesPublicKey = decodePublicKey0(publicKey);
            return verifySignature0(bytesPublicKey,bytesMessage,ByteUtil.hexStringToBytes(bytesSignature));
        }catch(Exception e) {
            LogUtil.debug("验证签名出错。");
            return false;
        }
    }
    private static boolean verifySignature0(byte[] publicKey, byte[] message, byte[] signature) {
        try {
            ECDSASigner signer = new ECDSASigner();
            ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKey), CURVE);
            signer.init(false, ecPublicKeyParameters);
            ASN1InputStream decoder = new ASN1InputStream(signature);
            DLSequence seq = (DLSequence) decoder.readObject();
            ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
            ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
            if (!isCanonical(s.getValue())) {
                return false;
            }
            decoder.close();
            return signer.verifySignature(message, r.getValue(), s.getValue());
        } catch (Exception e) {
            LogUtil.debug("验证签名，出错。");
            return false;
        }
    }

    /**
     * 由编码公钥解码出原始公钥
     */
    private static byte[] decodePublicKey0(String publicKey) {
        byte[] bytesPublicKey = ByteUtil.hexStringToBytes(publicKey);
        return bytesPublicKey;
    }

}