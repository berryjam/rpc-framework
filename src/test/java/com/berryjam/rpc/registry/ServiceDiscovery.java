package com.berryjam.rpc.registry;

import com.berryjam.rpc.constant.Constant;
import io.netty.util.internal.ThreadLocalRandom;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author huangjinkun.
 * @date 15/11/27
 * @time 上午12:48
 */
public class ServiceDiscovery {


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> dataList = new ArrayList<String>();

    private String registryAddress;

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT,
                    (event) -> {
                        if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            latch.countDown();
                        }
                    });
            latch.await();
        } catch (IOException e) {
            LOGGER.error("", e);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = null;
            try {
                nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, (event) -> {
                    if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                        watchNode(zk);
                    }
                });
            } catch (KeeperException e) {
                e.printStackTrace();
            }
            List<String> dataList = new ArrayList<String>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            LOGGER.debug("node data: {}", dataList);
            this.dataList = dataList;
        } catch (KeeperException e) {
            LOGGER.error("", e);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
