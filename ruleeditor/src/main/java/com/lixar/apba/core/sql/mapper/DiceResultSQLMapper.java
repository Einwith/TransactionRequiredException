package com.lixar.apba.core.sql.mapper;

import com.lixar.apba.core.sql.MySQLStringBuilder;
import com.lixar.apba.core.sql.SQLInsertMap;
import com.lixar.apba.domain.DiceResult;

public class DiceResultSQLMapper implements SQLInsertMap<DiceResult> {
	@Override
	public String toInsertSQLString(DiceResult diceResult) {
		MySQLStringBuilder builder = new MySQLStringBuilder();

		if (diceResult != null) {
			builder.appendRaw("INSERT INTO `dice_result` (`id`,`client`,`dice`,`dice_sid`,`dice_pool`,`requirement`,`result_parser`,`result_console`,`result_human`,`result_text`,`trigger_type`,`trigger_id`,`trigger_sid`,`trigger_pool`,`trigger_end`) VALUES (");
			builder.appendValue(diceResult.getId());
			builder.appendValue(diceResult.getClient());
			builder.appendValue(diceResult.getDice());
			builder.appendValue(diceResult.getDiceSid());
			builder.appendValue(diceResult.getDicePool());
			builder.appendValue(diceResult.getRequirement());
			builder.appendValue(diceResult.getResultParser());
			builder.appendValue(diceResult.getResultConsole());
			builder.appendValue(diceResult.getResultHuman());
			builder.appendValue(diceResult.getResultText());
			builder.appendValue(diceResult.getTriggerType());
			builder.appendValue(diceResult.getTriggerId());
			builder.appendValue(diceResult.getTriggerSid());
			builder.appendValue(diceResult.getTriggerPool());
			builder.appendValue(diceResult.getTriggerEnd(), false);
			builder.appendRaw(");");
		}

		return builder.toString();
	}
}
