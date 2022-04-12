package com.lixar.apba.core.book.model;

public class PlayId {
    private int dicePool;
    private int diceSid;
    private String requirement;

    public PlayId() {}

    public PlayId(int dicePool, int diceSid, String requirement) {
        setDicePool(dicePool);
        setDiceSid(diceSid);
        setRequirement(requirement);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayId playId = (PlayId) o;

        if (getDicePool() != playId.getDicePool()) return false;
        if (getDiceSid() != playId.getDiceSid()) return false;
        return getRequirement().equals(playId.getRequirement());

    }

    @Override
    public int hashCode() {
        int result = getDicePool();
        result = 31 * result + getDiceSid();
        result = 31 * result + getRequirement().hashCode();
        return result;
    }
}
