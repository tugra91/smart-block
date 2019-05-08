package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;

public class BlockSearchOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2897660973103782910L;
	
	private List<BlockInfoDocumentInput> searchOutputList;

	public List<BlockInfoDocumentInput> getSearchOutputList() {
		return searchOutputList;
	}

	public void setSearchOutputList(List<BlockInfoDocumentInput> searchOutputList) {
		this.searchOutputList = searchOutputList;
	}

	@Override
	public String toString() {
		return "BlockSearchOutput [searchOutputList=" + searchOutputList + "]";
	}
	
	

}
