package com.playtech.scenario;

/**
 * Created by valeriy on 3/8/16.
 */
public class CheckedValue {
    private String path;
    private String value;

    public CheckedValue(String path, String value) {
        this.path = path;
        this.value = value;
    }

    public CheckedValue() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
