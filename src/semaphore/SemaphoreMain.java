package semaphore;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class SemaphoreMain {
    static AtomicInteger writingFinish = new AtomicInteger(0);
    static Integer buffer = null;
    private static final Semaphore semaphoreRead = new Semaphore(1, true);
    private static final Semaphore semaphoreWrite = new Semaphore(1 , true);

    public static void main(String[] args) throws InterruptedException {

        long millis = System.currentTimeMillis();
        final int nWritings = 10;
        final int nWriters = 10;
        final int nReaders = 5;

        ArrayList<Thread> threadsReaders = new ArrayList<>();
        for (int i = 0; i < nReaders; i++) {
            threadsReaders.add(new Thread(() -> {
                while (writingFinish.get() < nWriters-1) {
                    try {
                        semaphoreRead.acquire();
                        if (buffer != null){
                            int current = buffer;
                            buffer = null;
                            StringBuilder tmp = new StringBuilder();
                            tmp.append("\t\t\t").append(Thread.currentThread().getName()).append("R");
                            for (int k = 0; k < nWritings; k++) {
                                tmp.append(k > current ? "__" : "<>");
                            }
                            System.out.println(tmp);
                        }
                    } catch (InterruptedException e) {
                        //throw new RuntimeException(e);
                    } finally {
                        semaphoreRead.release();
                    }
                }
            }));
            threadsReaders.get(i).start();
        }
        ArrayList<Thread> threadsWriters = new ArrayList<>();
        for (int i = 0; i < nWriters; i++) {
            threadsWriters.add(new Thread(() -> {
                for (int j = 0; j < nWritings;) {
                    try {
                        semaphoreWrite.acquire();
                        if (buffer == null) {
                            buffer = j;
                            StringBuilder tmp = new StringBuilder();
                            tmp.append("\t").append(Thread.currentThread().getName()).append("W");
                            for (int k = 0; k < nWritings; k++) {
                                tmp.append(k > j ? "__" : "[]");
                            }
                            System.out.println(tmp);
                            j++;
                        }
                    } catch (InterruptedException e) {
                        //throw new RuntimeException(e);
                    } finally {
                        semaphoreWrite.release();
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
