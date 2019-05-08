package com.turkcell.blockmail.admin.controller.impl;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.admin.controller.LoginTasksController;
import com.turkcell.blockmail.admin.service.LoginTasksService;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetWaitApproveUsersOutput;
import com.turkcell.blockmail.model.UserInformationModel;

@RestController
@RequestMapping(value = "/admin")
public class LoginTasksControllerImpl implements LoginTasksController {
	
	@Autowired
	private LoginTasksService loginTasksService;


	@Override
	@RequestMapping(value = "/getWaitUsers")
	public GetWaitApproveUsersOutput getWaitUsers(Principal principal) {
		GetWaitApproveUsersOutput output = new GetWaitApproveUsersOutput();
		output.setApproveUserList(loginTasksService.getWaitUsers(principal.getName()));
		return output;
	}


	@Override
	@RequestMapping(value = "/deleteUser")
	public BlockInfoOutput deleteUser(@RequestBody UserInformationModel user) {
		return loginTasksService.deleteUser(user);
	}


	@Override
	@RequestMapping(value = "/applyUser")
	public BlockInfoOutput applyUser(@RequestBody UserInformationModel user, Principal principal) {
		return loginTasksService.applyuser(user, principal.getName());
	}

}
