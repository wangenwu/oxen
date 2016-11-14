package com.baren.bison.zookeeper;

import com.baren.bison.exception.IllegalServiceHostException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by user on 16/11/9.
 */
public class PubSubServiceImp implements Watcher, IPubSubService {

    private static final Logger LOG = LoggerFactory.getLogger(PubSubServiceImp.class);

    private static final String ROOT_PATH = "/oxen";

    private ZooKeeper zooKeeper;
    private int sessionTimeout;
    private String hosts;
    private ConcurrentMap<String, Set<String>> serviceHosts = new ConcurrentHashMap<>();

    public PubSubServiceImp(String hosts, int sessionTimeout) throws IOException {
        this.hosts = hosts;
        this.sessionTimeout = sessionTimeout;
        this.zooKeeper = new ZooKeeper(hosts, sessionTimeout, this);

    }

    @Override
    public void process(WatchedEvent event) {
        String path = event.getPath();
        // 事件类型分类,如果是None,则表示连接状态有变
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:
                    // watch会自动重新注册到zookeeper的server上面
                    break;
                case Expired:
                    // zookeeper集群中断了客户端连接,需要重新创建zookeeper.
//                    dead = true;
                    try {
                        this.zooKeeper = new ZooKeeper(hosts, sessionTimeout, this);
                    } catch (IOException e) {
                        LOG.error("connect to zookeeper error.", e);
                    }
                    break;
            }
        }
        else { // 创建/删除节点,子节点变动和节点数据改变
            LOG.info("path change " + path);
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    List<String> childs = zooKeeper.getChildren(path, true);
                    hostChange(path, childs);
                    // 通知调用方

                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String zookeeperCreate(String path, byte[] data, List<ACL> acl, CreateMode mode) throws KeeperException, InterruptedException {
        try {
            LOG.info("create zookeeper node, path is {}", path);
            String realPath = zooKeeper.create(path, data, acl, mode);
            return realPath;
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                LOG.info("create zookeeper node of servicePubath {} is error, reason is servicePubath exist in zookeeper.", path);
                return path;
            } else if (e.code() == KeeperException.Code.SESSIONEXPIRED || e.code() == KeeperException.Code.CONNECTIONLOSS) {
                Long sid = zooKeeper.getSessionId();
                byte[] pwd = zooKeeper.getSessionPasswd();
                try {
                    zooKeeper = new ZooKeeper(hosts, sessionTimeout, this, sid, pwd);
                } catch (IOException e1) {
                    LOG.error("reconnect zookeeper hosts: "+ hosts + " error, reason is:", e1);
                }
                String realPath = zooKeeper.create(path, data, acl, mode);
                return realPath;
            }
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }
    }

    private String createNode(String path, byte[] data, List<ACL> acl, CreateMode mode) {


//        // 处理最后一个是/的path
//        if (path.endsWith("/")) {
//            path = path.substring(0, path.lastIndexOf("/"));
//        }

        String retPath = null;
        List<String> parts = Arrays.asList(path.substring(1).split("/"));
        String parent;
        for (int i = 1; i <= parts.size(); i++) {
            try {
                CreateMode pMode = CreateMode.PERSISTENT;
                if (i == parts.size()) {
                    pMode = mode;
                }
                parent = "/" + String.join("/", parts.subList(0, i));
                Stat stat = zooKeeper.exists(parent, false);
                if (stat != null) {
                    continue;
                } else {
                    retPath = zookeeperCreate(parent, data, acl, pMode);
                }
            } catch (KeeperException e) {
                if (e.code() == KeeperException.Code.NODEEXISTS) {
                    continue;
                }
                LOG.error("create zookeeper node of servicePubath "+ path + "is error. reason is:", e);
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
        return retPath;

    }



    @Override
    public boolean publishService(ServicePubEntity service) {

        String path = service.servicePubath();
        if (path == null) {
            throw new IllegalServiceHostException("service is not a valid :" + service.toString());
        }
        path = ROOT_PATH + "/" + path;

        String realPath = createNode(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        return realPath == null ? false : true;
    }

    @Override
    public Set<String> subscribeService(ServicePubEntity service) {
        String path = service.serviceSubPath();
        path =  ROOT_PATH + "/" + path;
        try {
            List<String> childs = zooKeeper.getChildren(path, true);
            hostChange(path, childs);
            return serviceHosts.get(path);
        } catch (KeeperException e) {
            LOG.error("get zookeeper node of path "+ path + "is error. reason is:", e);
        } catch (InterruptedException e) {
            LOG.error("get zookeeper node of path "+ path + "is error. reason is:", e);
        }
        return null;
    }

    private void hostChange(String path, List<String> childs) {
        Set<String> hosts = new HashSet<>();
        hosts.addAll(childs);
        serviceHosts.put(path, hosts);
    }
}
