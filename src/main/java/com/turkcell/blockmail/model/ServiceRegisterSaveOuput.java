package com.turkcell.blockmail.model;

import java.io.Serializable;

public class ServiceRegisterSaveOuput implements Serializable {

	private static final long serialVersionUID = 2869157600364542521L;
	
	private String message;
	private boolean result;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return "ServiceRegisterSaveOuput [message=" + message + ", result=" + result + "]";
	}
	
	

}
