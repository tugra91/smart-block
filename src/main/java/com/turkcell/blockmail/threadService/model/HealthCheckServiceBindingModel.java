package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;
import java.util.List;

public class HealthCheckServiceBindingModel extends GenericResultOutput  implements Serializable {

    private static final long serialVersionUID = 2236884819508135293L;
    private List<HealthCheckServiceBinding> bindingList;

    public List<HealthCheckServiceBinding> getBindingList() {
        return bindingList;
    }

    public void setBindingList(List<HealthCheckServiceBinding> bindingList) {
        this.bindingList = bindingList;
    }

    @Override
    public String toString() {
        return "HealthCheckServiceBindingModel{" +
                "bindingList=" + bindingList +
                '}';
    }
}
