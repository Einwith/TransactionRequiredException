package com.lixar.apba.core.book.model;

import org.thymeleaf.util.StringUtils;

public class Action {
    private String[] parts = new String[3];
    private boolean buff = false;
    private boolean unknownAction = false;

    public String getFirst() {
        return parts[0];
    }

    public void setFirst(String first) {
        this.parts[0] = first;
    }

    public String getSecond() {
        return parts[1];
    }

    public void setSecond(String second) {
        this.parts[1] = second;
    }

    public String getThird() {
        return parts[2];
    }

    public void setThird(String third) {
        this.parts[2] = third;
    }

    public void setPart(int index, String part) {
        if (index < 0 || index > (parts.length + 1)) {
            throw new IndexOutOfBoundsException();
        }

        this.parts[index] = part;
    }

    public String getPart(int index) {
        if (index < 0 || index > (parts.length + 1)) {
            throw new IndexOutOfBoundsException();
        }

        return this.parts[index];
    }

    public String[] getParts() {
        return parts;
    }

    public boolean partEquals(int index, String compareTo) {
        String part = getPart(index);

        return StringUtils.equals(part, compareTo);
    }

    public int getActivePartCount() {
        int count = 0;
        for (String part : parts) {
            if (part != null) {
                count++;
            }
        }

        return count;
    }

    public boolean isBuff() {
        return buff;
    }

    public void setBuff(boolean buff) {
        this.buff = buff;
    }

    public boolean isUnknownAction() {
        return unknownAction;
    }

    public void setUnknownAction(boolean unknownAction) {
        this.unknownAction = unknownAction;
    }
}
