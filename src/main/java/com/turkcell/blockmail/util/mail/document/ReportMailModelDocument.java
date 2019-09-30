package com.turkcell.blockmail.util.mail.document;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ReportMailModelDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5868968529496182737L;
	
	@Id
	private ObjectId id;
	private String segment;
	private String role;
	private List<String> toList;
	private List<String> ccList;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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
	
	@Override
	public String toString() {
		return "ReportMailModelDocument [id=" + id + ", segment=" + segment + ", role=" + role + ", toList=" + toList
				+ ", ccList=" + ccList + "]";
	}
	
	
}
