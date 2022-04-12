package com.lixar.apba.core.sql;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class MySQLStringBuilder {
    public static final String NULL = "NULL";

    private StringBuilder builder = new StringBuilder();

    public void appendRaw(String s) {
        if (s == null) {
            return;
        }

        builder.append(s);
    }

    public void appendValue(boolean b) {
        appendValue(b, true);
    }

    public void appendValue(boolean b, boolean includeComma) {
        builder.append(Boolean.toString(b));

        appendComma(includeComma);
    }

    public void appendValue(int i) {
        appendValue(i, true);
    }

    public void appendValue(int i, boolean includeComma) {
        builder.append(i);

        appendComma(includeComma);
    }

    public void appendValue(long l) {
        appendValue(l, true);
    }

    public void appendValue(long l, boolean includeComma) {
        builder.append(l);

        appendComma(includeComma);
    }

    public void appendValue(String s) {
        appendValue(s, true);
    }

    public void appendValue(String s, boolean includeComma) {
        if (s == null) {
            builder.append(NULL);
        } else {
            builder.append("'").append(escapeString(s)).append("'");
        }

        appendComma(includeComma);
    }

    public void appendValue(Object o) {
        appendValue(o, true);
    }

    public void appendValue(Object o, boolean includeComma) {
        if (o == null) {
            builder.append(NULL);
        } else {
            builder.append(String.valueOf(o));
        }

        appendComma(includeComma);
    }

    private String escapeString(String s) {
        s = StringUtils.replace(s, "\\", "\\\\");
        s = StringUtils.replace(s, "\'", "\\\'");
        s = StringUtils.replace(s, "\"", "\\\"");
        s = StringUtils.replace(s, "\n", "\\n");
        s = StringUtils.replace(s, "\r", "\\r");
        s = StringUtils.replace(s, "\t", "\\t");

        return s;
    }

    public void appendComma(boolean includeComma) {
        if (includeComma) {
            builder.append(",");
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}

