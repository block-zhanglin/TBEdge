package util;

/**
 *
 */
public class EncodeDecodeTool {//编码解码工具

    /**
     * 编码 object T 到字节数组
     * @param object
     * @param <T>
     * @return
     */
    public static<T> byte[] encode(T object) {
        return ByteUtil.stringToUtf8Bytes(JsonUtil.toString(object));
    }

    /**
     * 解码字节数组到对象object T
     * @param bytesObject
     * @param objectClass
     * @param <T>
     * @return
     */
    public static<T> T decode(byte[] bytesObject, Class<T> objectClass) {
        return JsonUtil.toObject(ByteUtil.utf8BytesToString(bytesObject),objectClass);
    }
}
