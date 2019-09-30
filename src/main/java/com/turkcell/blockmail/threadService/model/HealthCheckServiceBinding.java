package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;
import java.util.List;

public class HealthCheckServiceBinding implements Serializable {

    private static final long serialVersionUID = 6799214423141029282L;

    private String bindingName;
    private String endpointAddres;
    private List<HealthCheckServiceOperation> operationList;

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public String getEndpointAddres() {
        return endpointAddres;
    }

    public void setEndpointAddres(String endpointAddres) {
        this.endpointAddres = endpointAddres;
    }

    public List<HealthCheckServiceOperation> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<HealthCheckServiceOperation> operationList) {
        this.operationList = operationList;
    }

    @Override
    public String toString() {
        return "HealthCheckServiceBinding{" +
                "bindingName='" + bindingName + '\'' +
                ", endpointAddres='" + endpointAddres + '\'' +
                ", operationList=" + operationList +
                '}';
    }
}
