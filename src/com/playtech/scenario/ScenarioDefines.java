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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * ScenarioDefines class
 *
 */
public class ScenarioDefines {
    private static final Logger log = Logger.getLogger(ScenarioDefines.class.getName());

    private Map<String, Object> definesMap;
    private Map<String, Object> constantsMap;

    public ScenarioDefines() {
        constantsMap  = new HashMap<>();
        definesMap = new HashMap<>();
    }

    public ScenarioDefines setString(String name, String value) {
        definesMap.put(name, value);

        log.info(String.format("[ Defines item  set ] %s = %s", name, value));

        return this;
    }

    public ScenarioDefines setStringAsConstant(String name, String value) {
        constantsMap.put(name, value);

        log.info(String.format("[ Defines item  set ] %s = %s", name, value));

        return this;
    }

    public String getString(String name) {
        Object result = constantsMap.get(name);

        return result != null ? (String) result
                              : (String) definesMap.get(name);
    }

    public ScenarioDefines setInteger(String name, int value) {
        definesMap.put(name, new Integer(value));

        log.info(String.format("[ Defines item  set ] %s = %d", name, value));

        return this;
    }

    public ScenarioDefines setIntegerAsConstant(String name, int value) {
        constantsMap.put(name, new Integer(value));

        log.info(String.format("[ Defines item  set ] %s = %d", name, value));

        return this;
    }

    public int getInteger(String name) {
        Object result = constantsMap.get(name);

        return result != null ? (Integer) result
                              : (Integer) definesMap.get(name);
    }

    public ScenarioDefines setObject(String name, Object value) {
        definesMap.put(name, value);

        log.info(String.format("[ Defines item  set  ] %s = %s", name, value.toString()));

        return this;
    }

    public ScenarioDefines setObjectAsConstant(String name, Object value) {
        constantsMap.put(name, value);

        log.info(String.format("[ Defines item  set  ] %s = %s", name, value.toString()));

        return this;
    }

    public Object getObject(String name) {
        Object result = constantsMap.get(name);

        return result != null ? result : definesMap.get(name);
    }

    public void clear() {
        // constantsMap never clears
        definesMap.clear();
    }

    public Map<String, Object> export() {
        Map<String, Object> result = new HashMap<>();

        result.putAll(constantsMap);
        result.putAll(definesMap);

        return result;
    }
}
