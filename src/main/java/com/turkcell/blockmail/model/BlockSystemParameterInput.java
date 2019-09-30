package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockSystemParameterInput implements Serializable {
    private static final long serialVersionUID = 6818043861119256593L;
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BlockSystemParameterInput{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
