package com.alibaba.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.config.ZipkinConfig;
import com.alibaba.dubbo.config.ZipkinConstants;
import com.alibaba.dubbo.rpc.*;
import com.github.kristofa.brave.*;
import com.github.kristofa.brave.internal.Nullable;
import com.github.kristofa.brave.internal.Util;
import com.twitter.zipkin.gen.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Encoding;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Activate(group = Constants.CONSUMER)
public class DrpcClientInterceptor implements Filter{
	
    private final ClientRequestInterceptor clientRequestInterceptor;
    private final ClientResponseInterceptor clientResponseInterceptor;
    private final ClientSpanThreadBinder clientSpanThreadBinder;
   
    public DrpcClientInterceptor() {
    	String sendUrl = ZipkinConfig.getProperty(ZipkinConstants.SEND_ADDRESS);
    	Sender sender = OkHttpSender.create(sendUrl).toBuilder().encoding(Encoding.JSON).build();
    	Reporter<zipkin.Span> reporter = AsyncReporter.builder(sender).build();
    	String application = ZipkinConfig.getProperty(ZipkinConstants.BRAVE_NAME);
    	Brave brave = new Brave.Builder(application).reporter(reporter).build();
        this.clientRequestInterceptor = Util.checkNotNull(brave.clientRequestInterceptor(),null);
        this.clientResponseInterceptor = Util.checkNotNull(brave.clientResponseInterceptor(),null);
        this.clientSpanThreadBinder = Util.checkNotNull(brave.clientSpanThreadBinder(),null);
    }

   
	public Result invoke(Invoker<?> arg0, Invocation arg1) throws RpcException {
		clientRequestInterceptor.handle(new GrpcClientRequestAdapter(arg1));
		Map<String,String> att = arg1.getAttachments();
		final Span currentClientSpan = clientSpanThreadBinder.getCurrentClientSpan();
		Result result ;
		try {
			result =  arg0.invoke(arg1);
            clientSpanThreadBinder.setCurrentSpan(currentClientSpan);
            clientResponseInterceptor.handle(new GrpcClientResponseAdapter(result));
        } finally {
            clientSpanThreadBinder.setCurrentSpan(null);
        }
		return result;
	}

    static final class GrpcClientRequestAdapter implements ClientRequestAdapter {
    	private Invocation invocation;
        public GrpcClientRequestAdapter(Invocation invocation) {
            this.invocation = invocation;
        }

       
        public String getSpanName() {
        	 String ls = (String) invocation.getArguments()[0];
             String serviceName = ls ;
             return serviceName;
        }

       
        public void addSpanIdToRequest(@Nullable SpanId spanId) {
        	Map<String,String> at = this.invocation.getAttachments();
            if (spanId == null) {
                at.put("Sampled", "0");
            } else {
            	
                at.put("Sampled", "1");
                at.put("TraceId", spanId.traceIdString());
                at.put("SpanId", IdConversion.convertToString(spanId.spanId));
                
                if (spanId.nullableParentId() != null) {
                    at.put("ParentSpanId", IdConversion.convertToString(spanId.parentId));
                }
            }
        }

       
        public Collection<KeyValueAnnotation> requestAnnotations() {
        	String ls = (String) invocation.getArguments()[0];
            KeyValueAnnotation an = KeyValueAnnotation.create("params", ls);
            return Collections.singletonList(an);
        }
       
        public com.twitter.zipkin.gen.Endpoint serverAddress() {
            return null;
        }
    }

    static final class GrpcClientResponseAdapter implements ClientResponseAdapter {

        private final Result result;

        public GrpcClientResponseAdapter(Result result) {
            this.result = result;
        }

        
        public Collection<KeyValueAnnotation> responseAnnotations() {
        	return Collections.<KeyValueAnnotation>emptyList();
            /*
        	return !result.hasException()
                ? Collections.<KeyValueAnnotation>emptyList()
                : Collections.singletonList(KeyValueAnnotation.create("error", result.getException().getMessage()));
                */
        }
    }
}