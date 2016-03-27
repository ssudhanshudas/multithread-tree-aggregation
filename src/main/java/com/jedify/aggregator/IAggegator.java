package com.jedify.aggregator;

import java.util.Collection;

/**
 * Created by j1013575 on 1/20/2016.
 */
public interface IAggegator<T> {
    T aggregate(Collection<T> elements);

    int BATCHSIZE = 1000;
}
