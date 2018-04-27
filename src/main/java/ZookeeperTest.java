import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

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
    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeperClient = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("watchedEvent.getPath():"+watchedEvent.getPath());
                System.out.println("watchedEvent.getState().name():"+watchedEvent.getState().name());
                System.out.println("watchedEvent.getType().name():"+watchedEvent.getType().name());
            }
        });
        List<String> paths = zooKeeperClient.getChildren("/",true);
        for(String path:paths){
            System.out.println("path:"+path);
        }
    }
}
