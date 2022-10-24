package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Writer {
    private Integer buf;
    private final Lock lockW;
    private final Lock lockR;

    public Writer(Lock lockW, Lock lockR, Integer buf) {
        this.lockW = lockW;
        this.lockR = lockR;
        this.buf = buf;
    }

    public boolean write(Integer integer) {
        try {
            if (lockW.tryLock(10, TimeUnit.SECONDS)) {
                if (buf != null) return false;
                buf = integer;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {

            lockR.unlock();
        }
        return false;
        //System.out.println("\t" + this + "W" + integer.toString());
    }
}
