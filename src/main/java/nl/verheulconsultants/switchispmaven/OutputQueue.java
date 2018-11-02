package nl.verheulconsultants.switchispmaven;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author erik
 */
public class OutputQueue {
    static final int DEFAULT_SIZE = 50;
    static final int MAX_SIZE = 2000;
    static final int INIT_CAPACITY = 10000;
    private int size = DEFAULT_SIZE;
    private final int maxSize = MAX_SIZE;
    private BlockingQueue<String> queue;

    OutputQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    OutputQueue(int size) {
        queue = new LinkedBlockingQueue<>();
        if (size > 0 && size <= maxSize) {
            this.size = size;
        }
    }

    private void trimSize(int size) {
        while (size < queue.size()) {
            queue.remove();
        }
    }

    public void setSize(int newSize) {
        if (newSize <= 0 || newSize > maxSize) {
            throw new IllegalArgumentException("Output lines range must be >= 1 and <= " + maxSize);
        }
        size = newSize;
        trimSize(size);
    }

    public int getSize() {
        return size;
    }

    public void add(String s) throws InterruptedException {
        while (queue.size() >= size) {
            queue.remove();
        }
        queue.put(s);
    }

    public String getAll() {
        StringBuilder buf = new StringBuilder(INIT_CAPACITY);
        Iterator<String> it = queue.iterator();
        while (it.hasNext()) {
            buf.append(it.next());
            buf.append("\n");
        }
        return buf.toString();
    }
}
