package com.lixar.apba.domain;

public class OutcomeValidationError {
    private int dicePool;
    private int diceSid;
    private String requirement;
    private String csvConditions;
    private String errorMessage;

    public OutcomeValidationError() {
    }

    public OutcomeValidationError(int dicePool, int diceSid, String requirement, String csvCondition, String errorMessage) {
        setDicePool(dicePool);
        setDiceSid(diceSid);
        setRequirement(requirement);
        setCsvConditions(csvCondition);
        setErrorMessage(errorMessage);
    }

    public int getDicePool() {
        return dicePool;
    }

    public void setDicePool(int dicePool) {
        this.dicePool = dicePool;
    }

    public int getDiceSid() {
        return diceSid;
    }

    public void setDiceSid(int diceSid) {
        this.diceSid = diceSid;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getCsvConditions() {
        return csvConditions;
    }

    public void setCsvConditions(String csvConditions) {
        this.csvConditions = csvConditions;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
