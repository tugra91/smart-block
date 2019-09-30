package com.turkcell.blockmail.util.mail.controller;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.model.BlockGenericResultOutput;
import com.turkcell.blockmail.model.BlockSaveMailInformationInput;

public interface BlockSaveMailController {
	
	public BlockGenericResultOutput saveMailInformaton(BlockSaveMailInformationInput input);
	
	public BlockGenericResultOutput deleteMailInformation(ObjectId id);
	
	

}
