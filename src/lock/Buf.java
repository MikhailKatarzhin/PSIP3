package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buf<T> implements WritingBuf<T>, ReadingBuf<T> {
    private T content = null;
    private final Lock lockW = new ReentrantLock();
    private final Lock lockR = new ReentrantLock();
    @Override
    public boolean writeInBuf(T newObject) {
        boolean successOfWriting = false;
        try {
            if (lockW.tryLock(10, TimeUnit.SECONDS)) {
                if (content != null) throw new Exception("Content is full");
                content = newObject;
                lockR.unlock();
                successOfWriting = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            lockW.unlock();
            e.printStackTrace();
        }
        return successOfWriting;
    }
    @Override
    public T readFromBuf() {
        T objectFromContent = null;
        try {
            if (lockR.tryLock(10, TimeUnit.SECONDS)) {
                if (content == null) throw new Exception("Content is empty");
                objectFromContent = content;
                content = null;
                lockW.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            lockR.unlock();
            e.printStackTrace();
        }
        return objectFromContent;
    }
}
