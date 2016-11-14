package com.baren;

import com.baren.bison.zookeeper.PubSubServiceImp;
import com.baren.bison.zookeeper.ServicePubEntity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by user on 16/11/11.
 */
public class ZookeeperMain {

    public static void main(String[] args) throws IOException {

        PubSubServiceImp pubsub = new PubSubServiceImp("127.0.0.1:2181", 200000);
        ServicePubEntity entity = new ServicePubEntity("com.baren.bison.demo.proto.Mail", "test", 2222);
        pubsub.publishService(entity);

        pubsub.subscribeService(entity);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> System.out.println("dddddddddddd"));


    }
}
