package com.laputa.utils.structure;

import java.util.ArrayDeque;

/**
 *
 * FIFO limited array.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 07.09.16.
 */
public class LimitedArrayDeque<T> extends ArrayDeque<T> {

    private final int limit;

    public LimitedArrayDeque(int capacity) {
        super(capacity);
        this.limit = capacity - 1;
    }

    @Override
    public boolean add(T element) {
        while (size() > limit) {
            removeFirst();
        }

        return super.add(element);
    }

}
