package util;

public class BlockSetting {

    //  区块链高度
    public static long HEIGHT=0;

    //每间隔1分钟发布一次新的委员会
    public static String CommitteeTime="CommitteeTime";

    //委员会中包含几个成员
    public  static  String CommitteeCount ="CommitteeCount";


    //第几次发布委员会
    public static  long Index=0;

    public static long getHEIGHT() {
        return HEIGHT;
    }

    public static void setHEIGHT(long HEIGHT) {
        BlockSetting.HEIGHT = HEIGHT;
    }

    public static long getIndex() {
        return Index;
    }
    public static void setIndex(long index) {
        Index = index;
    }

}
