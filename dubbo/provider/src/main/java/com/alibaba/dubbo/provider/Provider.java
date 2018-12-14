package com.alibaba.dubbo.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2017/8/2.
 */
public class Provider {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        context.start();

        System.out.println("now you can consumer!");
        System.in.read(); //  按任意键退出
    }
}
