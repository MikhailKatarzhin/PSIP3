package lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockMain {

    static Boolean writingFinish = false;
    public static void main(String[] args) throws InterruptedException {
        Integer buf = null;
        Lock lockW = new ReentrantLock();
        Lock lockR = new ReentrantLock();

        final int nWritings = 10;
        final int nWriters = 5;
        final int nReaders = 10;

        lockR.lock();
        ArrayList<Thread> threadsReaders = new ArrayList<>();
        for (int i = 0; i < nReaders; i++) {
            threadsReaders.add(new Thread(() -> {
                Reader reader = new Reader(lockW, lockR, buf, writingFinish);
                while (!writingFinish) {
                    Integer current = reader.read();
                    if (current != null)
                        System.out.println("\t\t\t" + Thread.currentThread().getName() + "R" + current);
                }
            }));
            threadsReaders.get(i).start();
        }
        ArrayList<Thread> threadsWriters = new ArrayList<>();
        for (int i = 0; i < nWriters; i++) {
            threadsWriters.add(new Thread(() -> {
                Writer writer = new Writer(lockW, lockR, buf);
                for (int j = 0; j < nWritings;) {
                    if (writer.write(j)) {
                        System.out.println("\t" + Thread.currentThread().getName() + "W" + j);
                        j++;
                    }
                }
            }));
            threadsWriters.get(i).start();
        }
        for (Thread thread : threadsWriters)
            thread.join();
        writingFinish = true;
        for (Thread thread: threadsReaders)
            thread.join();
    }
}
