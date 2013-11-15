package nl.verheulconsultants.switchispmaven;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author erik
 */
public class OutputQueue {
    private int size = 50;
    private int maxSize = 1000;
    private BlockingQueue<String> queue;

    OutputQueue() {
        queue = new LinkedBlockingQueue<String>();
    }

    OutputQueue(int size) {
        queue = new LinkedBlockingQueue<String>();
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
        StringBuilder buf = new StringBuilder(10000);
        Iterator<String> it = queue.iterator();
        while (it.hasNext()) {
            buf.append(it.next());
            buf.append("\n");
        }
        return buf.toString();
    }
}
