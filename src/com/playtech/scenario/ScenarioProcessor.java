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

import com.jayway.jsonpath.JsonPath;
import com.playtech.environment.EnvironmentConfig;
import com.playtech.common.Defines;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;

/**
 * ScenarioProcessor class
 *
 */
public class ScenarioProcessor implements Runnable  {
    private static final Logger log = Logger.getLogger(ScenarioProcessor.class.getName());

    private Socket socket;
    private Scenario scenario;
    EnvironmentConfig config;
    private boolean isStopped;

    private ProcessingState  state;

    private Object lock1;
    private Object lock2;

    /**
     * Constructor
     *
     * @param socket
     * @param scenario
     * @param config
     */
    public ScenarioProcessor(Socket socket, Scenario scenario, EnvironmentConfig config) {
        this.socket   = socket;
        this.scenario = scenario;
        this.config = config;

        state = ProcessingState.READY;

        lock1 = new Object();
        lock2 = new Object();
    }

    public ProcessingState getState() {
        synchronized (lock1){
            return state;
        }
    }

    private void setState(ProcessingState state) {
        synchronized (lock1){
            this.state = state;
        }
    }

    public synchronized void stopProcessing() {
        synchronized (lock2){
            isStopped = true;
        }
    }

    private boolean isStopped() {
        synchronized (lock2){
            return isStopped;
        }
    }

    public void run() {
        int stepNumber = 1; // scenario step counting for log message

        try {
            log.info(String.format("Running scenario '%s'.", scenario.getFileName()));

            setState(ProcessingState.RUNNING);

            prepareDefines();

            for (ScenarioItem item: scenario.getItems()) {
                if (isStopped()) {
                    setState(ProcessingState.TERMINATED);

                    log.info(String.format("Scenario '%s' terminated at step %d.",
                                           scenario.getFileName(),
                                           stepNumber));

                    return;
                }

                // Execute one scenario step
                log.info(String.format("Executing scenario step [ %d/%d ]",
                                        stepNumber,
                                        scenario.getItems().size()));

                processScenarioItem(item); // << magic is here

                stepNumber++;
            }

            setState(ProcessingState.COMPLETE);

            log.info(String.format("Completed scenario '%s'.", scenario.getFileName()));
        }
        catch (Exception e) {
            String errorHeader = "\n\n--[ Error ]--------------------------------\n\n";
            String errorTrace  = "\n--[ Trace ]--------------------------------\n";
            log.severe(String.format("%sScenario '%s' failed at step [%d] with error:\n\n%s%s",
                                      errorHeader,
                                      config.getScenarioFile(),
                                      stepNumber,
                                      e.getMessage(),errorTrace));

            e.printStackTrace(); //TODO: print stack trace with logger
        }
    }

    private void prepareDefines(){
        scenario.getDefines().clear();

        scenario.getDefines()
                .setString (Defines.HOST         , config.getHost())
                .setInteger(Defines.PORT         , config.getPort())
                .setString (Defines.CASINO_NAME  , config.getCasinoName())
                .setString (Defines.USER_NAME    , config.getUserName()  )
                .setString (Defines.USER_PASSWORD, config.getUserPassword())
                .setString (Defines.CLIENT_SKIN  , config.getClientSkin());
    }

    /**
     * Send request data to socket
     *
     * @param socket
     * @param data
     * @throws IOException
     */
    private void sendData (Socket socket, String data) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        printWriter.println(data);
        printWriter.flush();
    }

    /**
     * Read response from the socket
     *
     * @param socket
     * @return
     * @throws IOException
     */
    private String readData (Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String data =  bufferedReader.readLine();

        return data;
    }

    private int getResponseID(String response) throws ScenarioException {
        int result;

        try {
            result = JsonPath.parse(response).read("$.ID");
        }
        catch(Exception e) {
            throw new ScenarioException("Cannot determine response message ID", e);
        }

        return result;
    }

    /**
     *  Check string for null or empty value
     *
     * @param value
     * @return boolean
     */
    private boolean isNullOrEmpty(String value) {
        return (value == null || value.isEmpty());
    }

    private boolean isSkippedByMessageID(int messageId) {
        for (Integer id :scenario.getSkippedIDs()) {
            if (id == messageId) {
                log.info("Skip message id detected: " + messageId);
                return true;
            }
        }

        return false;
    }

    private boolean isSkippedByCondition(ScenarioItem scenarioItem, String jsonString) {
        for(PathValue pathValue : scenarioItem.getSkipConditions()) {
            String skipValue = JsonPath.parse(jsonString).read(pathValue.getPath());

            if (pathValue.getValue().equals(skipValue)) {
                log.info("Skip message condition detected: " + pathValue.getPath() + "=" + skipValue);
                return true;
            }
        }

        return false;
    }


    /**
     * @param scenarioItem
     * @throws IOException
     * @throws ScenarioException
     */
    private void processScenarioItem(ScenarioItem scenarioItem)
            throws IOException, ScenarioException {

        // ------- Send request -------
        if(!isNullOrEmpty(scenarioItem.getRequestBody())) {
            // replace placeholders if any
            Map<String, Object> defines = scenario.getDefines().export();
            StrSubstitutor substitutor = new StrSubstitutor(defines, "${", "}");

            String body = substitutor.replace(scenarioItem.getRequestBody());

            log.info("[ Message sent ] " + body);

            sendData (socket, body);
        }

        // ------- Receive response -------
        String response;
        int responseId;
        boolean isSkipped;

        do {
            response   = readData(socket);
            responseId = getResponseID(response);
            isSkipped  = isSkippedByMessageID(responseId);

            log.info("[ Message received ] " + response);
            log.info("[ Response ID ] " + responseId);

            if (isSkipped) {
                log.info(String.format("Skipped [ %d ]", responseId));
            }
        } while(isSkipped);

        // ------- Response handling -------

        // Check message skip conditions
        if (isSkippedByCondition(scenarioItem, response)) {
            log.info("Skipped by message skip condition");
        }

        // Check response ID
        if (scenarioItem.getResponseId() > 0) {
            if (responseId != scenarioItem.getResponseId()) {
                throw new ScenarioException("Invalid response ID. Expected %d, but received %d",
                                             scenarioItem.getResponseId(),
                                             responseId);
            }
        }

        // Process checked values
        for(PathValue item : scenarioItem.getCheckedValues()) {
            String jsonValue = JsonPath.parse(response).read(item.getPath());

            if (!item.getValue().equals(jsonValue)) {
                throw new ScenarioException("Invalid response value.\nExpected : %s\nReceived : %s\nPath     : %s\n",
                                             item.getValue(), jsonValue, item.getPath());
            }
        }

        // Process imported values
        for(ImportedValue item : scenarioItem.getImportedValues()) {
            String jsonValue = JsonPath.parse(response).read(item.getPath());

            if(jsonValue != null) {
                scenario.getDefines().setString(item.getKey(), jsonValue);
            }
        }
    }
}
