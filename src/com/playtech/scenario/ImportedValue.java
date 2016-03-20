package com.playtech.scenario;

/**
 * Created by valeriy on 3/8/16.
 */
public class ImportedValue {
    private String path;
    private String key;

    public ImportedValue(String key, String path) {
        this.key = key;
        this.path = path;
    }

    public ImportedValue() {}

    public String getPath() { return path; }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
