package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.Map;

public class RTEGetLoginInformationOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7131701830713011289L;
	
	private String accessToken;
	private String refreshToken;
	private Map<String,Object> additionalInformation;
	
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
	public Map<String, Object> getAdditionalInformation() {
		return additionalInformation;
	}
	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
	@Override
	public String toString() {
		return "RTEGetLoginInformationOutput [accessToken=" + accessToken + ", refreshToken=" + refreshToken
				+ ", additionalInformation=" + additionalInformation + "]";
	}
	
	

}
