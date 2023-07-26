package util;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *Byte数组：字节是通过网络传输信息（或在硬盘或内存中存储的信息）的单位。在Ascii中，一个英文字母占一个字节的空间，一个中文汉字占两个字节的空间。英文标点占一个字节，中文标点占2个字节。
 * 对于我们的需求来说，十六进制是0-F的符号，所以一个Byte数组中的元素对应一个ASCII码，也就是两个十六进制数字。（1byte=8位  ，一个十六进制数字=4位）
 *
 * Hex编码：就是把一个8位的字节数据用两个十六进制数展示出来，编码时，将8位二进制码重新分组成两个4位的字节，其中一个字节的低4位是原字节的高四位，另一个字节的低4位是原数据的低4位，高
 * 四位全部补0，然后输出这两个字节对应十六进制数字作为编码。Hex编码后的长度是源数据的2倍。
 *
 */
public class ByteUtil {  //字符操作类

    public static final int BYTE8_BYTE_COUNT = 8;

    /**
     * byte数组转十六进制字符串(十六进制字符串小写，仅包含字符0123456789abcdef)。
     * 不允许省略十六进制字符串前面的零，因此十六进制字符串的长度是字节数量的2倍。
     *
     * 16进制字符串转byte数组（十六进制字符串小写，仅包含字符0123456789adcdef）
     * @param hexString 16进制字符串，改属性值的长度一定是2的整数倍
     */
    public static String bytesToHexString(byte[] bytes) {
        return Hex.toHexString(bytes);
    }
    public static byte[] hexStringToBytes(String hexString) {
        return Hex.decode(hexString);
    }



    /**
     * 无符号整数转换为(大端模式)8个字节的字节数组。
     *
     * （大端模式）8个字节数组转换位无符号整数。
     */

    public static byte[] uint64ToBytes(long number) {
        return Longs.toByteArray(number);
    }
    public static long bytesToUint64(byte[] bytes) {
        return Longs.fromByteArray(bytes);
    }


    /**
     * 字符串转换为UTF8字节数组
     *
     * UTF8字节数组转换为字符串
     *
     * 布尔转为UTF8字节数组
     *
     * UTF字节数组转换为布尔
     */
    public static byte[] stringToUtf8Bytes(String stringValue) {
        return stringValue.getBytes(StandardCharsets.UTF_8);
    }

    public static String utf8BytesToString(byte[] bytesValue) {
        return new String(bytesValue,StandardCharsets.UTF_8);
    }

    public static byte[] booleanToUtf8Bytes(boolean booleanValue) {
        return String.valueOf(booleanValue).getBytes(StandardCharsets.UTF_8);  //先将boolean型给字符串化
    }

    public static boolean utf8BytesToBoolean(byte[] bytesValue) {
        return Boolean.valueOf(new String(bytesValue,StandardCharsets.UTF_8));
    }

    /**
     * 拼接数组(数组数量为2个)。
     *
     * 拼接数组（数组数量为3个）
     *
     * 拼接数组（数组数量为4个）
     *
     *拼接长度（计算【传入字节数组】的长度，然后将长度转为8个字节的字节数组（大端），然后将长度字节数组拼接在【传入字节数组】前，然后返回）
     */

    public static byte[] concatenate(byte[] bytes1,byte[] bytes2) {
        return Bytes.concat(bytes1,bytes2);
    }

    public static byte[] concatenate3(byte[] bytes1,byte[] bytes2,byte[] bytes3) {
        return Bytes.concat(bytes1,bytes2,bytes3);
    }

    public static byte[] concatenate4(byte[] bytes1,byte[] bytes2,byte[] bytes3,byte[] bytes4) {
        return Bytes.concat(bytes1,bytes2,bytes3,bytes4);
    }

    public static byte[] concatenateLength(byte[] value) {
        return concatenate(uint64ToBytes(value.length),value);
    }

    /**
     * 碾平字节数组列表为字节数组。
     */
    public static byte[] flat(List<byte[]> values) {
        byte[] concatBytes = new byte[0];
        for(byte[] value:values){
            concatBytes = concatenate(concatBytes,value);
        }
        return concatBytes;
    }

    /**
     * 碾平字节数组列表为新的字节数组，然后拼接长度并返回。
     */
    public static byte[] flatAndConcatenateLength(List<byte[]> values) {
        byte[] flatBytes = flat(values);
        return concatenateLength(flatBytes);
    }


    /**
     *
     *copy源数组从开始地方，固定长度到新的数组中
     *
     */
    public static byte[] copy(byte[] sourceBytes, int startPosition, int length) {
        byte[] destinationBytes = new byte[length];
        System.arraycopy(sourceBytes,startPosition,destinationBytes,0,length);
        return destinationBytes;
    }

    /**
     * copy源数组从开始地方，固定长度到指定数组当中
     *
     */

    public static void copyTo(byte[] sourceBytes, int sourceStartPosition, int length, byte[] destinationBytes, int destinationStartPosition){
        System.arraycopy(sourceBytes,sourceStartPosition,destinationBytes,destinationStartPosition,length);
    }

    /**
     * 判断2个字节数组是否相等
     */
    public static boolean equals(byte[] bytes1, byte[] bytes2) {
        return Arrays.equals(bytes1,bytes2);
    }

    /**
     * 随机化一个32为的字节数组
     * @return
     */
    public static byte[] random32Bytes(){
        byte[] randomBytes = new byte[32];
        Random RANDOM = new Random();
        RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }
}