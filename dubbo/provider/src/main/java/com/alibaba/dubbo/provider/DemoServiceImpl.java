package com.alibaba.dubbo.provider;

import com.alibaba.dubbo.DemoService;

/**
 * Created by Administrator on 2017/8/2.
 */
public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
    	System.out.println("my was called!");
        return "hello " + name;
    }
}
