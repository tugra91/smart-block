package com.turkcell.blockmail.document;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class AccessTokenInformationDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1737781887052734092L;
	
	@Id
	private ObjectId id;
	private String accessToken;
	private String refreshToken;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
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
	@Override
	public String toString() {
		return "AccessTokenInformationDocument [id=" + id + ", accessToken=" + accessToken + ", refreshToken="
				+ refreshToken + "]";
	}
	
	
}
