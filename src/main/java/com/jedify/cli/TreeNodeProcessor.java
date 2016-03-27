package com.jedify.cli;

/**
 * Created by j1013575 on 1/21/2016.
 */

import com.jedify.nodeaggregator.TreeNodeAggregator;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;


public class TreeNodeProcessor {
    private static Map<String, Long> resultMap = new ConcurrentHashMap<String, Long>();

    public static long aggregateNode(final DefaultMutableTreeNode file, int outputLevel) {
        final ForkJoinPool pool = new ForkJoinPool();
        try {
            return pool.invoke(new TreeNodeAggregator(file, outputLevel, resultMap));
        } finally {
            pool.shutdown();
        }
    }

    private TreeNodeProcessor() {
    }

    public static void main(String args[]) {

        long val = TreeNodeProcessor.aggregateNode(prepareTestTree(), 1);
        System.out.println("Size : " + val);
        long x = System.currentTimeMillis();
        System.out.println("Processing Finished : " + x);
        System.out.println("Time Taken To Finish : " + (x - ctime));
        System.out.println("Result map : " + resultMap.toString());
    }

    public static DefaultMutableTreeNode prepareTestTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(1);
        System.out.println("---------------------Preparing Hierarchy Tree -------------------");
        for (int i = 0; i < 1000; i++)
            root.add(new DefaultMutableTreeNode(1000 + i));

        for (int i = 0; i < root.getChildCount(); i++) {
            for (int j = 0; j < 2000; j++)
                ((DefaultMutableTreeNode) root.getChildAt(i)).add(new DefaultMutableTreeNode(1));
        }
        Enumeration enumeration = root.postorderEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            //System.out.println(node.getUserObject() + " : " + printPath(node.getUserObjectPath()));
        }

        System.out.println("--------------------------Tree Prepared.--------------------------");
        ctime = System.currentTimeMillis();
        System.out.println("Time  : " + ctime);
        return root;
    }

    private static long ctime;
}
