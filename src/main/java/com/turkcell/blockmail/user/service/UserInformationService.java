package com.turkcell.blockmail.user.service;

import com.turkcell.blockmail.model.GetBlockListForUserOutput;

public interface UserInformationService {
	
	public GetBlockListForUserOutput getUserBlockList(String username);
}
