package com.lixar.apba.domain;

import javax.validation.constraints.NotNull;

public class MinorDiceCategory {
    private DieCategoryId id;
    private String title;
    private int minimumDie;
    private int maximumDie;

    public MinorDiceCategory() {}

    public MinorDiceCategory(DieCategoryId id, String title, int minimumDie, int maximumDie) {
        setId(id);
        setTitle(title);
        setMinimumDie(minimumDie);
        setMaximumDie(maximumDie);
    }

    public DieCategoryId getId() {
        return id;
    }

    public void setId(@NotNull DieCategoryId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMinimumDie() {
        return minimumDie;
    }

    public void setMinimumDie(int minimumDie) {
        this.minimumDie = minimumDie;
    }

    public int getMaximumDie() {
        return maximumDie;
    }

    public void setMaximumDie(int maximumDie) {
        this.maximumDie = maximumDie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinorDiceCategory that = (MinorDiceCategory) o;

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
