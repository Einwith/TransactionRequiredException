package com.lixar.apba.core.sql;

import com.lixar.apba.core.util.SQLUtil;
import com.lixar.apba.domain.AvatarStat;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class AvatarStatBulkInsert extends AbstractBulkInsert<AvatarStat> {

	public AvatarStatBulkInsert(Collection<AvatarStat> entities) {
		super(entities);
	}

	@Override
	protected String getInsertSQL() {
		return "INSERT INTO avatar_stat (client, avatar, stat, start) VALUES (?, ?, ?, ?)";
	}

	@Override
	protected void fillStatement(PreparedStatement insertStatement, AvatarStat stat) throws SQLException {
		int index = 1;

		SQLUtil.setIntOrNull(insertStatement, index++, stat.getClient());
		SQLUtil.setIntOrNull(insertStatement, index++, stat.getAvatar());
		SQLUtil.setIntOrNull(insertStatement, index++, stat.getStat());
		SQLUtil.setStringOrNull(insertStatement, index++, stat.getStart());
	}
}
