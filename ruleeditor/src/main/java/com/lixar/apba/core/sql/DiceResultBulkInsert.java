package com.lixar.apba.core.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import com.lixar.apba.core.util.SQLUtil;
import com.lixar.apba.domain.DiceResult;

public class DiceResultBulkInsert extends AbstractBulkInsert<DiceResult> {

	public DiceResultBulkInsert(Collection<DiceResult> entities) {
		super(entities);
	}

	@Override
	protected String getInsertSQL() {
		return "INSERT INTO dice_result (client, dice, dice_sid, dice_pool, requirement, result_parser, result_console, result_human, result_text, trigger_type, trigger_id, trigger_sid, trigger_pool, trigger_end) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected void fillStatement(PreparedStatement insertStatement, DiceResult diceResult) throws SQLException {
		int index = 1;

		SQLUtil.setIntOrNull(insertStatement, index++, diceResult.getClient());
		SQLUtil.setIntOrNull(insertStatement, index++, diceResult.getDice());
		SQLUtil.setIntOrNull(insertStatement, index++, diceResult.getDiceSid());
		SQLUtil.setIntOrNull(insertStatement, index++, diceResult.getDicePool());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getRequirement());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getResultParser());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getResultConsole());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getResultHuman());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getResultText());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getTriggerType());
		SQLUtil.setStringOrNull(insertStatement, index++, diceResult.getTriggerId());
		SQLUtil.setIntOrNull(insertStatement, index++, diceResult.getTriggerSid());
		SQLUtil.setIntOrNull(insertStatement, index++, diceResult.getTriggerPool());
		//noinspection UnusedAssignment - keep the ++ as guard for further inserts
		SQLUtil.setBooleanOrNull(insertStatement, index++, diceResult.getTriggerEnd());
	}
}
