package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

public class GetWaitApproveUsersOutput implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2799869196758206118L;
	private List<UserInformationModel> approveUserList;
	
	public List<UserInformationModel> getApproveUserList() {
		return approveUserList;
	}

	public void setApproveUserList(List<UserInformationModel> approveUserList) {
		this.approveUserList = approveUserList;
	}

	@Override
	public String toString() {
		return "GetWaitApproveUsersOutput [approveUserList=" + approveUserList + "]";
	} 
	
	
}
