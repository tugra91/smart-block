package com.turkcell.blockmail.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.blockmail.model.GetBlockListForUserOutput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.user.dao.UserInformationDao;
import com.turkcell.blockmail.user.service.LoginProcessService;
import com.turkcell.blockmail.user.service.UserInformationService;

@Service
public class UserInformationServiceImpl implements UserInformationService {
	
	@Autowired
	private UserInformationDao userInformationDao;
	
	@Autowired
	private LoginProcessService loginPageService;
	
	
	@Override
	public GetBlockListForUserOutput getUserBlockList(String username) {
		GetBlockListForUserOutput output = new GetBlockListForUserOutput();
		UserInformationModel userInfo = loginPageService.getUserInformation(username);
		output.setBlockList(userInformationDao.getUserBlockList(userInfo.getId()));
		return output;
	}

}
