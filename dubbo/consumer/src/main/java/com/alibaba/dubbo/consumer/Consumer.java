package com.alibaba.dubbo.consumer;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.dubbo.DemoService;

/**
 * Created by Administrator on 2017/8/2.
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        context.start();

        DemoService demoService = (DemoService)context.getBean("demoService"); 
        System.out.println("it is begin invoke!");
        String hello = demoService.sayHello("world"); // 执行远程方法

        System.out.println( hello ); // 显示调用结果
    }
}
