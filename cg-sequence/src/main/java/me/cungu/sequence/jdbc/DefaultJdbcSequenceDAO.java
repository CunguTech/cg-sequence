package me.cungu.sequence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import me.cungu.sequence.SequenceConfig;

public class DefaultJdbcSequenceDAO implements JdbcSequenceDAO {
	
	private DataSource dataSource;
	
	@Override
	public long getValue(SequenceConfig sequenceConfig, int sharding) throws SQLException {
		List<Long> seqValues = new ArrayList<Long>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			ps = conn.prepareStatement("SELECT value FROM cg_sequence WHERE name = ? AND sharding = ?");
			ps.setString(1, sequenceConfig.getSequenceName());
			ps.setInt(2, sharding);
			rs = ps.executeQuery();
			while (rs.next()) {
				seqValues.add(rs.getLong(1));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		
		if (seqValues.size() == 0) {
			throw new IllegalArgumentException("Not exists sequence '"+ sequenceConfig.getSequenceName() + ":" + sharding +"'");
		} else if (seqValues.size() > 1) {
			throw new IllegalStateException("Duplicate sequence '"+ sequenceConfig.getSequenceName() + ":" + sharding +"'");
		}
		
		return seqValues.get(0);
	}

	@Override
	public int updateValue(SequenceConfig sequenceConfig, long newVal, long oldVal, int sharding) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			ps = conn.prepareStatement("UPDATE cg_sequence SET value = ?, update_time = ? WHERE name = ? AND sharding = ? AND value = ?"); // CURRENT_TIMESTAMP()
			ps.setLong(1, newVal);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, sequenceConfig.getSequenceName());
			ps.setInt(4, sharding);
			ps.setLong(5, oldVal);
			return ps.executeUpdate();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}