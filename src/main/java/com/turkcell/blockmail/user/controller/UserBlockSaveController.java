package com.turkcell.blockmail.user.controller;

import java.security.Principal;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockOnlyIdInput;
import com.turkcell.blockmail.model.BlockTempUpdateInput;
import com.turkcell.blockmail.model.BlockTempUpdateOutput;

public interface UserBlockSaveController {
	
	public BlockInfoOutput saveBlock(BlockInfoDocumentInput input);
	public BlockInfoOutput deleteBlock(BlockOnlyIdInput input, Principal principal);
	public BlockTempUpdateOutput updateBlockTemp(BlockTempUpdateInput updateModel, Principal principal);
	
}
