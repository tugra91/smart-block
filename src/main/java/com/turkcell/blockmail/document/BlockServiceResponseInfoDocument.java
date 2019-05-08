package com.turkcell.blockmail.document;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlockServiceResponseInfoDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1703322010701456838L;
	
	@Id
	private BigInteger id;
	private String serviceName;
	private double responseTime;
	private int timeoutTime;
	private long lastUpdate;
	private boolean status;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public double getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(double responseTime) {
		this.responseTime = responseTime;
	}
	public int getTimeoutTime() {
		return timeoutTime;
	}
	public void setTimeoutTime(int timeoutTime) {
		this.timeoutTime = timeoutTime;
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	

}
