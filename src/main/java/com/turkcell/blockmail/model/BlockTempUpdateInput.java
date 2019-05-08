package com.turkcell.blockmail.model;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class BlockTempUpdateInput implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5228438142075299308L;
	private ObjectId id;
	private long endDate;
	
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	
	

}
