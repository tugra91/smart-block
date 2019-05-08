package com.turkcell.blockmail.user.controller.impl;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockOnlyIdInput;
import com.turkcell.blockmail.model.BlockTempUpdateInput;
import com.turkcell.blockmail.model.BlockTempUpdateOutput;
import com.turkcell.blockmail.save.service.BlockSaveService;
import com.turkcell.blockmail.user.controller.UserBlockSaveController;

@RestController
@RequestMapping(value = "/user")
public class UserBlockSaveControllerImpl implements UserBlockSaveController {
	
	@Autowired
	private BlockSaveService blockSaveService;

	@Override
	@RequestMapping(value = "/saveblock")
	public BlockInfoOutput saveBlock(@RequestBody BlockInfoDocumentInput input) {
		return blockSaveService.saveBlock(input);
	}

	@Override
	@RequestMapping(value = "/updateBlockTemp")
	public BlockTempUpdateOutput updateBlockTemp(@RequestBody BlockTempUpdateInput updateModel, Principal principal) {
		return blockSaveService.updateBlockMailTemp(updateModel.getId(), updateModel.getEndDate(), principal.getName());
	}

	@Override
	@RequestMapping(value = "/deleteBlock")
	public BlockInfoOutput deleteBlock(@RequestBody BlockOnlyIdInput input, Principal principal) {
		return blockSaveService.deleteBlock(input.getId(), principal.getName());
	}

}
