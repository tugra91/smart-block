package com.turkcell.blockmail.user.controller.impl;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetAccessTokenInput;
import com.turkcell.blockmail.model.GetAccessTokenOutput;
import com.turkcell.blockmail.model.RTEGetLoginInformationInput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.user.controller.LoginProcessController;
import com.turkcell.blockmail.user.service.LoginProcessService;

@RestController
public class LoginProcessControllerImpl implements LoginProcessController {
	
	@Autowired
	private LoginProcessService loginPageService;

	@Override
	@RequestMapping(value = "/getAccessToken")
	public GetAccessTokenOutput getAccesToken(@RequestBody GetAccessTokenInput input) {
		return loginPageService.getAccessToken(input.getUsername(), input.getPassword());
	}

	@Override
	@RequestMapping(value = "/saveUser")
	public BlockInfoOutput saveUser(@RequestBody UserInformationModel userInformation) {
		return loginPageService.saveUser(userInformation);
	}
	
	@Override
	@RequestMapping(value = "/updateUser")
	public UserInformationModel updateUser(@RequestBody UserInformationModel userInformation) {
		return loginPageService.updateUser(userInformation);
	}

	@Override
	@RequestMapping(value = "/user/getuserinformation")
	public UserInformationModel getUserInformation(Principal principal) {
		return loginPageService.getUserInformation(principal.getName());
	}

	@Override
	@RequestMapping(value ="/user/checkUserExpire")
	public String checkUserExpire() {
		return loginPageService.checkUserExpire();
	}

	@Override
	@RequestMapping(value ="/getAccessTokenForRT")
	public GetAccessTokenOutput getAccessTokenViaRT(@RequestBody RTEGetLoginInformationInput input) {
		return loginPageService.getAccessTokenViaRefreshToken(input.getCode());
	}


}
