package com.turkcell.blockmail.model;

public class ServiceRegisterTestOutput {
	
	private String message;
	private boolean testResult;
	private String response;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isTestResult() {
		return testResult;
	}
	public void setTestResult(boolean testResult) {
		this.testResult = testResult;
	}
	
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	@Override
	public String toString() {
		return "ServiceRegisterTestOutput [message=" + message + ", testResult=" + testResult + ", response=" + response
				+ "]";
	}
	
	
	
	
	

}
