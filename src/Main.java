import java.util.ArrayList;

public class Main {
    static Integer buf = null;
    static Boolean writingFinish = false;
    public static void main(String[] args) throws InterruptedException {
        final int nWritings = 3;
        final int nWriters = 3;
        final int nReaders = 10;

        StringBuilder  stringBuilderW = new StringBuilder();
        StringBuilder  stringBuilderR = new StringBuilder();

        ArrayList<Thread> threadsWriters = new ArrayList<>();
        for (int i = 0; i < nWriters; i++) {
            threadsWriters.add(new Thread(() -> {
                for (int j = 0; j < nWritings; j++) {
                    while (buf != null) {
                    }
                    buf = j;
                    StringBuilder tmp  = new StringBuilder();
                    tmp.append(Thread.currentThread().getName()).append("W").append(j).append(" ");
                    stringBuilderW.append(tmp);
                }
            }));
            threadsWriters.get(i).start();
        }

        ArrayList<Thread> threadsReaders = new ArrayList<>();
        for (int i = 0; i < nReaders; i++) {
            threadsReaders.add(new Thread(() -> {
                while (!writingFinish) {
                    while (buf == null  && !writingFinish) {}
                    StringBuilder tmp = new StringBuilder();
                    tmp.append(Thread.currentThread().getName()).append("R").append(buf).append(" ");
                    stringBuilderR.append(tmp);
                    buf = null;
                }
            }));
            threadsReaders.get(i).start();
        }
        for (Thread thread : threadsWriters)
            thread.join();
        for (Thread thread: threadsReaders) {
            writingFinish = true;
            thread.join();
        }
            System.out.println("\n" + stringBuilderW);
            System.out.println("\n" + stringBuilderR);
    }
}