package lock;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class LockMain {

    static AtomicInteger writingFinish = new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException {

        long millis = System.currentTimeMillis();
        final Buf<Integer> buffer= new Buf<>();
        final int nWritings = 10;
        final int nWriters = 10;
        final int nReaders = 5;

        ArrayList<Thread> threadsReaders = new ArrayList<>();
        for (int i = 0; i < nReaders; i++) {
            threadsReaders.add(new Thread(() -> {
                while (writingFinish.get() < nWriters-1) {
                    Integer current = buffer.readFromBuf();
                    if (current != null) {
                        StringBuilder tmp = new StringBuilder();
                        tmp.append("\t\t\t").append(Thread.currentThread().getName()).append("R");
                        for (int k = 0; k < nWritings; k++) {
                            tmp.append(k > current ? "__" : "<>");
                        }
                        System.out.println(tmp);
                    }
                }
            }));
            threadsReaders.get(i).start();
        }
        ArrayList<Thread> threadsWriters = new ArrayList<>();
        for (int i = 0; i < nWriters; i++) {
            threadsWriters.add(new Thread(() -> {
                for (int j = 0; j < nWritings;) {
                    if (buffer.writeInBuf(j)) {
                        StringBuilder tmp = new StringBuilder();
                        tmp.append("\t").append(Thread.currentThread().getName()).append("W");
                        for (int k = 0; k < nWritings; k++) {
                            tmp.append(k > j ? "__" : "[]");
                        }
                        System.out.println(tmp);
                        j++;
                    }
                }
                writingFinish.incrementAndGet();
            }));
            threadsWriters.get(i).start();
        }
        for (Thread thread : threadsWriters)
            thread.join();
        for (Thread thread: threadsReaders)
            thread.join();

        System.out.println("\n\nProgram continued " + ((System.currentTimeMillis() - millis)) + " milliseconds.");
    }
}
