package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockInfoOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2936030858621202997L;
	
	private String result;
	private boolean status;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "BlockInfoOutput [result=" + result + ", status=" + status + "]";
	}
	
	

}
