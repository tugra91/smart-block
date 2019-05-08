package com.turkcell.blockmail.admin.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.model.UserInformationModel;

public interface LoginTasksDao {
	
	public List<UserInformationModel> getWaitUsers();
	
	public void deleteUser(ObjectId id);
	
	public void applyUser(ObjectId id, String applyUserName);
}
