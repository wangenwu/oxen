package com.baren.bison.common.http;

import org.apache.commons.collections.MapUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 16/11/18.
 */
public class BaseHttpClient {
    protected static RequestConfig requestConfig = null;

    static {
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(200)
                .setConnectTimeout(200)
                .setConnectionRequestTimeout(500)
                .build();

    }

    protected static void handleGzip(HttpClientBuilder httpBulder) {

        httpBulder.addInterceptorFirst((HttpRequest request, HttpContext context) -> {

            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }

        });
        httpBulder.addInterceptorFirst((HttpResponse response, HttpContext context) -> {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(
                                    new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }

        });
    }

    protected static Header[] convert(HashMap<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            List<Header> headerList = headers.entrySet().stream()
                    .map(entry -> new BasicHeader(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            return headerList.toArray(new Header[headerList.size()]);
        }
        return null;
    }
}
