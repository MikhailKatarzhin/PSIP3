package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Reader {
    private Integer buf;
    private final Lock lockW;
    private final Lock lockR;
    private final Boolean writingFinish;

    public Reader(Lock lockW, Lock lockR, Integer buf, Boolean writingFinish) {
        this.lockW = lockW;
        this.lockR = lockR;
        this.buf = buf;
        this.writingFinish = writingFinish;
    }

    public Integer read(){
        Integer ret = null;
        try {
            if (lockR.tryLock(10, TimeUnit.SECONDS)) {
                if (buf != null && !writingFinish) {
                    ret = buf;
                    buf = null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lockW.unlock();
            if (writingFinish) lockR.unlock();
        }
        return ret;
        //System.out.println("\t" + this + "W" + integer.toString());
    }
}
