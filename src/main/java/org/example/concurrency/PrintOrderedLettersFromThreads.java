package org.example.concurrency;

/***
 * Given: print sequence of characters from various threads ten times meanwhile preserving the order
 * For instance, thread 1 prints 'A', thread 2 - 'B' and so on.
 * Expected:
 * ABCD
 * ABCD
 * ... (10 times)
 */
public class PrintOrderedLettersFromThreads {

    private static final int LINES = 10;
    private static final int LENGTH = 4;
    private static volatile int count = 0;

    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        Thread[] threads = new Thread[LENGTH];
        int i = 0;
        for (char ch = 'A'; ch < 'A' + LENGTH; ch++) {
            var pos = i;
            threads[i++] = new Thread(() -> print(pos), String.valueOf(ch));
        }
        for (Thread t : threads) {
            t.start();
        }
    }

    private static void print(int pos) {
        try {
            synchronized (LOCK) {
                while (count < LENGTH * LINES) {
                    if (count % LENGTH == pos) {
                        System.out.print(Thread.currentThread().getName());
                        count++;
                        if (count % LENGTH == 0 && count / LENGTH > 0) {
                            System.out.print('\n');
                        }
                        LOCK.notifyAll();
                    } else {
                        LOCK.wait();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}