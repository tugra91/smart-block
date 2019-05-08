package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

public class ServiceRegisterDetailOutput implements Serializable {

	private static final long serialVersionUID = -3126347288091025539L;
	
	private String message;
	private boolean success;
	private List<String> operationList;
	private String serviceName;
	private String portName;
	private String bindingProtocol;
	private String targetNamespace;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<String> getOperationList() {
		return operationList;
	}
	public void setOperationList(List<String> operationList) {
		this.operationList = operationList;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getBindingProtocol() {
		return bindingProtocol;
	}
	public void setBindingProtocol(String bindingProtocol) {
		this.bindingProtocol = bindingProtocol;
	}
	public String getTargetNamespace() {
		return targetNamespace;
	}
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}
	
	
	@Override
	public String toString() {
		return "ServiceRegisterDetailOutput [operationList=" + operationList + ", serviceName=" + serviceName
				+ ", portName=" + portName + ", bindingProtocol=" + bindingProtocol + ", targetNamespace="
				+ targetNamespace + "]";
	}
	
	
	

}
