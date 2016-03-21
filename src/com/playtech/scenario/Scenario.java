// ==========================================================================
// Copyright Notice!
//
// This file contains proprietary and confidential trade secrets of Playtech.
// Reproduction, disclosure or use without specific written authorization
// from Playtech is prohibited.
//
// (C) Copyright 2016 Playtech.
// All rights reserved.
// ==========================================================================

package com.playtech.scenario;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Scenario class
 *
 */
public class Scenario {
    private static final Logger log = Logger.getLogger(Scenario.class.getName());

    private String fileName;
    private List<ScenarioItem> scenarioItems;
    private List<Integer> skippedIDs;
    private ScenarioDefines scenarioDefines;

    public static final String COMMENT_PREFIX        = "#";
    public static final String SKIPPED_ID_PREFIX     = "->";
    public static final String DEFINE_PREFIX         = "*>";
    public static final String RESPONSE_ID_PREFIX    = "$>";
    public static final String REQUEST_PREFIX        = "<=";
    public static final String CHECKED_VALUE_PREFIX  = "=>";
    public static final String IMPORTED_VALUE_PREFIX = "+>";
    public static final String SKIP_CONDITION_PREFIX = "?>";
    public static final String START_ITEM_PREFIX     = "{";
    public static final String END_ITEM_PREFIX       = "}";


    public Scenario () {
        scenarioItems = new ArrayList<>();
        skippedIDs = new ArrayList<>();
        scenarioDefines = new ScenarioDefines();
    }

    public List<ScenarioItem> getItems() {
        return scenarioItems;
    }

    public ScenarioDefines getDefines() {
        return scenarioDefines;
    }

    public List<Integer> getSkippedIDs() {
        return skippedIDs;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     *  Load all steps from a scenario file
     *
     *      # Scenario structure
     *      -> 1234, 2345, 123, 345               - skipped messages id
     *      *> key1=value1                        - key/value pairs which will be added to defines
     *      *> key2=value2
     *          ...
     *      {                                     - scenario item start
     *      $> 345                                - expected message id
     *      <= {"key1":"value1","key3":"value3"}  - send json data (can contains ${name} place holders)
     *      => <json-path-1>=value1               - verified json values in received json data
     *      => <json-path-2>=value2
     *              ...
     *      +> key1=<json-path-1>                 - added to scenario defines key/value pairs from received data
     *      +> key2=<json-path-2>
     *              ...
     *      ?> <json-path-1> = value1             - skip message conditions, message will be skipped if one of them is true
     *      ?> <json-path-2> = value2
     *              ...
     *      }                                     - scenario item end
     *      ...
     */
    public Scenario load(String fileName) throws ScenarioException {
        log.info(String.format("Loading scenario '%s'...", fileName));

        this.fileName = fileName;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            ScenarioItem scenarioItem = null;

            // reading file file line by line
            while ((line = br.readLine()) != null) {
                // skip empty lines
                if(line.trim().isEmpty()) {
                    continue;
                }

                // Comments
                if(line.startsWith(COMMENT_PREFIX)){
                    continue;
                }

                // skipped message IDs
                if(line.startsWith(SKIPPED_ID_PREFIX)){
                    processSkippedIDs(removePrefix(line));
                    continue;
                }

                if(line.startsWith(DEFINE_PREFIX)) {
                    processDefine(removePrefix(line));
                }

                if(line.startsWith(START_ITEM_PREFIX)){
                    scenarioItem = new ScenarioItem();
                    continue;
                }

                if(line.startsWith(RESPONSE_ID_PREFIX)){
                    scenarioItem.setResponseId(Integer.valueOf(removePrefix(line)));
                    continue;
                }

                if(line.startsWith(REQUEST_PREFIX)){
                    scenarioItem.setRequestBody(removePrefix(line));
                    continue;
                }

                if(line.startsWith(CHECKED_VALUE_PREFIX)) {
                    scenarioItem.getCheckedValues()
                                .add(processCheckedValue(removePrefix(line)));
                    continue;
                }

                if(line.startsWith(IMPORTED_VALUE_PREFIX)){
                    scenarioItem.getImportedValues()
                                .add(processImportedValue(removePrefix(line)));
                    continue;
                }

                if(line.startsWith(SKIP_CONDITION_PREFIX)){
                    scenarioItem.getSkipConditions()
                                .add(processSkipCondition(removePrefix(line)));
                    continue;
                }
                
                if(line.startsWith(END_ITEM_PREFIX)){
                    scenarioItems.add(scenarioItem);
                    scenarioItem = null;
                    continue;
                }
            }
        }
        catch(Exception e) {
            throw new ScenarioException(String.format("Cannot load scenario file '%s'", fileName), e);
        }

        log.info(String.format("Completed. Scenario contains %d steps", scenarioItems.size()));

        return this;
    }

    private CheckedValue processCheckedValue(String line) {
        String[] items = StringUtils.split(line, "=");

        return new CheckedValue(items[0].trim(), items[1].trim());
    }

    private ImportedValue processImportedValue(String line){
        String[] items = StringUtils.split(line, "=");

        return new ImportedValue(items[0].trim(), items[1].trim());
    }

    private PathValue processSkipCondition(String line){
        String[] items = StringUtils.split(line, "=");

        return new PathValue(items[0].trim(), items[1].trim());
    }



    private void processSkippedIDs(String line) {
        String[] IDs = StringUtils.split(line, ",");

        for(String item : IDs) {
            skippedIDs.add(new Integer(item.trim()));
        }
    }

    private String removePrefix(String line) {
        String result = null;

        if(line.startsWith(SKIPPED_ID_PREFIX)) {
            result = StringUtils.removeStart(line,SKIPPED_ID_PREFIX);
        }

        if(line.startsWith(RESPONSE_ID_PREFIX)) {
            result = StringUtils.removeStart(line,RESPONSE_ID_PREFIX);
        }

        if(line.startsWith(REQUEST_PREFIX)) {
            result = StringUtils.removeStart(line,REQUEST_PREFIX);
        }

        if(line.startsWith(CHECKED_VALUE_PREFIX)) {
            result = StringUtils.removeStart(line,CHECKED_VALUE_PREFIX);
        }

        if(line.startsWith(IMPORTED_VALUE_PREFIX)) {
            result = StringUtils.removeStart(line, IMPORTED_VALUE_PREFIX);
        }

        if(line.startsWith(START_ITEM_PREFIX)) {
            result = StringUtils.removeStart(line,START_ITEM_PREFIX);
        }

        if(line.startsWith(END_ITEM_PREFIX)) {
            result = StringUtils.removeStart(line,END_ITEM_PREFIX);
        }

        return result.trim();
    }

    private void processDefine(String line) {
        String[] items = StringUtils.split(line, "=");

        scenarioDefines.setStringAsConstant(items[0].trim(), items[1].trim());
    }
}
