package com.turkcell.blockmail.admin.service;

import java.util.List;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.UserInformationModel;

public interface LoginTasksService {
	
	public List<UserInformationModel> getWaitUsers(String username);
	
	public BlockInfoOutput deleteUser(UserInformationModel id);
	
	public BlockInfoOutput applyuser(UserInformationModel id, String applyUserName);

}
