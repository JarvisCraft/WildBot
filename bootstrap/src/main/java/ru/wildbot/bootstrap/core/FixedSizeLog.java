package ru.wildbot.bootstrap.core;

import lombok.Synchronized;
import lombok.val;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class FixedSizeLog extends CircularFifoQueue<String> {
    public FixedSizeLog() {
        this(20);
    }

    public FixedSizeLog(int size) {
        super(size);
    }

    @Synchronized public String[] getLines() {
        val log = new String[size()];
        for (int i = 0; i < this.size(); i++) log[i] = get(i);
        return log;
    }
}
