package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockSearchInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8873068427400009231L;
	
	private String searchText;
	private String blockSystem;
	private String affectSystem;
	private String affectEnvironment;
	private String blockType;
	private long startDate;
	private long endDate;
	
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public String getBlockSystem() {
		return blockSystem;
	}
	public void setBlockSystem(String blockSystem) {
		this.blockSystem = blockSystem;
	}
	public String getAffectSystem() {
		return affectSystem;
	}
	public void setAffectSystem(String affectSystem) {
		this.affectSystem = affectSystem;
	}
	public String getAffectEnvironment() {
		return affectEnvironment;
	}
	public void setAffectEnvironment(String affectEnvironment) {
		this.affectEnvironment = affectEnvironment;
	}
	public String getBlockType() {
		return blockType;
	}
	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String toString() {
		return "BlockSearchInput [searchText=" + searchText + ", blockSystem=" + blockSystem + ", affectSystem="
				+ affectSystem + ", envSystem=" + affectEnvironment + ", blockType=" + blockType + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}
	
	

}
