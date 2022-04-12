package com.lixar.apba.domain;

import com.lixar.apba.domain.enums.DieCategoryType;

import javax.validation.constraints.NotNull;

public class DieCategoryId {
    private DieCategoryType type;
    private int id;

    public DieCategoryId() {}

    public DieCategoryId(@NotNull DieCategoryType type, int id) {
        setType(type);
        setId(id);
    }

    public DieCategoryType getType() {
        return type;
    }

    public void setType(DieCategoryType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DieCategoryId that = (DieCategoryId) o;

        if (getId() != that.getId()) return false;
        return getType() == that.getType();

    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + getId();
        return result;
    }
}
