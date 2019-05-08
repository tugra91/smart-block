package com.turkcell.blockmail.model;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class BlockOnlyIdInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4408089034842774138L;
	
	private ObjectId id;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "BlockOnlyIdInput [id=" + id + "]";
	}
	

}
