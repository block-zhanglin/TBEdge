package util;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *DB数据库中存在都是bytes[]数组
 */
public class KvDbUtil {

    private static Map<String, DB> dbMap = new HashMap<>(); //HsahMap<dbpath,db>leveldb地址和该数据库

    /**
     * 获取该地址的db数据库（如果不存在，就创建）
     * @param dbPath
     * @return
     */
    private static DB getDb(String dbPath) { //根据地址获取db
        synchronized (KvDbUtil.class){ //同步锁
            DB db = dbMap.get(dbPath);
            if(db == null){ //如果该数据库地址没有被用
                try {
                    DBFactory factory = new Iq80DBFactory(); //创建db工厂
                    Options options = new Options(); //创建options
                    db = factory.open(new File(dbPath), options); //使用db factory创建db
                } catch (IOException e) {
                    System.out.println("创建levelDB数据库出错");
                    throw new RuntimeException(e);
                }
                dbMap.put(dbPath,db);
            }
            return db;
        }
    }


    public static void put(String dbPath, byte[] bytesKey, byte[] bytesValue) { //向level数据库写入<key，value>
        DB db = getDb(dbPath);
        db.put(bytesKey,bytesValue);
    }

    public static void delete(String dbPath, byte[] bytesKey) { //向level数据库删除<key,>
        DB db = getDb(dbPath);
        db.delete(bytesKey);
    }

    public static byte[] get(String dbPath, byte[] bytesKey) { //从level数据库中获取value
        DB db = getDb(dbPath);
        return db.get(bytesKey);
    }

    public static List<byte[]> gets(String dbPath, long from, long size) { //从level数据库中获取from开始大小为size的数据
        synchronized (KvDbUtil.class){
            List<byte[]> values = new ArrayList<>();
            int cunrrentFrom = 0;
            int cunrrentSize = 0;
            DB db = getDb(dbPath);
            for (DBIterator iterator = db.iterator(); iterator.hasNext(); iterator.next()) {
                byte[] byteValue = iterator.peekNext().getValue();
                if(byteValue == null || byteValue.length==0){
                    //注意，用levelDB这里确是continue
                   continue;
                }
                cunrrentFrom++;
                if(cunrrentFrom>=from && cunrrentSize<size){
                    values.add(byteValue);
                    cunrrentSize++;
                }
                if(cunrrentSize>=size){
                    break;
                }
            }
            return values;
        }
    }

    public static void write(String dbPath, KvWriteBatch kvWriteBatch) { //leveldb 的writebatch同一时间操作多个
        DB db = getDb(dbPath);
        WriteBatch levelDbWriteBatch = toLevelDbWriteBatch(kvWriteBatch);
        db.write(levelDbWriteBatch);
    }

    private static WriteBatch toLevelDbWriteBatch(KvWriteBatch kvWriteBatch) {
        WriteBatch writeBatch = new WriteBatchImpl();
        if(kvWriteBatch != null){
            for (KvWrite kvWrite : kvWriteBatch.getKvWrites()){
                if(kvWrite.getKvWriteAction() == KvWriteAction.ADD){
                    writeBatch.put(kvWrite.key, kvWrite.value);
                }else if(kvWrite.getKvWriteAction() == KvWriteAction.DELETE){
                    writeBatch.delete(kvWrite.key);
                }else {
                    throw new RuntimeException();
                }
            }
        }
        return writeBatch;
    }



    public static class KvWriteBatch {  //KvWriteBatch类(List<KvWrite>)
        private List<KvWrite> kvWrites;
        public KvWriteBatch() {
            this.kvWrites = new ArrayList<>();
        }
        public List<KvWrite> getKvWrites() {
            return kvWrites;
        }
        public void setKvWrites(List<KvWrite> kvWrites) {
            this.kvWrites = kvWrites;
        }
        public void put(byte[] key, byte[] value) {
            kvWrites.add(new KvWrite(KvWriteAction.ADD,key,value));
        }
        public void delete(byte[] key) {
            kvWrites.add(new KvWrite(KvWriteAction.DELETE,key,null));
        }
    }

    public static class KvWrite { //kvWrite类（动作，key，value）
        private KvWriteAction kvWriteAction;
        private byte[] key;
        private byte[] value;
        public KvWrite(KvWriteAction kvWriteAction, byte[] key, byte[] value) {
            this.kvWriteAction = kvWriteAction;
            this.key = key;
            this.value = value;
        }
        public KvWriteAction getKvWriteAction() {
            return kvWriteAction;
        }
        public byte[] getKey() {
            return key;
        }
        public byte[] getValue() {
            return value;
        }
    }

    public enum KvWriteAction {
        ADD,DELETE
    }
}
