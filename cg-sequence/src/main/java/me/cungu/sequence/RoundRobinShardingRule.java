package me.cungu.sequence;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinShardingRule implements ShardingRule {
	
	private SequenceConfig sequenceConfig;
	
	private AtomicInteger nextIndex = new AtomicInteger(0);
	
	public RoundRobinShardingRule(SequenceConfig sequenceConfig) {
		this.sequenceConfig = sequenceConfig;
	}

	@Override
	public int getShardingIndex() {
		int shardingNum = sequenceConfig.getSharding();
		if (shardingNum > 1) {
			return (nextIndex() % shardingNum) + 1;
		}
		
		return 1;
	}
	
	private int nextIndex() {
		nextIndex.compareAndSet(Integer.MAX_VALUE, 0);
		return nextIndex.getAndIncrement();
	}
	
	public static void main(String[] args) {
		RoundRobinShardingRule r = new RoundRobinShardingRule(SequenceBuilder.newBuild().setSequenceConfig("a", 10, 5).buildSequenceConfig());
		for (int i = 0 ; i < 100; i++) {
		int ai  = r.getShardingIndex();
		System.out.println(ai);
		}
	}
}