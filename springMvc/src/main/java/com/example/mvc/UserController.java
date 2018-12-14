package com.example.mvc;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private CloseableHttpClient httpClient;

    @RequestMapping("get")
    public String getUser() throws IOException {
        HttpGet get = new HttpGet("http://127.0.0.1:8088/example/login");
        CloseableHttpResponse response = httpClient.execute(get);
        return EntityUtils.toString(response.getEntity(),"UTF-8");
    }


    @RequestMapping("post")
    public String postTest() throws IOException {
        HttpPost post = new HttpPost("http://127.0.0.1:8088/example/login");
        // 创建请求参数
        List<NameValuePair> list = new LinkedList<>();
        BasicNameValuePair param1 = new BasicNameValuePair("username", "test");
        BasicNameValuePair param2 = new BasicNameValuePair("password", "test");
        list.add(param1);
        list.add(param2);
        // 使用URL实体转换工具
        UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
        post.setEntity(entityParam);
        CloseableHttpResponse response = httpClient.execute(post);
        return EntityUtils.toString(response.getEntity(),"UTF-8");
    }
}
