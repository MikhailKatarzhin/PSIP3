package lock;

import java.util.ArrayList;

public class LockMain {

    static int writingFinish = 0;
    public static void main(String[] args) throws InterruptedException {

        final Buf<Integer> buffer= new Buf<>();
        final int nWritings = 10;
        final int nWriters = 5;
        final int nReaders = 10;

        ArrayList<Thread> threadsReaders = new ArrayList<>();
        for (int i = 0; i < nReaders; i++) {
            threadsReaders.add(new Thread(() -> {
                while (writingFinish < nWritings) {
                    Integer current = buffer.readFromBuf();
                    if (current != null)
                        System.out.println("\t\t\t" + Thread.currentThread().getName() + "R" + current);
                }
            }));
            threadsReaders.get(i).start();
        }
        ArrayList<Thread> threadsWriters = new ArrayList<>();
        for (int i = 0; i < nWriters; i++) {
            threadsWriters.add(new Thread(() -> {
                for (int j = 0; j < nWritings;) {
                    if (buffer.writeInBuf(j)) {
                        System.out.println("\t" + Thread.currentThread().getName() + "W" + j);
                        j++;
                    }
                }
            }));
            threadsWriters.get(i).start();
        }
        for (Thread thread : threadsWriters)
            thread.join();
        for (Thread thread: threadsReaders)
            thread.join();
    }
}
