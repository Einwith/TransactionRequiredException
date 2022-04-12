package com.lixar.apba.core.sql;

import com.lixar.apba.core.util.SQLUtil;
import com.lixar.apba.domain.AvatarDice;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class AvatarDiceBulkInsert extends AbstractBulkInsert<AvatarDice> {

	public AvatarDiceBulkInsert(Collection<AvatarDice> entities) {
		super(entities);
	}

	@Override
	protected String getInsertSQL() {
		return "INSERT INTO avatar_dice (client, avatar, dice, priority, name) VALUES (?, ?, ?, ?, ?)";
	}

	@Override
	protected void fillStatement(PreparedStatement insertStatement, AvatarDice die) throws SQLException {
		int index = 1;

		SQLUtil.setIntOrNull(insertStatement, index++, die.getClient());
		SQLUtil.setIntOrNull(insertStatement, index++, die.getAvatar());
		SQLUtil.setIntOrNull(insertStatement, index++, die.getDice());
		SQLUtil.setIntOrNull(insertStatement, index++, die.getPriority());
		//noinspection UnusedAssignment - keep the ++ as guard for further inserts
		SQLUtil.setStringOrNull(insertStatement, index++, die.getName());
	}
}
