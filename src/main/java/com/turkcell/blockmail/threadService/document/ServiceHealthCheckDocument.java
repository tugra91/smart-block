package com.turkcell.blockmail.threadService.document;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document
public class ServiceHealthCheckDocument implements Serializable {


    private static final long serialVersionUID = 4457044901975255111L;


    @Id
    private ObjectId id;
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
    private boolean isReqResCheck;
    private Long uptime;
    private Long slaTime;
    private String env;
    private Long runningTime;
    private Long closeTime;
    private Long downDate;
    private boolean status;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public boolean isReqResCheck() {
        return isReqResCheck;
    }

    public void setReqResCheck(boolean reqResCheck) {
        isReqResCheck = reqResCheck;
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

    public Long getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(Long runningTime) {
        this.runningTime = runningTime;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public Long getDownDate() {
        return downDate;
    }

    public void setDownDate(Long downDate) {
        this.downDate = downDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ServiceHealthCheckDocument{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
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
                ", isReqResCheck=" + isReqResCheck +
                ", uptime=" + uptime +
                ", slaTime=" + slaTime +
                ", env='" + env + '\'' +
                ", runningTime=" + runningTime +
                ", closeTime=" + closeTime +
                ", downDate=" + downDate +
                ", status=" + status +
                '}';
    }
}