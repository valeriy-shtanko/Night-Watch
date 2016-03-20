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

public class ScenarioException extends Exception {
    public ScenarioException() {
    }

    public ScenarioException(String message){
        super(message);
    }

    public ScenarioException(String format, Object... args){
        super(String.format(format, args));
    }

    public ScenarioException(String message, Throwable cause){
        super(message, cause);
    }
}
