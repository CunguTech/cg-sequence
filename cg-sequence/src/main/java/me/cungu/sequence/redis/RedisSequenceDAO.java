package me.cungu.sequence.redis;

import me.cungu.sequence.SequenceConfig;

public interface RedisSequenceDAO {
	
	long incrBy(SequenceConfig sequenceConfig, long incrementBy, int sharding);
	
}