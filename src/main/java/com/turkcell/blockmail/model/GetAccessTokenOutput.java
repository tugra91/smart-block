package com.turkcell.blockmail.model;

import java.io.Serializable;

public class GetAccessTokenOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1011775263548865761L;
	
	private String accessToken;
	private String refreshToken;
	private String result;
	private boolean status;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
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
	
	

}
