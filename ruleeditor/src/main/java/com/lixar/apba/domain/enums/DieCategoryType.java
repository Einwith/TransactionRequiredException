package com.lixar.apba.domain.enums;

public enum DieCategoryType {
    DicePool("dice_pool", "page"),
    DiceSid("dice_sid", "section");

	/**
	 * The following attributes are unused in this class, but referenced elsewhere in the application
	 */
    @SuppressWarnings("unused")
	private String diceResultColumnName;
    @SuppressWarnings("unused")
	private String bookColumnName;

    DieCategoryType(String diceResultColumnName, String bookColumnName) {
        this.diceResultColumnName = diceResultColumnName;
        this.bookColumnName = bookColumnName;
    }
}
