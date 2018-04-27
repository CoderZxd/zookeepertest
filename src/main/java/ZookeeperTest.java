import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

/**
 * @author CoderZZ
 * @Title: ${FILE_NAME}
 * @Project: zookeepertest
 * @Package PACKAGE_NAME
 * @description: TODO:一句话描述信息
 * @Version 1.0
 * @create 2018-04-27 23:20
 **/
public class ZookeeperTest {
    private static ZooKeeper zooKeeperClient = null;
    static{
        try {
             zooKeeperClient = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("watchedEvent.getPath():"+watchedEvent.getPath());
                    System.out.println("watchedEvent.getState().name():"+watchedEvent.getState().name());
                    System.out.println("watchedEvent.getType().name():"+watchedEvent.getType().name());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createZNode() throws KeeperException, InterruptedException {
        //CreateMode.PERSISTENT 创建永久节点
        String nodeName_1 = zooKeeperClient.create("/mobanker","Hello World".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //CreateMode.EPHEMERAL 创建临时节点,线程结束节点被删除
        String nodeName_2 = zooKeeperClient.create("/mobanker/fx","framework".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.out.println("nodeName_1:"+nodeName_1);
        System.out.println("nodeName_2:"+nodeName_2);

//        Thread.sleep(Long.MAX_VALUE);
    }

    private static void getChildren() throws KeeperException, InterruptedException {
        List<String> paths = zooKeeperClient.getChildren("/",true);
        for(String path:paths){
            System.out.println("path:"+path);
        }
    }
    public static void main(String[] args) throws Exception {
        createZNode();
    }
}
