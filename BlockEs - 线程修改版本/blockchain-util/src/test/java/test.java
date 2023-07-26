import org.junit.Test;

import java.util.Random;


public class test {


    @Test
    public void t(){
        Random random=new Random();

        for(int i=0;i<10;i++){
            System.out.println(random.nextInt(100)+1);
        }

        for(int i=0;i<10;i++){
            System.out.println(random.nextInt(100)+1);
        }

    }
}
