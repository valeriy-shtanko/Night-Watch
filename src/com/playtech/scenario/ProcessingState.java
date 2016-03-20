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

/**
 * ProcessingState enum
 */
public enum ProcessingState {
    READY,          // ready to execute
    RUNNING,        // executing
    COMPLETE,       // finished
    TERMINATED      // abnormal termination
}
