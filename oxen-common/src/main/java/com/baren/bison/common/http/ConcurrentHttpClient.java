package com.baren.bison.common.http;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by user on 16/11/18.
 */
public class ConcurrentHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentHttpClient.class);
    private static ExecutorService executorService;



    static {
//        Executors.
        executorService = new ThreadPoolExecutor(5, 200,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static List<Pair<String, ResponseData>> concurrentHttpGet(List<String> urls, int timeout) {

        List<Pair<String, ResponseData>> ret = Lists.newArrayList();
        List<Callable<Pair<String, ResponseData>>> calls = urls.stream().
                <Callable<Pair<String, ResponseData>>>map(url -> (() -> Pair.of(url, HttpClientCommon.httpGet(url))))
                .collect(Collectors.toList());
        try {
            List<Future<Pair<String, ResponseData>>> futures = executorService.invokeAll(calls, timeout, TimeUnit.MILLISECONDS);

            for (int i = 0; i < futures.size(); i++) {
                try {
                    Pair<String, ResponseData> pair = futures.get(i).get();
                    ret.add(pair);
                } catch (CancellationException e) {
                    String errUrl = urls.get(i);
                    LOG.error("url is:" + errUrl + " cancell execute, reson may timeout.");
                    ResponseData data = new ResponseData();
                    data.setStatusCode(ResponseData.TIME_OUT_CODE);
                    data.setException(e);
                    ret.add(Pair.of(errUrl, data));
                }
            }

        } catch (InterruptedException e) {
            LOG.error("invokeAll Interrupted, cause is :", e);
        } catch (ExecutionException e) {
            LOG.error("Future.get() Execution error, cause is :", e);

        }
        return ret;
    }


    public static void main(String[] args) {

        List<String> urls = Lists.newArrayList();
        urls.add("http://i.play.api.3g.youku.com/common/v3/play?audiolang=1&brand=apple&btype=iPhone6%2C2&ctype=21&deviceid=0e884d8790f566e680e12f8bbbd0abfd&did=e7c50db0f4bd10982f41b88338040e6b0f7830c0&format=1%2C3%2C4%2C6%2C7%2C8&guid=367dbd641f196807ae11ee7298322be3&id=XMTY2MTg3MDM3Mg==&idfa=B6CD0483-9FD7-DF09-446B-852E0BBF8213&local_point=0&local_time=0&local_vid=XMTY2MTg3MDM3Mg==&network=WIFI&operator=%E4%B8%AD%E5%9B%BD%E7%A7%BB%E5%8A%A8_46000&os=ios&os_ver=9.3.2&ouid=e7c50db0f4bd10982f41b88338040e6b0f7830c0&pid=69b81504767483cf&point=1&scale=3&user_id=UMzI4NDg0MzgyMA==&vdid=5172355C-9E37-33E4-8D8C-0B33D83A59E9&ver=5.7.1&_s_=8d1acd613b70584a4c5cd5b70029538b&1475855939");
        urls.add("http://localhost:8000/common/v5/play?audiolang=1&brand=apple&btype=iPhone6%2C2&ctype=21&deviceid=0e884d8790f566e680e12f8bbbd0abfd&did=e7c50db0f4bd10982f41b88338040e6b0f7830c0&format=1%2C3%2C4%2C6%2C7%2C8&guid=367dbd641f196807ae11ee7298322be3&id=XMTY2MTg3MDM3Mg==&idfa=B6CD0483-9FD7-DF09-446B-852E0BBF8213&local_point=0&local_time=0&local_vid=XMTY2MTg3MDM3Mg==&network=WIFI&operator=%E4%B8%AD%E5%9B%BD%E7%A7%BB%E5%8A%A8_46000&os=ios&os_ver=9.3.2&ouid=e7c50db0f4bd10982f41b88338040e6b0f7830c0&pid=69b81504767483cf&point=1&scale=3&user_id=UMzI4NDg0MzgyMA==&vdid=5172355C-9E37-33E4-8D8C-0B33D83A59E9&ver=5.7.1&_s_=8d1acd613b70584a4c5cd5b70029538b&1475855939");
        List<Pair<String, ResponseData>> ret = concurrentHttpGet(urls, 2000);
        System.out.println(ret);
    }

}
