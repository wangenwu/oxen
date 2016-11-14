package com.baren.bison.zookeeper;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Created by user on 16/11/10.
 */
public interface IPubSubService {

    boolean publishService(ServicePubEntity service);

    Set<String> subscribeService(ServicePubEntity service);


}
