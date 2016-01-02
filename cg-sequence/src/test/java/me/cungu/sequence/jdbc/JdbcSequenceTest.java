package me.cungu.sequence.jdbc;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.druid.pool.DruidDataSource;

import me.cungu.sequence.SequenceBuilder;

public class JdbcSequenceTest {

	private DruidDataSource dataSource;
	
	private JdbcSequence seq;
	
	@BeforeClass
	public void beforeClass() throws SQLException {
		dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/cungu?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true");
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setMaxActive(60);
		dataSource.init();
		
		seq = SequenceBuilder.newBuild().setSequenceConfig("OrderId", 10, 2).buildJdbcSequence(dataSource);
		// 500QPS
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
					for (int j = 0; j < 500; j++) { // 1: 1844 -> 1829  100: 1858 -> 1845
//						System.out.println(seq.currVal() + "   : " + seq.nextVal());
//						seq.nextVal();
						set.add(seq.nextVal());
						latch.countDown();
						// 1jvm => 10345 
						// 3jvm => 18246 1S: 30578
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
		dataSource.close();
	}
}