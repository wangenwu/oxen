package com.baren.bison.common.http;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by user on 16/11/18.
 */
public class HttpClientCommon extends BaseHttpClient{


    public static ResponseData httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    public static ResponseData httpGet(String url, HashMap<String, String> headers) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setHeaders(convert(headers));
        return handleHttp(get);
    }

    public static ResponseData httpPost(String url, Map<String, String> querys) throws IOException {

        return httpPost(url, querys, null);
    }
    public static ResponseData httpPost(String url, Map<String, String> querys, HashMap<String, String> headers) throws IOException {

        List<NameValuePair> params = querys.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        HttpEntity reqEntity = new UrlEncodedFormEntity(params, "utf-8");
        HttpPost post = new HttpPost(url);
        post.setEntity(reqEntity);
        post.setHeaders(convert(headers));
        return handleHttp(post);
    }

    private static ResponseData handleHttp(HttpUriRequest request) throws IOException {


        CloseableHttpClient httpclient = getClient();
        CloseableHttpResponse response = httpclient.execute(request);
        try {
            int code = response.getStatusLine().getStatusCode();
            ResponseData responseData = new ResponseData();
            responseData.setStatusCode(code);
            responseData.setHeaders(Arrays.asList(response.getAllHeaders()));
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String message = EntityUtils.toString(entity, "utf-8");
                responseData.setContent(message);
            } else {
                responseData.setContent(null);
            }
            return responseData;
        } finally {
            response.close();
        }
//        return null;
    }

    private static CloseableHttpClient getClient() {
        HttpClientBuilder httpBulder = HttpClients.custom();
        handleGzip(httpBulder);
        httpBulder.setDefaultRequestConfig(requestConfig);
        return httpBulder.build();
    }

    public static void main(String[] args) throws IOException {
//        String url = "http://localhost:8000/common/v5/play?id=XMTY2MTg3MDM3Mg==&audiolang=1&ctype=21&format=1%2C3%2C4%2C6%2C7%2C8&guid=362be3&os=ios&ouid=e7c50db0f4bd10982f41b88338040e6b0f7830c0&pid=69b81504767483cf&point=1&scale=3";
        String url = "http://10.103.188.200/video.show?ft=json&ob=&q=videoid%3AXMTY2MTg3MDM3Mg%3D%3D&fd=title%20videoid%20desc%20category%20streamtypes%20width%20height%20wh%20userid%20username%20source%20publishtime%20thumburl%20thumburl_v2%20seconds%20decright%20total_vv%20total_comment%20total_fav%20total_up%20total_down%20is_drm%20sharestate%20state%20limit_level%20tags%20operation_limit%20transfer_mode%20size%20is_panorama%20is_topic%20download_status%20show_id%20ugctitle%20dvdtitle%20isshowvideo%20show_videotype%20show_videoseq%20audiolang%20attachment%20guest&pn=1&pl=10";
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "XMTY2MTg3MDM3Mg==");
        HashMap<String, String> header = new HashMap<>();
//        header.put("User-Agent", "Youku;5.7.1;iPhone OS;9.3.2;iPhone6,2");
//        header.put("Cookie", "k=xu%E5%8D%81%E4%B8%80;ykss=545cf95459460d59267e9340;YOUKUSESSID=yktk%3D1%7C1425628244%7C15%7CaWQ6ODcxNjEwMTAsbm46eHXljYHkuIAsdmlwOnRydWUseXRpZDozNDU3OTA1MzAsdGlkOjA%3D%7Cf3e1e2e284c6c73f454c13884ab69872%7C26ef16bd7350b6ffec9482ac236272e5ff215589%7C1;yktk=1%7C1425628244%7C15%7CaWQ6ODcxNjEwMTAsbm46eHXljYHkuIAsdmlwOnRydWUseXRpZDozNDU3OTA1MzAsdGlkOjA%3D%7Cf3e1e2e284c6c73f454c13884ab69872%7C26ef16bd7350b6ffec9482ac236272e5ff215589%7C1;u=xu%E5%8D%81%E4%B8%80;v=UMzQ4NjQ0MDQw__1%7C1425628244%7C15%7CaWQ6ODcxNjEwMTAsbm46eHXljYHkuIAsdmlwOnRydWUseXRpZDozNDU3OTA1MzAsdGlkOjA%3D%7Cf3e1e2e284c6c73f454c13884ab69872%7C26ef16bd7350b6ffec9482ac236272e5ff215589%7C1____545cf95459460d59267e9340;logintime=1425628244;_1=1;userTrack_predict=76,1615.1315789473683,1425611408455,1.710669520303996; Domain=.atm.youku.com; expires=Thu, 01-Mar-2035 03:10:08 GMT; Path=/;updatetime=1425611408455; Domain=.atm.youku.com; expires=Thu, 01-Mar-2035 03:10:08 GMT; Path=/;lunbo2=9:503599:3:5:14171040; Domain=.atm.youku.com; expires=Wed, 22-Nov-2034 03:25:53 GMT; Path=/;lunbo=9:610968:5,7,9,1,3:8|2:594523_601839:3_6,8:10|20:584008_589413_582831:10,2_6_9,3:5|300:629232:10,2,4,6,8:7|300:617949:7,9,1,3,5:10|9:503599:9,1,3,5,7:3|3:604657_567687:4,6_1,3:3|2:645138:1,2,3,4,5,6,7,8,9,10:1|3:567021:1,2,3,4,5,6,7,8,9,10:7|3:612472_616760:6,8,10_2:9|2:616643_605547:3_10,2,5:8|9:581364_583158:3,5,7_10,2:7|300:615182:10,2,4,6,8:5|9:594820_601774_602977:3,9_4,6_10,2,5,8,1,7:3|3:588124:1,3:3|3:612472:1,3,5:4|20:574241:1,3,5:1|300:615884:2,4,6,8,10:8|3:589957_599439:6,9,3_8,10,2,4:5|20:615994_612471:4,6,8_1,2,3,5,7,9,10:4|2:594111_604213:2_7,9,1,3:3|2:571552_567121:4,6,8_9,1,3:1|9:567114_582096:5,8,1,7,3_10,2,4,6:7; Domain=.atm.youku.com; expires=Sat, 07-Mar-2015 03:10:08 GMT; Path=/;");
        ResponseData data = httpGet(url, header);
        System.out.println(data);
    }
}
