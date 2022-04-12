package com.lixar.apba.core.sql;

import com.lixar.apba.core.util.SQLUtil;
import org.hibernate.jdbc.Work;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public abstract class AbstractBulkInsert<K> implements Work {
	private Collection<K> entities;
	private int count = 0;
	private boolean isAutoCommit = false;
	private int maximumBatchSize = 10000;
	private PreparedStatement insertStatement;
	private Connection connection; // must not be closed

	public AbstractBulkInsert(Collection<K> entities) {
		this.entities = entities;
	}

	@Override
	public void execute(Connection connection) throws SQLException {
		try {
			this.connection = connection;
			isAutoCommit = connection.getAutoCommit();

			insertStatement = connection.prepareStatement(getInsertSQL());

			for (K entity : entities) {
				fillStatement(insertStatement, entity);

				insertStatement.addBatch();
				count++;

				checkExecuteBatch();
			}

			executeBatch();
		} finally {
			SQLUtil.safeClose(insertStatement);
		}
	}

	private void checkExecuteBatch() throws SQLException {
		if (count >= maximumBatchSize) {
			executeBatch();
		}
	}

	private void executeBatch() throws SQLException {
		if (count == 0) {
			return;
		}

		insertStatement.executeBatch();
		if (!isAutoCommit) {
			connection.commit();
		}
		count = 0;
	}

	protected abstract String getInsertSQL();

	protected abstract void fillStatement(PreparedStatement insertStatement, K entity) throws SQLException;
}
