package com.turkcell.blockmail.document;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlockSystemListDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6296123319629434270L;
	
	@Id
	private BigInteger id;
	private String systemName;
	
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	@Override
	public String toString() {
		return "BlockSystemList [id=" + id + ", systemName=" + systemName + "]";
	}
	
	

}
