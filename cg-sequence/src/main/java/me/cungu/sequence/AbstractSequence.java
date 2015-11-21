package me.cungu.sequence;

public abstract class AbstractSequence implements Sequence {
	
	protected SequenceConfig sequenceConfig;
	
	protected ShardingRule sharingRule;
	
	@Override
	public SequenceConfig getSequenceConfig() {
		return sequenceConfig;
	}

	public void setSequenceConfig(SequenceConfig sequenceConfig) {
		this.sequenceConfig = sequenceConfig;
	}

	public ShardingRule getSharingRule() {
		return sharingRule;
	}

	public void setSharingRule(ShardingRule sharingRule) {
		this.sharingRule = sharingRule;
	}
}