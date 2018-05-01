import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;

/**
 * @author CoderZZ
 * @Title: ${FILE_NAME}
 * @Project: zookeepertest
 * @Package PACKAGE_NAME
 * @description: TODO:一句话描述信息
 * @Version 1.0
 * @create 2018-05-01 23:07
 **/
public class DistributeLockTest {


    private ZooKeeper zkCli = null;
    boolean haveLock = false;
    private final String groupNode = "/locks";
    private String myNodePath;
    private static String hostname;

    /**
     * 获取锁，然后去访问共享资源进行业务处理
     *
     * @throws Exception
     */
    public void gainLockAndDoSomething() throws Exception {

        // 构造一个zk客户端，定义一个监听器
        zkCli = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {

            public void process(WatchedEvent event) {

                if(event.getType()!= Event.EventType.NodeChildrenChanged) return;
                try{
                    // 用zk客户端去获取锁权限
                    haveLock = gainLock();
                    if (haveLock) {
                        System.out.println(hostname + " gained the lock....");
                        // 拿到锁之后，调用业务处理方法进行业务处理
                        doSomeThing();
                        // 释放锁
                        releaseLock();
                        // 重新创建锁节点并注册监听
                        registerLock();
                    }

                }catch(Exception e){

                }
            }
        });

        // 用zk客户端向"/locks"下注册一把自己的锁节点
        registerLock();

        // 让线程稍微休眠一下
        Thread.sleep((long) (Math.random() * 500 + 500));

        // 用zk客户端去获取锁权限
        haveLock = gainLock();

        if (haveLock) {
            System.out.println(hostname + " gained the lock....");
            // 拿到锁之后，调用业务处理方法进行业务处理
            doSomeThing();
            // 释放锁
            releaseLock();
            // 重新创建锁节点并注册监听
            registerLock();
        }

    }

    /**
     * 注册锁节点信息以用于竞争锁权限
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void registerLock() throws KeeperException, InterruptedException {

        // 向zookeeper中注册一个自己的锁节点，并把该锁节点路径记录在myNodePath变量中
        myNodePath = zkCli.create(groupNode + "/lock", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

    }

    /**
     * 获取锁权限 逻辑是：自己创建的锁节点如果是所有锁节点中最小的一个，则获得锁权限
     *
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public boolean gainLock() throws KeeperException, InterruptedException {

        List<String> children = zkCli.getChildren(groupNode, true);

        // 如果子节点列表就只有1个元素，那么说明，当前就只有我这一个节点在线，我就可以直接获得所权限
        if (children.size() == 1)
            return true;

        Collections.sort(children);

        // myNodePath : /locks/lock0000001

        if (children.get(0).equals(myNodePath.substring(groupNode.length() + 1))) {

            return true;
        } else {

            return false;
        }

    }

    /**
     * 模拟访问共享资源并进行业务处理的方法
     * @throws InterruptedException
     */
    public void doSomeThing() throws InterruptedException {

        System.out.println("begin working .......");
        Thread.sleep((long)(Math.random()*1000+500));
        System.out.println("work has complished.....");

    }


    /**
     * 释放所权限
     * 逻辑：删除掉自己的锁节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void releaseLock() throws InterruptedException, KeeperException {

        zkCli.delete(myNodePath, -1);

    }

    /**
     * 程序的入口
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        for(int i=0;i<5;i++){
            final int finalI = i;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    Thread.currentThread().setName("Server_"+ finalI);
                    hostname = "Server_"+ finalI;

                    // 去获取锁并且进行业务处理
                    DistributeLockTest demoServer = new DistributeLockTest();
                    try {
                        demoServer.gainLockAndDoSomething();
                        // 主线程休眠
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }
    }
}
