package com.lixar.apba.core.book.model;

import com.lixar.apba.core.book.validator.StringValidator;

import java.util.Map;

public class AllowedAction {

    public static final int INVALID_TEXT_INPUT_INDEX = -1;

    private String value;
    private String text;
    private String description;
    private boolean canBuff;
    private Map<String, String> allowedSecondEntries;
    private Map<String, String> allowedThirdEntries;
    private int textInputIndex = INVALID_TEXT_INPUT_INDEX;
    private StringValidator inputValidator;
    private Map<String, String> validationContext;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCanBuff() {
        return canBuff;
    }

    public void setCanBuff(boolean canBuff) {
        this.canBuff = canBuff;
    }

    public Map<String, String> getAllowedSecondEntries() {
        return allowedSecondEntries;
    }

    public void setAllowedSecondEntries(Map<String, String> allowedSecondEntries) {
        this.allowedSecondEntries = allowedSecondEntries;
    }

    public Map<String, String> getAllowedThirdEntries() {
        return allowedThirdEntries;
    }

    public void setAllowedThirdEntries(Map<String, String> allowedThirdEntries) {
        this.allowedThirdEntries = allowedThirdEntries;
    }

    public int getTextInputIndex() {
        return textInputIndex;
    }

    public void setTextInputIndex(int textInputIndex) {
        this.textInputIndex = textInputIndex;
    }

    public StringValidator getInputValidator() {
        return inputValidator;
    }

    public void setInputValidator(StringValidator inputValidator) {
        this.inputValidator = inputValidator;
    }

    public Map<String, String> getValidationContext() {
        return validationContext;
    }

    public void setValidationContext(Map<String, String> validationContext) {
        this.validationContext = validationContext;
    }
}
