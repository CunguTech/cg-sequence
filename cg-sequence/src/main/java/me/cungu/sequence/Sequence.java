package me.cungu.sequence;

public interface Sequence {
	
	SequenceConfig getSequenceConfig();
	
	long currVal();
	
	long nextVal();
}