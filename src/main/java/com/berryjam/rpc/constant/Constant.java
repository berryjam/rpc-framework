package com.berryjam.rpc.constant;

/**
 * @author huangjinkun.
 * @date 15/11/25
 * @time 上午12:01
 */
public interface Constant {

    int ZK_SESSION_TIMEOUT = 50000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
