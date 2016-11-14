package com.baren.bison.zookeeper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by user on 16/11/10.
 */
public class ServicePubEntity {

    private static final Logger LOG = LoggerFactory.getLogger(ServicePubEntity.class);

    private String iface;
    private String group;
    private int port;

    public ServicePubEntity(String iface, String group, int port) {
        this.iface = iface;
        this.group = group;
        this.port = port;
    }

    public String getIface() {
        return iface;
    }

    public String getGroup() {
        return group;
    }

    public int getPort() {
        return port;
    }

    public String servicePubath() {
        StringBuilder str = new StringBuilder();
        if (!StringUtils.isEmpty(group)) {
            str.append(group).append("/");
        }
        str.append(iface).append("/").append(getHostIp() + ':' + port);
        return str.toString();
    }

    public String serviceSubPath() {
        StringBuilder str = new StringBuilder();
        if (!StringUtils.isEmpty(group)) {
            str.append(group).append("/");
        }
        str.append(iface);
        return str.toString();
    }

    public static String getHostIp(){
        try{
            InetAddress netAddress = InetAddress.getLocalHost();
            String ip = netAddress.getHostAddress(); //get the ip address
            return ip;
        }catch(UnknownHostException e){
            LOG.error("error get local host ip.", e);
        }
        return null;
    }
    public String toString() {
        return String.format("group: {}, iface: {}, port: {}");
    }
}
