package com.turkcell.blockmail.user.controller;

import java.security.Principal;

import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetBlockListForUserOutput;

public interface UserInformationController {
	
	public GetBlockListForUserOutput getUserBlockList(Principal principal);
	

}
