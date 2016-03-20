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

package com.playtech;

import com.playtech.environment.Environment;
import com.playtech.environment.EnvironmentConfig;
import com.playtech.scenario.ProcessingState;

import java.io.File;
import java.util.logging.Logger;

/**
 * Bootstrap
 *
 */
public class NighWatch {
    private static final Logger log = Logger.getLogger(NighWatch.class.getName());

    public static void main(String[] args) throws Exception {
        // Check path to environment configuration
        if(args.length == 0) {
            log.info("Path to environment configuration not defined");
            return;
        }

        String filePath = args[0];

        if (!checkFile(filePath)) {
            return;
        }

        // Environment configuration
        EnvironmentConfig config = new EnvironmentConfig().load(filePath);

        // Creating Environment
        Environment environment = new Environment(config);

        // Starting scenario execution
        environment.startProcessing();

        // Waiting for scenario execution complete
        while(environment.getState() == ProcessingState.RUNNING) {
            Thread.sleep(500);
        }
    }


    private static boolean checkFile(String filePath) {
        File f = new File(filePath);

        if(!f.exists()) {
            log.severe(String.format("File '%s' does not found", filePath));
            return false;
        }

        if(f.isDirectory()) {
            log.severe(String.format("File '%s' is a directory", filePath));
            return false;
        }

        return true;
    }
}
