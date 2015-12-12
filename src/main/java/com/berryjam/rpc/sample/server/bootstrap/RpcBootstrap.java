package com.berryjam.rpc.sample.server.bootstrap;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author huangjinkun.
 * @date 15/11/24
 * @time 下午11:44
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }
}
