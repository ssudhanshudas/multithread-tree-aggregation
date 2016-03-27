package com.jedify.nodeaggregator;

import com.jedify.aggregator.ParExIntegerSumAggregator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

/**
 * Created by j1013575 on 3/27/2016.
 */
public class TreeNodeAggregator extends RecursiveTask<Long> {
    private static final long serialVersionUID = -196522408291343951L;


    private final TreeNode treeNode;
    private int outputLevel;
    private Collection<Long> spreadValues;
    private Map<String, Long> resultMap = new ConcurrentHashMap<String, Long>();

    public TreeNodeAggregator(final TreeNode treeNode, int outputLevel, Map<String, Long> resultMap) {
        this.treeNode = Objects.requireNonNull(treeNode);
        this.outputLevel = outputLevel;
        this.spreadValues = new ArrayList<>();
        this.resultMap = resultMap;
    }

    @Override
    protected Long compute() {
        //  System.out.println("Computing size of: " + file.getName());

        if (treeNode.isLeaf()) {
            return Long.parseLong(((DefaultMutableTreeNode) treeNode).getUserObject().toString());
        }

        final List<TreeNodeAggregator> tasks = new ArrayList<>();
        // final File[] children = treeNode.listFiles();
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            final TreeNodeAggregator task = new TreeNodeAggregator(treeNode.getChildAt(i), outputLevel, resultMap);
            task.fork();
            tasks.add(task);
        }

        Long result = new Long(0);
        for (final TreeNodeAggregator task : tasks) {
            Long taskValue = task.join();
            spreadValues.add(taskValue);
            result += taskValue;
        }


        if (((DefaultMutableTreeNode) treeNode).getLevel() == outputLevel) {
            Long aggregatedValue = new ParExIntegerSumAggregator().aggregate(spreadValues);
            resultMap.put(printPath(((DefaultMutableTreeNode) treeNode).getUserObjectPath()), aggregatedValue);
            System.out.println("Aggregated Value : " + aggregatedValue);
        }
        return result;
    }

    public String printPath(Object[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < arr.length; x++) {
            sb.append(x == 0 ? "" : "->").append(arr[x].toString());
        }
        return sb.toString();
    }
}
