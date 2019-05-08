package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import org.bson.Document;

public class BlockInfoAsTimeOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7140904524362003918L;
	
	
	private List<Document> blockDetail;


	public List<Document> getBlockDetail() {
		return blockDetail;
	}


	public void setBlockDetail(List<Document> blockDetail) {
		this.blockDetail = blockDetail;
	}


	@Override
	public String toString() {
		return "BlockInfoAsTimeOutput [blockDetail=" + blockDetail + "]";
	}
	
	
	

}
