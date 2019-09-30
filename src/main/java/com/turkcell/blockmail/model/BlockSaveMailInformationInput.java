package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

public class BlockSaveMailInformationInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3483993436149526912L;
	
	private String segment;
	private List<String> toList;
	private List<String> ccList;
	private String role;
	
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public List<String> getToList() {
		return toList;
	}
	public void setToList(List<String> toList) {
		this.toList = toList;
	}
	public List<String> getCcList() {
		return ccList;
	}
	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "BlockMailInformationInput [segment=" + segment + ", toList=" + toList + ", ccList=" + ccList + ", role="
				+ role + "]";
	}
	
	
	
	
}
