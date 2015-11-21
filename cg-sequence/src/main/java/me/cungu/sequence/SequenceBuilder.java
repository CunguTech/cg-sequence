package me.cungu.sequence;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import me.cungu.sequence.jdbc.DefaultJdbcSequenceDAO;
import me.cungu.sequence.jdbc.JdbcSequence;
import me.cungu.sequence.jdbc.JdbcSequenceDAO;
import me.cungu.sequence.redis.DefaultRedisSequenceDAO;
import me.cungu.sequence.redis.RedisSequence;
import me.cungu.sequence.redis.RedisSequenceDAO;

public class SequenceBuilder {

	private Map<String, Object> properties = new HashMap<String, Object>();
	private String sequenceName;
	private int cache;
	private int sharding;
	
	private ShardingRule sharingRule;
	
	public static SequenceBuilder newBuild() {
		return new SequenceBuilder();
	}

	public SequenceBuilder setSequenceConfig(String sequenceName, int cache, int sharding) {
		this.sequenceName = sequenceName;
		this.cache = cache;
		this.sharding = sharding;
		return this;
	}
	
	public SequenceBuilder setProperties(Map<String, Object> properties) {
		this.properties = properties;
		return this;
	}
	
	public SequenceBuilder setShardingRule(ShardingRule sharingRule) {
		this.sharingRule = sharingRule;
		return this;
	}
	
	public SequenceConfig buildSequenceConfig() {
		return new SequenceConfig() {
			@Override
			public String getSequenceName() {
				return sequenceName;
			}
			@Override
			public int getCache() {
				return cache;
			}
			@Override
			public int getSharding() {
				return sharding;
			}
			@Override
			public <T> T get(String key, Class<T> requiredType) {
				return (T) properties.get(key);
			}
			@Override
			public <T> T get(String key, Class<T> requiredType, T defaultValue) {
				T value = (T) properties.get(key);
				if (value == null) {
					return defaultValue;
				}
				return value;
			}
			@Override
			public Map<String, Object> getProperties() {
				return properties;
			}
		};
	}
	
	public JdbcSequence buildJdbcSequence(DataSource dataSource) {
		SequenceConfig sequenceConfig = buildSequenceConfig();
		if (sharingRule == null) {
			sharingRule = new RandomShardingRule(sequenceConfig);
		}
		JdbcSequence sequence = new JdbcSequence();
		
		JdbcSequenceDAO jdbcSequenceDAO = new DefaultJdbcSequenceDAO();
		jdbcSequenceDAO.setDataSource(dataSource);
		sequence.setJdbcSequenceDAO(jdbcSequenceDAO);
		
		sequence.setSequenceConfig(sequenceConfig);
		sequence.setSharingRule(sharingRule);
		
		return sequence;
	}
	
	public RedisSequence buildRedisSequence() {
		SequenceConfig sequenceConfig = buildSequenceConfig();
		if (sharingRule == null) {
			sharingRule = new RandomShardingRule(sequenceConfig);
		}
		RedisSequence sequence = new RedisSequence();
		
		RedisSequenceDAO redisSequenceDAO = new DefaultRedisSequenceDAO();
		sequence.setRedisSequenceDAO(redisSequenceDAO);
		
		sequence.setSequenceConfig(sequenceConfig);
		sequence.setSharingRule(sharingRule);
		
		return sequence;
	}
}