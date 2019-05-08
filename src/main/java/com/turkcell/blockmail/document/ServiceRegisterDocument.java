package com.turkcell.blockmail.document;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ServiceRegisterDocument implements Serializable{
	
	private static final long serialVersionUID = 1247584970050012419L;
	
	
	@Id
	private BigInteger id;
	private String request;
	private String response;
	private String endpointAddress;
	private String serviceName;
	private String operationName;
	private String portName;
	private String bindProtocol;
	private String wsdlAddress;
	private String targetNameSpace;
	private boolean isAuth;
	private String userName;
	private String password;
	
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
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
	public String getEndpointAddress() {
		return endpointAddress;
	}
	public void setEndpointAddress(String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getBindProtocol() {
		return bindProtocol;
	}
	public void setBindProtocol(String bindProtocol) {
		this.bindProtocol = bindProtocol;
	}
	public String getWsdlAddress() {
		return wsdlAddress;
	}
	public void setWsdlAddress(String wsdlAddress) {
		this.wsdlAddress = wsdlAddress;
	}
	public String getTargetNameSpace() {
		return targetNameSpace;
	}
	public void setTargetNameSpace(String targetNameSpace) {
		this.targetNameSpace = targetNameSpace;
	}
	public boolean isAuth() {
		return isAuth;
	}
	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	@Override
	public String toString() {
		return "ServiceRegisterDocument [id=" + id + ", request=" + request + ", response=" + response
				+ ", endpointAddress=" + endpointAddress + ", serviceName=" + serviceName + ", portName=" + portName
				+ ", bindProtocol=" + bindProtocol + ", wsdlAddress=" + wsdlAddress + ", targetNameSpace="
				+ targetNameSpace + ", isAuth=" + isAuth + ", userName=" + userName + ", password=" + password + "]";
	}
	
	
	
	
	

}
