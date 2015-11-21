package me.cungu.sequence.redis;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.cungu.sequence.SequenceConfig;
import redis.clients.jedis.Jedis;

public class DefaultRedisSequenceDAO implements RedisSequenceDAO { // 没有容错处理
	
	public static String CONFIG_REDIS_SERVERS = "redisServers";
	public static String CONFIG_REDIS_DATABASE = "redisDatabase";
	public static String CONFIG_REDIS_TIMEOUT = "redisTimeout";

	private List<Jedis> jedisList = null;

	@Override
	public long incrBy(SequenceConfig sequenceConfig, long incrementBy, int sharding) { // IN LOCKED
		if (jedisList == null) {
			initJedisList(sequenceConfig);
		}
		
		int jedisListIndex = sharding - 1;
		Jedis jedis = jedisList.get(jedisListIndex);
		long value = jedis.incrBy(sequenceConfig.getSequenceName(), incrementBy);
		
		return value;
	}
	
	public void initJedisList(SequenceConfig sequenceConfig) {
		if (jedisList != null) {
			for (Jedis jedis : jedisList) {
				try {
					jedis.close();
				} catch (Exception e) {
					// NOP
				}
			}
		}
		
		String redisServers = sequenceConfig.get("redisServers", String.class);
		int redisDatabase = sequenceConfig.get("redisDatabase", Integer.class, 0);
		int redisTimeout = sequenceConfig.get("redisTimeout", Integer.class, 2000);
		
		jedisList = new CopyOnWriteArrayList<Jedis>();
		for (String redisServer : redisServers.split(",")) {
			String[] redisServerArr = redisServer.split(":");
			Jedis jedis = new Jedis(redisServerArr[0], new Integer(redisServerArr[1]), redisTimeout);
			jedis.select(redisDatabase);
			jedisList.add(jedis);
		}
	}
}