import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

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
                    try {
//                        if(null != watchedEvent.getPath() && !"".equals(watchedEvent.getPath())){
                            //只会监听到/mobanker节点的数据变化，监听不到子节点变化
//                            zooKeeperClient.exists("/monbanker",true);
                            //只能监听到子节点变化，监听不到数据变化
                            zooKeeperClient.getChildren("/mobanker",true);
//                        }
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

    private static void modifyZNode() throws KeeperException, InterruptedException {
        byte[] mobanker = zooKeeperClient.getData("/mobanker",true,null);
        System.out.println("modify before:"+new String(mobanker));
        Stat stat = zooKeeperClient.setData("/mobanker","Hello Zookeeper".getBytes(),-1);
        System.out.println("修改后版本："+stat.getVersion());
        mobanker = zooKeeperClient.getData("/mobanker",true,null);
        System.out.println("modify after:"+new String(mobanker));
    }

    private static void deleteZNode() throws KeeperException, InterruptedException {
        Stat stat = zooKeeperClient.exists("/mobanker",true);
        if(null != stat){
            System.out.println("/mobanker节点存在!");
            zooKeeperClient.delete("/mobanker",-1);
            stat = zooKeeperClient.exists("/mobanker",false);
            if(null == stat){
                System.out.println("删除成功!");
            }else {
                System.out.println("删除失败!");
            }
        }else{
            System.out.println("/mobanker节点不存在!");
        }

    }
    private static void getZNodeData() throws KeeperException, InterruptedException {
        byte[] mobanker = zooKeeperClient.getData("/mobanker",true,null);
        System.out.println(new String(mobanker));
    }

    private static void getChildren() throws KeeperException, InterruptedException {
        List<String> paths = zooKeeperClient.getChildren("/",true);
        for(String path:paths){
            System.out.println("path:"+path);
        }
    }
    public static void main(String[] args) throws Exception {
//        createZNode();
//        getZNodeData();
//        getChildren();
//        System.out.println("modifyZNode======================");
//        modifyZNode();
//        System.out.println("delete znode=================");
//        deleteZNode();
        System.out.println("begin===============");
        Thread.sleep(Long.MAX_VALUE);
    }
}
