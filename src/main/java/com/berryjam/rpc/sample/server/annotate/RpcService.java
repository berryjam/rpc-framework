package com.berryjam.rpc.sample.server.annotate;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangjinkun.
 * @date 15/11/24
 * @time 下午4:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component // 表明可被Spring 扫描
public @interface RpcService {

    Class<?> value();
}
