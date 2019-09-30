package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;
import java.util.List;

public class GetIsOperationOutputModel extends GenericResultOutput implements Serializable {
    private static final long serialVersionUID = 8473127585874447356L;

    private List<HealthCheckServiceModel> isOperationList;

    public List<HealthCheckServiceModel> getIsOperationList() {
        return isOperationList;
    }

    public void setIsOperationList(List<HealthCheckServiceModel> isOperationList) {
        this.isOperationList = isOperationList;
    }

    @Override
    public String toString() {
        return "GetIsOperationOutputModel{" +
                "isOperationList=" + isOperationList +
                '}';
    }
}
