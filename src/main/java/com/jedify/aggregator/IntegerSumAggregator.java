package com.jedify.aggregator;

import java.util.Collection;

/**
 * Created by j1013575 on 1/20/2016.
 */
public class IntegerSumAggregator implements IAggegator<Long>{
    @Override
    public Long aggregate(Collection<Long> elements) {
        Long result = new Long(0);
        if(elements==null || elements.isEmpty())
            return result;
        for(Long element : elements) {
            result = result + element;
        }
        return result;
    }
}
