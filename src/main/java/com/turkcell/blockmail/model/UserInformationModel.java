package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class UserInformationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6881870145686966693L;
	
	@Id
	private ObjectId id;
	private String username;
	private String password;
	private List<String> roles;
	private String email;
	private String teamId;
	private String name;
	private String surname;
	private String appliedUser;
	private long createdDate;
	private boolean isApproved;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getAppliedUser() {
		return appliedUser;
	}
	public void setAppliedUser(String appliedUser) {
		this.appliedUser = appliedUser;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public boolean isApproved() {
		return isApproved;
	}
	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
	
	@Override
	public String toString() {
		return "UserInformationModel [id=" + id + ", username=" + username + ", password=" + password + ", roles="
				+ roles + ", email=" + email + ", teamId=" + teamId + ", name=" + name + ", surname=" + surname
				+ ", appliedUser=" + appliedUser + ", createdDate=" + createdDate + ", isApproved=" + isApproved + "]";
	}
	
	
	
	
	

}
