package com.jedify.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by j1013575 on 1/20/2016.
 */
public class IntegerAverageAggregator implements IAggegator<Long>{

    class AggregateWorker extends RecursiveTask<Long> {

        ArrayList<Long> dataList;
        public AggregateWorker(Collection<Long> data) {
            this.dataList = new ArrayList<>(data);
        }

        /**
         * The main computation performed by this task.
         */
        @Override
        protected Long compute() {
            if(dataList.size() <= BATCHSIZE)
                return sum();
            else {
                final List<AggregateWorker> tasks = new ArrayList<>();
                int index = dataList.size();
                while(index>0) {
                    int startIndex = index-BATCHSIZE >= 0 ? index-BATCHSIZE:0;
                   //  System.out.println("Created task for " + startIndex + " to " + index);
                    Collection<Long> subList = dataList.subList(startIndex, index);
                    final AggregateWorker task = new AggregateWorker(subList);
                    task.fork();
                    tasks.add(task);
                    index = index-BATCHSIZE;
                }
                long result = 0;
                for (final AggregateWorker task : tasks) {
                    result += task.join();
                }
                return result;
            }
        }
        private Long sum() {
            Long result = new Long(0);
            for(Long data : dataList) {
                result+=data;
            }
            return result;
        }
    }
    @Override
    public Long aggregate(Collection<Long> elements) {
        final ForkJoinPool pool = new ForkJoinPool();
        Long result = new Long(0);
        try {
            result = pool.invoke(new AggregateWorker(elements));
        } finally {
            pool.shutdown();
        }
        return result / elements.size();
    }
}
