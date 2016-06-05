package com.berryjam.zookeepersample.primitive;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 基于ZooKeeper实现的分布式锁
 *
 * @author huangjinkun.
 * @date 16/5/31
 * @time 上午9:01
 */
public class DistributedLocksClient implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLocksClient.class);

    private static final String LOCK_BASE_PATH = "/_locknode_";
    private static final String LOCK_NAME = LOCK_BASE_PATH + "/guid-lock-";

    ZooKeeper zk;
    CountDownLatch latch = null;
    private String lockPath = null;

    public DistributedLocksClient(ZooKeeper zk, CountDownLatch latch) {
        this.zk = zk;
        this.latch = latch;
    }

    public void acquireLock() throws KeeperException, InterruptedException {
        lockPath = zk.create(LOCK_NAME, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode
                .EPHEMERAL_SEQUENTIAL);
        final Object lock = new Object();

        while (true) {
            List<String> nodes = zk.getChildren(LOCK_BASE_PATH,
                    (event) -> {
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    });

            Collections.sort(nodes); // ZooKeeper node names can be sorted lexographically
            if (lockPath.endsWith(nodes.get(0))) {
                return;
            } else {
                synchronized (lock) {
                    lock.wait();
                }
            }
        }
    }

    public void releaseLock() {
        try {
            zk.delete(lockPath, -1);
            lockPath = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            acquireLock();
            for (int i = 0; i < 100; i++) {
                LockDelegation.data++;
            }
            latch.countDown();
            releaseLock();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 100; i++) {
//            LockDelegation.data++;
//        }
//        synchronized (latch){
//            latch.countDown();
//        }

    }
}
