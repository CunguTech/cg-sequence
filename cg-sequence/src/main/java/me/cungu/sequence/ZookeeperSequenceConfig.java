package me.cungu.sequence;

import java.util.Map;

import me.cungu.sequence.SequenceConfig;

public class ZookeeperSequenceConfig implements SequenceConfig {
	
	@Override
	public String getSequenceName() {
		return null;
	}
	
	@Override
	public int getCache() {
		return 10;
	}
	
	@Override
	public int getSharding() {
		return 1;
	}

	@Override
	public Map<String, Object> getProperties() {
		return null;
	}

	@Override
	public <T> T get(String key, Class<T> requiredType) {
		return null;
	}

	@Override
	public <T> T get(String key, Class<T> requiredType, T defaultValue) {
		return null;
	}
}