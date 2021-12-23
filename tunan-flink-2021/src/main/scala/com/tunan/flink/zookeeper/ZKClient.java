package com.tunan.flink.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import scala.Int;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: 李沅芮
 * @Date: 2021/12/23 08:24
 * @Description:
 */
public class ZKClient {

    //会话超时时间
    private static final int SESSION_TIMEOUT = 10 * 1000;

    //连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;

    //ZooKeeper服务地址
    private static final String CONNECT_ADDR = "hadoop1:2181";

    //创建连接实例
    private CuratorFramework client = null;

    public static void main(String[] args) throws Exception {
        //1 重试策略：重试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        //2 通过工厂创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR).connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();
        //3 开启连接
        client.start();

        System.out.println(ZooKeeper.States.CONNECTED);
        System.out.println(client.getState());




        new Thread(new Runnable() {
            @Override
            public void run() {
                //创建临时节点
                boolean flag = true;

                while (flag){
                    try {
                        client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                                .forPath("/curator/table", "table111".getBytes());
                        flag = false;

                    }catch (Exception ex){
                        System.out.println("创建失败，无限循环中...");
                        flag = true;
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("创建成功");
            }
        }).start();


        System.out.println("main 线程执行完了");
        Thread.sleep(Integer.MAX_VALUE);
    }
}
