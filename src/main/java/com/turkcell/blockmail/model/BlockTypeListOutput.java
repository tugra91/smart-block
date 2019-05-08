package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import com.turkcell.blockmail.document.BlockTypeListDocument;

public class BlockTypeListOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1764486161149454351L;
	
	private List<BlockTypeListDocument> typeList;

	public List<BlockTypeListDocument> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<BlockTypeListDocument> typeList) {
		this.typeList = typeList;
	}

	@Override
	public String toString() {
		return "BlockTypeListOutput [typeList=" + typeList + "]";
	}
	
	

}
