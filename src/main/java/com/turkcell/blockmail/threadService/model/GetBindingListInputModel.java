package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;

public class GetBindingListInputModel extends GenericResultOutput implements  Serializable {


    private static final long serialVersionUID = -4420567963976247455L;
    private String serviceWsdlURL;
    private boolean hasAuth;
    private String userName;
    private String password;

    public String getServiceWsdlURL() {
        return serviceWsdlURL;
    }

    public void setServiceWsdlURL(String serviceWsdlURL) {
        this.serviceWsdlURL = serviceWsdlURL;
    }

    public boolean isHasAuth() {
        return hasAuth;
    }

    public void setHasAuth(boolean hasAuth) {
        this.hasAuth = hasAuth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "GetBindingListInputModel{" +
                "serviceWsdlURL='" + serviceWsdlURL + '\'' +
                ", hasAuth=" + hasAuth +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
