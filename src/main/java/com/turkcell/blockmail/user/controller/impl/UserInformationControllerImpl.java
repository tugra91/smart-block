package com.turkcell.blockmail.user.controller.impl;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetBlockListForUserOutput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.user.controller.UserInformationController;
import com.turkcell.blockmail.user.service.UserInformationService;

@RestController
public class UserInformationControllerImpl implements UserInformationController {
	
	@Autowired
	private UserInformationService userInformationService;

	@Override
	@RequestMapping(value = "/user/getmyblocks")
	public GetBlockListForUserOutput getUserBlockList(Principal principal) {
		return userInformationService.getUserBlockList(principal.getName());
	}


	
	
}
