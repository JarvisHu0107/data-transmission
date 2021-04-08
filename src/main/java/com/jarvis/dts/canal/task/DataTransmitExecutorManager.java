package com.jarvis.dts.canal.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jarvis.dts.canal.task.AbstractDataTransmitTask;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Hu Xin
 * @Date: 2021/3/30 19:54
 * @Desc: 同步任务执行管理器
 **/
@Slf4j
public class DataTransmitExecutorManager {

    private static ThreadFactory threadFactory =
            new ThreadFactoryBuilder().setNameFormat("mysql-dts-pool-%d").build();

    private ExecutorService executorService;

    private CountDownLatch countDownLatch;

    private List<AbstractDataTransmitTask> taskList;


    public DataTransmitExecutorManager(int num) {
        taskList = new ArrayList<>(num);
        this.executorService = new ThreadPoolExecutor(num, num, 0L,
                TimeUnit.MILLISECONDS, new SynchronousQueue<>(), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        this.countDownLatch = new CountDownLatch(num);
    }

    /**
     * 维护一个线程池，每个线程执行一个库的同步任务
     * 启动的时候 丢不同的同步任务给管理器，开始执行
     */
    public void addTask(AbstractDataTransmitTask task) {
        task.setExecutorManager(this);
        taskList.add(task);
        executorService.submit(() -> task.start());
    }


    /**
     * 优雅退出的时候 要关闭所有任务线程
     */
    public void stop() throws InterruptedException {
        taskList.stream().forEach(task -> task.stop());
        countDownLatch.await();
        log.info("全部任务已经完成关闭。。。");

    }

    public void countDown() {
        countDownLatch.countDown();
    }


}
