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

package com.playtech.environment;

import com.playtech.common.Defines;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * EnvironmentConfig config
 *
 */
public class EnvironmentConfig {
    private static final Logger log = Logger.getLogger(EnvironmentConfig.class.getName());

    private String  host;
    private int port;
    private String casinoName;
    private boolean useSSL;
    private long timeout;
    private String userName;
    private String userPassword;
    private String clientSkin;

    private String scenarioFile;

    public String getHost() {
        return host;
    }

    public EnvironmentConfig setHost(String host) {
        this.host = host;

        return this;
    }

    public int getPort() {
        return port;
    }

    public EnvironmentConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getCasinoName() {
        return casinoName;
    }

    public EnvironmentConfig setCasinoName(String casinoName) {
        this.casinoName = casinoName;
        return this;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public EnvironmentConfig setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public EnvironmentConfig setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getScenarioFile() {
        return scenarioFile;
    }

    public EnvironmentConfig setScenarioFile(String scenarioFile) {
        this.scenarioFile = scenarioFile;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public EnvironmentConfig setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public EnvironmentConfig setUserPassword(String userPassword) {
        this.userPassword = userPassword;
        return this;
    }

    public String getClientSkin() {
        return clientSkin;
    }

    /**
     * Loading environment configuration from properties file by file path name
     * @param fileName - environment configuration file name
     * @return - ...
     * @throws IOException
     */
    public EnvironmentConfig load(String fileName) throws IOException {
        log.info(String.format("Loading environment configuration from '%s'", fileName));

        Properties properties = new Properties();
        properties.load(new FileInputStream(fileName));

        //  Setting configuration setting/parameters from the configuration for the environment file
        host         = properties.getProperty(Defines.HOST, "").trim();
        port         = Integer.parseInt(properties.getProperty(Defines.PORT, "0").trim());
        casinoName   = properties.getProperty(Defines.CASINO_NAME).trim();
        useSSL       = Boolean.parseBoolean(properties.getProperty(Defines.USE_SSL, "false").trim());
        timeout      = Integer.parseInt(properties.getProperty(Defines.TIMEOUT, "1000").trim());
        userName     = properties.getProperty(Defines.USER_NAME, "").trim();
        userPassword = properties.getProperty(Defines.USER_PASSWORD, "").trim();
        clientSkin   = properties.getProperty(Defines.CLIENT_SKIN, "").trim();
        scenarioFile = properties.getProperty(Defines.SCENARIO_FILE, "").trim();

        return this;
    }
}
