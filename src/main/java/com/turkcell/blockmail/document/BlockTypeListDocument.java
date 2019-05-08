package com.turkcell.blockmail.document;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlockTypeListDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2236142896581434992L;
	
	@Id
	private BigInteger id;
	private String blockType;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getBlockType() {
		return blockType;
	}
	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}
	
	@Override
	public String toString() {
		return "BlockTypeListDocument [id=" + id + ", blockType=" + blockType + "]";
	}
	
	

}
