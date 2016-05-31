package com.berryjam.zookeepersample.primitive;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huangjinkun.
 * @date 16/5/31
 * @time 上午10:47
 */
public class LockDelegation {
    public static Integer data = 0;
    private static final int THREADS_COUNT = 100;
    private static final String HOST_PORT = "127.0.0.1:2181";

    private static final Logger LOGGER = LoggerFactory.getLogger(LockDelegation.class);

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];
        final CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(HOST_PORT, 50000, (event) -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }

        CountDownLatch mainThreadLatch = new CountDownLatch(THREADS_COUNT);
        for (int i = 0; i < THREADS_COUNT; i++) {
            DistributedLocksClient client = new DistributedLocksClient(zk,mainThreadLatch);
            threads[i] = new Thread(client);
            threads[i].start();
        }

        try {
            mainThreadLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(LockDelegation.data);
    }
}
