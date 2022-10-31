package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buf<T> implements WritingBuf<T>, ReadingBuf<T> {
    private T content = null;
    private final Lock lockRW = new ReentrantLock();
    @Override
    public boolean writeInBuf(T newObject) {
        boolean successOfWriting = false;
        try {
            if (lockRW.tryLock(10, TimeUnit.SECONDS)) {
                if (content != null) throw new Exception("Content is full");
                content = newObject;
                successOfWriting = true;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }finally {
            lockRW.unlock();
        }
        return successOfWriting;
    }
    @Override
    public T readFromBuf() {
        T objectFromContent = null;
        try {
            if (lockRW.tryLock(10, TimeUnit.SECONDS)) {
                if (content == null) throw new Exception("Content is empty");
                objectFromContent = content;
                content = null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            lockRW.unlock();
        }
        return objectFromContent;
    }
}
