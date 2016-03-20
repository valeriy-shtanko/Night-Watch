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
import com.playtech.scenario.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

/**
 *  Environment class
 *
 */
public class Environment implements java.io.Serializable{
    private static final Logger log = Logger.getLogger(Environment.class.getName());

    private EnvironmentConfig config;

    private Socket socket;
    private Scenario scenario;

    private ScenarioProcessor scenarioProcessor;

    public Environment(EnvironmentConfig config) {
        this.config = config;
    }

    /**
     * Utility method to create Socket connection to an environment
     *
     * @return Socket
     * @throws IOException
     */
    private Socket getSocket() throws IOException {
        Socket socket;

        log.info("Creating socket " + config.getHost() + ":" + config.getPort());

        socket = new Socket(config.getHost(), config.getPort());

        return socket;
    }

    /**
     * Utility method to create SSL Socket connection to an environment
     *
     * @return SSLSocket
     * @throws IOException
     */
    private SSLSocket getSSLSocket() throws IOException {
        SSLSocket sslSocket;

        log.info("Creating SSL socket " + config.getHost() + ":" + config.getPort());

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        sslSocket = (SSLSocket) socketFactory.createSocket(config.getHost(), config.getPort());


        return sslSocket;
    }

    /**
     * Central method to create Socket connection to an environment. It uses getSocket() and getSSLSocket() methods
     *
     * @return Socket
     * @throws IOException
     */
    public Socket getEnvironmentSocket () throws IOException {
        if (socket != null) {
            return socket;
        };

        if (config.isUseSSL()) {
             socket = getSSLSocket();
        }
        else {
            socket = getSocket();
        }

        return socket;
    }

    public void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            };

        }
        catch(Exception ignore) {}

        socket = null;
    }

    /**
     * Return current scenario execution state
     *
     */
    public ProcessingState getState() {
        if(scenarioProcessor == null) {
            return ProcessingState.READY;
        } else {
            return scenarioProcessor.getState();
        }
    }

    /**
     * Start scenario execution
     *
     */
    public void startProcessing() throws IOException, InterruptedException, ScenarioException {
        scenario = new Scenario().load(config.getScenarioFile());

        scenario.getDefines()
                .setStringAsConstant(Defines.HOST, config.getHost())
                .setIntegerAsConstant(Defines.PORT, config.getPort())
                .setStringAsConstant(Defines.CASINO_NAME, config.getCasinoName())
                .setStringAsConstant(Defines.USER_NAME, config.getUserName()  )
                .setStringAsConstant(Defines.USER_PASSWORD, config.getUserPassword())
                .setStringAsConstant(Defines.CLIENT_SKIN, config.getClientSkin())
                .setStringAsConstant(Defines.SCENARIO_FILE, config.getScenarioFile());

        scenarioProcessor = new  ScenarioProcessor(getEnvironmentSocket(), scenario, config);

        //  Run selected scenario processing
        (new Thread(scenarioProcessor)).start();
    }

    /**
     * Stop scenario execution
     *
     */
    public void stopProcessing() {
        scenarioProcessor.stopProcessing();
    }
}