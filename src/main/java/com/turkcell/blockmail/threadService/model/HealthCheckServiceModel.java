package com.turkcell.blockmail.threadService.model;

import java.io.Serializable;
import java.util.List;

public class HealthCheckServiceModel extends GenericResultOutput implements Serializable {

    private static final long serialVersionUID = -6219581121457747229L;

    private String serviceName;
    private String serviceURL;
    private String serviceWsdlURL;
    private Long serviceType;
    private boolean hasAuth;
    private String userName;
    private String password;
    private String request;
    private String response;
    private String operationName;
    private String soapAction;
    private String bindingName;
    private String sourceSystem;
    private List<String> targetSystem;
    private String methodType;
    private String segment;
    private Long uptime;
    private Long slaTime;
    private String env;
    private Float uptimePercent;
    private boolean isReqResCheck;
    private boolean status;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String getServiceWsdlURL() {
        return serviceWsdlURL;
    }

    public void setServiceWsdlURL(String serviceWsdlURL) {
        this.serviceWsdlURL = serviceWsdlURL;
    }

    public Long getServiceType() {
        return serviceType;
    }

    public void setServiceType(Long serviceType) {
        this.serviceType = serviceType;
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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

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

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public List<String> getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(List<String> targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }

    public Long getSlaTime() {
        return slaTime;
    }

    public void setSlaTime(Long slaTime) {
        this.slaTime = slaTime;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Float getUptimePercent() {
        return uptimePercent;
    }

    public void setUptimePercent(Float uptimePercent) {
        this.uptimePercent = uptimePercent;
    }

    public boolean isReqResCheck() {
        return isReqResCheck;
    }

    public void setReqResCheck(boolean reqResCheck) {
        isReqResCheck = reqResCheck;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HealthCheckServiceModel{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceURL='" + serviceURL + '\'' +
                ", serviceWsdlURL='" + serviceWsdlURL + '\'' +
                ", serviceType=" + serviceType +
                ", hasAuth=" + hasAuth +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", request='" + request + '\'' +
                ", response='" + response + '\'' +
                ", operationName='" + operationName + '\'' +
                ", soapAction='" + soapAction + '\'' +
                ", bindingName='" + bindingName + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", targetSystem=" + targetSystem +
                ", methodType='" + methodType + '\'' +
                ", segment='" + segment + '\'' +
                ", uptime=" + uptime +
                ", slaTime=" + slaTime +
                ", env='" + env + '\'' +
                ", uptimePercent=" + uptimePercent +
                ", isReqResCheck=" + isReqResCheck +
                ", status=" + status +
                '}';
    }
}
