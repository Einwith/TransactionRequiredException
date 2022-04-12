package com.lixar.apba.core.php;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class PHPStringBuilder {
    public static final String NULL = "null";

    private StringBuilder builder = new StringBuilder();

    public PHPStringBuilder appendRaw(String s) {
        if (s == null) {
            return this;
        }

        builder.append(s);

        return this;
    }

    public PHPStringBuilder appendRawLine(String s) {
        appendRaw(s);
        appendNewLine();

        return this;
    }

    public PHPStringBuilder appendNewLine() {
        builder.append("\n");
        return this;
    }

    public PHPStringBuilder appendValue(boolean b) {
        builder.append(Boolean.toString(b));
        return this;
    }

    public PHPStringBuilder appendValue(int i) {
        builder.append(i);
        return this;
    }

    public PHPStringBuilder appendValue(long l) {
        builder.append(l);
        return this;
    }

    public PHPStringBuilder appendValue(String s) {
        if (s == null) {
            builder.append(NULL);
        } else {
            builder.append("'").append(addSlashes(s)).append("'");
        }

        return this;
    }

    public PHPStringBuilder appendValue(Object o) {
        if (o == null) {
            builder.append(NULL);
        } else {
            appendValue(String.valueOf(o));
        }

        return this;
    }

	private String addSlashes(String toEscape) {
        toEscape = StringUtils.replace(toEscape, "\\", "\\\\");
        toEscape = StringUtils.replace(toEscape, "'", "\\'");

        return toEscape;
    }

    public PHPStringBuilder appendPersistAndFlush(String variableName) {
        appendRaw("$manager->persist(").appendRaw(variableName).appendRaw(");");
        appendRawLine("$manager->flush();");

        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}

