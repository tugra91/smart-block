package com.turkcell.blockmail.user.controller;

import java.security.Principal;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetAccessTokenInput;
import com.turkcell.blockmail.model.GetAccessTokenOutput;
import com.turkcell.blockmail.model.RTEGetLoginInformationInput;
import com.turkcell.blockmail.model.UserInformationModel;

public interface LoginProcessController {
	
	public GetAccessTokenOutput getAccesToken(GetAccessTokenInput input);
	
	public BlockInfoOutput saveUser(UserInformationModel userInformation);
	
	public UserInformationModel updateUser(UserInformationModel userInformation);
	
	public UserInformationModel getUserInformation(Principal principal);
	
	public String checkUserExpire();
	
	public GetAccessTokenOutput getAccessTokenViaRT(RTEGetLoginInformationInput input);
	
}
