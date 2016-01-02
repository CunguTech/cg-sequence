package me.cungu.sequence.jdbc;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import me.cungu.sequence.SequenceBuilder;
import me.cungu.sequence.redis.DefaultRedisSequenceDAO;
import me.cungu.sequence.redis.RedisSequence;

public class RedisSequenceTest {

	private RedisSequence seq;
	
	@BeforeClass
	public void beforeClass() throws SQLException {
		Map<String, Object> properties = new HashMap<>();
		properties.put(DefaultRedisSequenceDAO.CONFIG_REDIS_SERVERS, "127.0.0.1:6379,127.0.0.1:6380");
		properties.put(DefaultRedisSequenceDAO.CONFIG_REDIS_DATABASE, 0);
		properties.put(DefaultRedisSequenceDAO.CONFIG_REDIS_TIMEOUT, 2000);
		
		seq = SequenceBuilder.newBuild().setSequenceConfig("OrderId", 10, 2).setProperties(properties).buildRedisSequence();
	}
	
	@Test//(threadPoolSize=20, invocationCount=10000)
	public void case1() throws InterruptedException {
		long st = System.currentTimeMillis();
		
		final Set<Long> set = Collections.synchronizedSet(new TreeSet<Long>());
		ExecutorService e = Executors.newFixedThreadPool(100);
		final CountDownLatch latch = new CountDownLatch(100 * 500);
		for (int i = 0; i < 100; i++) {
			e.execute(new Runnable() {
				public void run() {
					for (int j = 0; j < 500; j++) {
//						System.out.println(seq.currVal() + "   : " + seq.nextVal());
//						seq.nextVal();
						set.add(seq.nextVal());
						latch.countDown();
					}
				}
			});
		}
		
		latch.await();
		System.out.println(System.currentTimeMillis() - st);
		System.out.println(set);
		System.out.println(set.size());
		
	}
	
	@AfterClass
	public void afterCLass() {
	}
}