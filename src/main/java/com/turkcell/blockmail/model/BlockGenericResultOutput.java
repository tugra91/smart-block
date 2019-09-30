package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockGenericResultOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 370422267663023946L;
	
	private boolean success;
	private String errorLog;
	private String result;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorLog() {
		return errorLog;
	}
	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "BlockGenericResultOutput [success=" + success + ", errorLog=" + errorLog + ", result=" + result + "]";
	}
	
	

}
