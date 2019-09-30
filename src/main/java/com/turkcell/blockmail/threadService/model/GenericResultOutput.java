package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;

public class GenericResultOutput implements Serializable {

    private static final long serialVersionUID = -6752379520380395560L;

    private boolean result;
    private Long errorCode;
    private String message;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GenericResultOutput{" +
                "result=" + result +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }
}
