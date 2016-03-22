package com.playtech.scenario;

/**
 * Created by valeriy on 3/21/16.
 */
public class PathValue {
    private String path;
    private String value;

    public PathValue(String path, String value) {
        this.path = path;
        this.value = value;
    }

    public PathValue() {
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
