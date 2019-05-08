package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;

public class GetBlockListForUserOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8963847204699810919L;
	
	private List<BlockInfoDocumentInput> blockList;

	public List<BlockInfoDocumentInput> getBlockList() {
		return blockList;
	}

	public void setBlockList(List<BlockInfoDocumentInput> blockList) {
		this.blockList = blockList;
	}

	@Override
	public String toString() {
		return "GetBlockListForUserOutput [blockList=" + blockList + "]";
	}
	
	

}
