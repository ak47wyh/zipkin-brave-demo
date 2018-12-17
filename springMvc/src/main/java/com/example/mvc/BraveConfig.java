package com.example.mvc;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.github.kristofa.brave.httpclient.BraveHttpRequestInterceptor;
import com.github.kristofa.brave.httpclient.BraveHttpResponseInterceptor;
import com.github.kristofa.brave.servlet.BraveServletFilter;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * springboot brave(zipkin)配置方式
 */
@Configuration
public class BraveConfig {

    @Value("${server.servlet.application-display-name}")
    private String serverName;

    /**
     * span 配置收集器
     * @return
     */
    @Bean
    public SpanCollector spanCollector(){
        HttpSpanCollector.Config config = HttpSpanCollector.Config.builder().compressionEnabled(false).connectTimeout(500)
                .flushInterval(1).readTimeout(6000).build();
        return HttpSpanCollector.create("http://127.0.0.1:9411",config,new EmptySpanCollectorMetricsHandler());
    }

    /**
     * brave 工具类
     * @param spanCollector
     * @return
     */
    @Bean
    public Brave brave(SpanCollector spanCollector){
        Brave.Builder builder = new Brave.Builder(serverName);//指定服务名称
        builder.spanCollector(spanCollector);
        builder.traceSampler(Sampler.create(1));
        return builder.build();
    }

    /**
     * 定义拦截器 需要serverRequestInterceptor,serverResponseInterceptor 分别完成sr和ss收集操作
     * @param brave
     * @return
     */
    @Bean
    public BraveServletFilter braveServletFilter(Brave brave){
        return new BraveServletFilter(brave.serverRequestInterceptor(),brave.serverResponseInterceptor(),new DefaultSpanNameProvider());
    }

    /**
     * httpClient客户端 需要clientRequestInterceptor,clientResponseInterceptor分别完成cs和cr收集操作
     * @param brave
     * @return
     */
    @Bean
    public CloseableHttpClient httpClient(Brave brave){
        CloseableHttpClient httpClient = HttpClients.custom()
                .addInterceptorFirst(new BraveHttpRequestInterceptor(brave.clientRequestInterceptor(),new DefaultSpanNameProvider()))
                .addInterceptorFirst(new BraveHttpResponseInterceptor(brave.clientResponseInterceptor()))
                .build();
        return httpClient;
    }

    /**
     * 以resttemplate的方式实现 需要clientRequestInterceptor,clientResponseInterceptor分别完成cs和cr收集操作
     * @param brave
     * @return
     */
    @Bean
    public RestTemplate restTemplate(Brave brave){
//        无法通过RestTemplateBuilder添加过滤器
//        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
//        restTemplateBuilder.additionalInterceptors(new BraveClientHttpRequestInterceptor(brave.clientRequestInterceptor(),brave.clientResponseInterceptor(),new DefaultSpanNameProvider()));
//        return restTemplateBuilder.build();
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new BraveClientHttpRequestInterceptor(brave.clientRequestInterceptor(),brave.clientResponseInterceptor(),new DefaultSpanNameProvider()));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
