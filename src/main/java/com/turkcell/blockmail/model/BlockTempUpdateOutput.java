package com.turkcell.blockmail.model;

import java.io.Serializable;

import org.bson.Document;

public class BlockTempUpdateOutput implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2425629052357701946L;
	
	private Document blockDetail;

	public Document getBlockDetail() {
		return blockDetail;
	}

	public void setBlockDetail(Document blockDetail) {
		this.blockDetail = blockDetail;
	}

	@Override
	public String toString() {
		return "BlockTempUpdateOutput [blockDetail=" + blockDetail + "]";
	}
	
	

}
