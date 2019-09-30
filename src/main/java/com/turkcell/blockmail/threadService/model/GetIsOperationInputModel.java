package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;

public class GetIsOperationInputModel implements Serializable {

    private static final long serialVersionUID = -2314336332598895102L;
    private String bindingName;
    private String wsdlURL;

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public String getWsdlURL() {
        return wsdlURL;
    }

    public void setWsdlURL(String wsdlURL) {
        this.wsdlURL = wsdlURL;
    }

    @Override
    public String toString() {
        return "GetIsOperationInputModel{" +
                "bindingName='" + bindingName + '\'' +
                ", wsdlURL='" + wsdlURL + '\'' +
                '}';
    }
}
