package com.turkcell.blockmail.save.controller.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockOnlyIdInput;
import com.turkcell.blockmail.model.BlockUpdateInformationModel;
import com.turkcell.blockmail.save.controller.BlockSaveController;
import com.turkcell.blockmail.save.service.BlockSaveService;

@RestController
public class BlockSaveControllerImpl implements BlockSaveController {
	
	
	@Autowired
	private BlockSaveService blockSaveService;

	
	@Override
	@RequestMapping(value = "/getBlockById")
	public BlockInfoDocumentInput getBlockInfoById(@RequestBody BlockOnlyIdInput input) {
		return blockSaveService.getBlockById(input.getId());
	}

	
	@Override
	@RequestMapping(value = "/updateBlockService")
	public BlockInfoOutput updateBlockMail(@RequestParam("id") BigInteger id, @RequestBody BlockUpdateInformationModel input) {
		
		//TODO Daha sonra buralar değerlenecek.
		BlockInfoOutput output = new BlockInfoOutput();
		blockSaveService.updateBlockMail(id, input);
		return output;
	}


}
