package me.cungu.sequence;

import java.util.Map;

public interface SequenceConfig {

	String getSequenceName();

	int getCache();
	
	int getSharding();

	// int getIncrementBy();

	// int getStartWith();

	// boolean isNoMaxValue();

	// boolean isNoCycle();
	
	Map<String, Object> getProperties();

	<T> T get(String key, Class<T> requiredType);
	
	<T> T get(String key, Class<T> requiredType, T defaultValue);
}