package me.cungu.sequence.redis;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.cungu.sequence.AbstractSequence;

public class RedisSequence extends AbstractSequence {
	
	private static final Logger LOG = LoggerFactory.getLogger(RedisSequence.class);
	
	private volatile long value = 0;
	private volatile long maxValue = 0;
	private ReentrantLock lock = new ReentrantLock();
	
	private RedisSequenceDAO redisSequenceDAO;
	
	@Override
	public long currVal() {
		return value;
	}

	@Override
	public long nextVal() {
		try {
			lock.lock();
			if (value == maxValue) {
				long cache = sequenceConfig.getCache();
				long incrementBy = cache * sequenceConfig.getSharding();
				
				long redisMaxVal = -1;
				Exception lastException = null;
				int retryNum = 1;
				while (retryNum <= 3) {
					try {
						int sharding = sharingRule.getShardingIndex();
						redisMaxVal = redisSequenceDAO.incrBy(sequenceConfig, incrementBy, sharding);
						break;
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
						lastException = e;
					}
					retryNum++;
				}
				if (lastException != null) {
					throw new IllegalStateException(lastException.getMessage(), lastException);
				}
				if (redisMaxVal == -1) {
					throw new IllegalStateException("get nextVal error");
				}
				
				maxValue = redisMaxVal;
				value = redisMaxVal - cache;
			}
			
			return ++value;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			lock.unlock();
		}
	}
	
	public RedisSequenceDAO getRedisSequenceDAO() {
		return redisSequenceDAO;
	}

	public void setRedisSequenceDAO(RedisSequenceDAO redisSequenceDAO) {
		this.redisSequenceDAO = redisSequenceDAO;
	}

	public ReentrantLock getLock() {
		return lock;
	}
}