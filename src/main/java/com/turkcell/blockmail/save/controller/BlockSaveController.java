package com.turkcell.blockmail.save.controller;

import java.math.BigInteger;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockOnlyIdInput;
import com.turkcell.blockmail.model.BlockUpdateInformationModel;

public interface BlockSaveController {
	
	public BlockInfoDocumentInput getBlockInfoById(BlockOnlyIdInput input);

	public BlockInfoOutput updateBlockMail(BigInteger id, BlockUpdateInformationModel input);
	

}
