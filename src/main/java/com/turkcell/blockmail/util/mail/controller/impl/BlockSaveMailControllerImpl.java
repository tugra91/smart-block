package com.turkcell.blockmail.util.mail.controller.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.model.BlockGenericResultOutput;
import com.turkcell.blockmail.model.BlockSaveMailInformationInput;
import com.turkcell.blockmail.util.mail.controller.BlockSaveMailController;
import com.turkcell.blockmail.util.mail.service.BlockSaveMailService;

@RestController
public class BlockSaveMailControllerImpl implements BlockSaveMailController {
	
	@Autowired
	private BlockSaveMailService blockSaveMailService;

	@Override
	@RequestMapping(value = "/saveMailInformation", method = {RequestMethod.POST})
	public BlockGenericResultOutput saveMailInformaton(@RequestBody BlockSaveMailInformationInput input) {
		return blockSaveMailService.saveMailInformatin(input);
	}

	@Override
	@RequestMapping(value = "/deleteMailInformation")
	public BlockGenericResultOutput deleteMailInformation(@RequestParam("id") ObjectId id) {
		return blockSaveMailService.deleteMailInformation(id);
	}
	
	
	
	 
}
