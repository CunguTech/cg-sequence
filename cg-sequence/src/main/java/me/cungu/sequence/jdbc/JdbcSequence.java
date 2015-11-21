package me.cungu.sequence.jdbc;

import java.util.concurrent.locks.ReentrantLock;

import me.cungu.sequence.AbstractSequence;

public class JdbcSequence extends AbstractSequence {
	
	private volatile long value = 0;
	private volatile long maxValue = 0;
	private ReentrantLock lock = new ReentrantLock();
	
	private int retryNum = 100;
	
	private JdbcSequenceDAO jdbcSequenceDAO;
	
	@Override
	public long currVal() {
		return value;
	}
	
	@Override
	public long nextVal() {
		try {
			lock.lock();
			if (value == maxValue) {
				int i = 1;
				while ((i++) <= retryNum) {
					int sharding = sharingRule.getShardingIndex();
					
					long dbVal = jdbcSequenceDAO.getValue(sequenceConfig, sharding);
					long dbMaxVal = dbVal + (sequenceConfig.getCache() * sequenceConfig.getSharding());
					
					int affectedRows = jdbcSequenceDAO.updateValue(sequenceConfig, dbMaxVal, dbVal, sharding);
					if (affectedRows == 0) {
						continue;
					}
					
					maxValue = dbMaxVal;
					value = dbMaxVal - sequenceConfig.getCache();
					break;
				}
				if (i > retryNum) {
					throw new IllegalStateException("get nextVal error");
				}
			}
			return ++value;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			lock.unlock();
		}
	}
	
	public int getRetryNum() {
		return retryNum;
	}

	public void setRetryNum(int retryNum) {
		this.retryNum = retryNum;
	}

	public JdbcSequenceDAO getJdbcSequenceDAO() {
		return jdbcSequenceDAO;
	}

	public void setJdbcSequenceDAO(JdbcSequenceDAO jdbcSequenceDAO) {
		this.jdbcSequenceDAO = jdbcSequenceDAO;
	}

	public ReentrantLock getLock() {
		return lock;
	}
}