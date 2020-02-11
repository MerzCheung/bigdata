package ming.zhang.web.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务发现即监测
 * Created by windwant on 2016/6/30.
 */
@Component
public class ServiceDiscovery {

    //同步工具 zookeeper连接
    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> dataList = new ArrayList<String>();

    private String registryAddress;

    ServiceDiscovery() throws IOException {
        InputStream inputStream = ServiceDiscovery.class.getClassLoader().getResourceAsStream("zookeeper.properties");
        Properties prop = new Properties();
        prop.load(inputStream);
        this.registryAddress = prop.getProperty("registry.address");
        //连接zookeeper服务
        ZooKeeper zk = connectServer();
        if(zk != null){
            //监听节点
            watchNode(zk);
        }
    }

    /**
     * 随机获取服务器地址
     * @return
     */
    public String discory(){
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
            }
        }
        return data;
    }

    /**
     * 连接zookeeper服务
     * @return
     */
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, RPCConstants.ZK_SESSION_TIMEOUT, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    //连接完成，唤醒阻塞
                    latch.countDown();
                }
            });
            //阻塞等待连接完成
            latch.await();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk){
        try {
            //获取服务节点 并监听
            List<String> nodeList = zk.getChildren(RPCConstants.ZK_REGISTRY_PATH, event -> {
                if(event.getType() == Watcher.Event.EventType.NodeChildrenChanged){
                    watchNode(zk);
                }
            });

            List<String> dataList = new ArrayList<String>();
            for(String node: nodeList){
                //获取节点数据（服务器地址 host:port）
                byte[] data = zk.getData(RPCConstants.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(data));
            }
            this.dataList = dataList;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
