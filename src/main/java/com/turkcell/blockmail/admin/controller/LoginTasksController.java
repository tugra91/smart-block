package com.turkcell.blockmail.admin.controller;

import java.security.Principal;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetWaitApproveUsersOutput;
import com.turkcell.blockmail.model.UserInformationModel;

public interface LoginTasksController {
	
	public GetWaitApproveUsersOutput getWaitUsers(Principal principal);
	
	public BlockInfoOutput deleteUser(UserInformationModel user);
	
	public BlockInfoOutput applyUser(UserInformationModel user, Principal principal);
}
