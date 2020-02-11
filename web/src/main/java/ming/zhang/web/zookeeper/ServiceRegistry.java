package ming.zhang.web.zookeeper;

import org.apache.zookeeper.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper注册服务
 * Created by windwant on 2016/6/30.
 */
@Service
public class ServiceRegistry {

    private CountDownLatch latch = new CountDownLatch(1);

    @Value("${registry.address}")
    private String registryAddress;

    public void registry(String data){
        if(data != null){
            ZooKeeper zk = connectSvr();
            if(zk != null){
                createNode(zk, data);
            }
        }
    }

    /**
     * 注册服务
     * @return
     */
    private ZooKeeper connectSvr(){
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, RPCConstants.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getState() == Event.KeeperState.SyncConnected){
                        latch.countDown(); //释放
                    }
                }
            });
            latch.await();//阻塞
            if(zk.exists(RPCConstants.ZK_REGISTRY_PATH, false) == null){ //检测是否存在注册服务，否则创建
                zk.create(RPCConstants.ZK_REGISTRY_PATH, "ROOT".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return zk;
    }

    private void createNode(ZooKeeper zk, String data){
        try {
            String path = zk.create(RPCConstants.ZK_DATA_PATH, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
