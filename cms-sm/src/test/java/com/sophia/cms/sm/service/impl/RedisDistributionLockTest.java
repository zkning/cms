package com.sophia.cms.sm.service.impl;

import com.sophia.cms.SmApplication;
import com.sophia.cms.orm.lock.RedisDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RedisDistributionLock Tester.
 *
 * @author <Authors name>
 * @version 1.0
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SmApplication.class)
public class RedisDistributionLockTest {
    private static final String lock = "RedisDistributionLockTest";
    @Autowired
    RedisDistributionLock redisDistributionLock;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: acquire(String key)
     */
    @Test
    public void testAcquireKey() throws Exception {
        int threadCount = 20;
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            exec.execute(() -> {
                try {
                    long tId = Thread.currentThread().getId();
                    log.info(tId + "申请锁时间:" + System.currentTimeMillis());
                    Boolean lockResult = redisDistributionLock.acquire(lock, 5 * 60 * 1000, 3, 1000 * 10);
                    if (lockResult) {
                        log.info(tId + "锁已获取..." + System.currentTimeMillis());
                        Thread.sleep(1000 * 5);
                        redisDistributionLock.release(lock);
                        log.info(tId + "锁已释放..." + System.currentTimeMillis());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.info("执行完成");
    }

    /**
     * Method: acquire(String key, long expire)
     */
    @Test
    public void testAcquireForKeyExpire() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: acquire(String key, long expire, int retryTimes, long sleepMillis)
     */
    @Test
    public void testAcquireForKeyExpireRetryTimesSleepMillis() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: release(String key)
     */
    @Test
    public void testRelease() throws Exception {
//TODO: Test goes here... 
    }


} 
