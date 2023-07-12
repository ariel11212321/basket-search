package com.example.a12thproject.widgets;

import java.util.Objects;

public class Item {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public Item() {}
    public Item(String name, Boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getEtValue() {
        return etValue;
    }

    public void setEtValue(String etValue) {
        this.etValue = etValue;
    }

    private String etValue;


    private String name;
    private Boolean value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) && Objects.equals(value, item.value) && Objects.equals(textValue, item.textValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, textValue);
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    private String textValue;
}