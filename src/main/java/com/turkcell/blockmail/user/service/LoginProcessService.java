package com.turkcell.blockmail.user.service;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetAccessTokenOutput;
import com.turkcell.blockmail.model.UserInformationModel;

public interface LoginProcessService {
	
	public GetAccessTokenOutput getAccessToken(String username, String password);
	
	public BlockInfoOutput saveUser(UserInformationModel userInformation);
	
	public UserInformationModel updateUser(UserInformationModel userInformation);

	public UserInformationModel getUserInformation(String username);
	
	public UserInformationModel getUserInformationWithId(ObjectId id);
	
	public String checkUserExpire();
	
	public GetAccessTokenOutput getAccessTokenViaRefreshToken(String accessToken);
	
}
