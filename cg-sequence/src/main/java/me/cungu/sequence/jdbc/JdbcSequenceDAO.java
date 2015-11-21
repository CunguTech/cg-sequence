package me.cungu.sequence.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

import me.cungu.sequence.SequenceConfig;

public interface JdbcSequenceDAO {
	
	long getValue(SequenceConfig sequenceConfig, int sharding) throws SQLException;
	
	int updateValue(SequenceConfig sequenceConfig, long newVal, long oldVal, int sharding) throws SQLException;
	
	DataSource getDataSource();

	void setDataSource(DataSource dataSource);
}
