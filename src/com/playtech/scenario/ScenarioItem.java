package com.playtech.scenario;


import java.util.ArrayList;
import java.util.List;

/**
 *  Class for hold one request/reply scenario step. It represents one  line from scenario file
 *
 *      # Scenario item structure
 *
 *      {                                     - scenario item start
 *      $> 345                                - expected message id (if less than 0 it means not checked)
 *      <= {"key1":"value1","key3":"value3"}  - send json data (can contains ${name} place holders)
 *      => <json-path-1> = value1             - verified json values in received json data
 *      => <json-path-2> = value2
 *              ...
 *      +> key1=<json-path-1>                 - added to scenario defines key/value pairs from received data
 *      +> key2=<json-path-2>
 *              ...
 *      }                                     - scenario item end
 *      ...
 */
public class ScenarioItem {
    private int responseId;
    private String requestBody;

    List<CheckedValue> checkedValues;
    List<ImportedValue> importedValues;

    public ScenarioItem() {
        checkedValues = new ArrayList<>();
        importedValues = new ArrayList<>();

        responseId = 1;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public List<CheckedValue> getCheckedValues() {
        return checkedValues;
    }

    public void setCheckedValues(List<CheckedValue> checkedValues) {
        this.checkedValues = checkedValues;
    }

    public List<ImportedValue> getImportedValues() {
        return importedValues;
    }

    public void setImportedValues(List<ImportedValue> importedValues) {
        this.importedValues = importedValues;
    }
}
