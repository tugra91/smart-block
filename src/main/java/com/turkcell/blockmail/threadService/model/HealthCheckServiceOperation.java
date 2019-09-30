package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;

public class HealthCheckServiceOperation implements Serializable {

    private static final long serialVersionUID = -2512129187145478143L;

    private String operationName;
    private String soapAction;
    private String exampleRequest;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getExampleRequest() {
        return exampleRequest;
    }

    public void setExampleRequest(String exampleRequest) {
        this.exampleRequest = exampleRequest;
    }

    @Override
    public String toString() {
        return "HealthCheckServiceOperation{" +
                "operationName='" + operationName + '\'' +
                ", soapAction='" + soapAction + '\'' +
                ", exampleRequest='" + exampleRequest + '\'' +
                '}';
    }
}
