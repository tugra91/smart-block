package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import com.turkcell.blockmail.document.BlockSystemListDocument;

public class BlockSystemListOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6658001640502255033L;
	
	private List<BlockSystemListDocument> systemList;

	public List<BlockSystemListDocument> getSystemList() {
		return systemList;
	}

	public void setSystemList(List<BlockSystemListDocument> systemList) {
		this.systemList = systemList;
	}

	@Override
	public String toString() {
		return "BlockSystemListOutput [systemList=" + systemList + "]";
	}
	
	


}
