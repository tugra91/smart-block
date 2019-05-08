package com.turkcell.blockmail.model;

import java.io.Serializable;

public class ServiceRegisterDetailInput implements Serializable {

	private static final long serialVersionUID = 3844415169016003432L;
	
	private String wsdlURL; 
	private boolean isAuth; 
	private String username; 
	private String password;
	
	public String getWsdlURL() {
		return wsdlURL;
	}
	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}
	public boolean isAuth() {
		return isAuth;
	}
	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "ServiceRegisterDetailInput [wsdlURL=" + wsdlURL + ", isAuth=" + isAuth + ", username=" + username
				+ ", password=" + password + "]";
	}
	

}
