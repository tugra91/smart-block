package com.turkcell.blockmail.model;

import java.io.Serializable;

public class RTEGetLoginInformationInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4971446014009001880L;
	
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "RTEGetLoginInformationInput [code=" + code + "]";
	}
	
	

}
