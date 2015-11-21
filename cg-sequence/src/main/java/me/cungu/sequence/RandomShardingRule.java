package me.cungu.sequence;

import java.util.Random;

public class RandomShardingRule implements ShardingRule {
	
	private SequenceConfig sequenceConfig;
	
	private Random random;
	
	public RandomShardingRule(SequenceConfig sequenceConfig) {
		this.random = new Random();
		this.sequenceConfig = sequenceConfig;
	}

	@Override
	public int getShardingIndex() {
		int shardingNum = sequenceConfig.getSharding();
		if (shardingNum > 1) {
			return (random.nextInt(shardingNum) + 1);
		}
		
		return 1;
	}
}