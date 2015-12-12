package com.berryjam.rpc.sample.server.service.impl;

import com.berryjam.rpc.sample.server.annotate.RpcService;
import com.berryjam.rpc.sample.server.service.HelloService;

/**
 * @author huangjinkun.
 * @date 15/11/24
 * @time 下午4:50
 */
@RpcService(HelloService.class) // 指定远程接口
public class HelloServiceImpl implements HelloService {


    public String hello(String name) {
        return "Hello! " + name;
    }
}
