package com.turkcell.blockmail.common.controller;

import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.model.BlockHoursOutput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockOnlyIdInput;
import com.turkcell.blockmail.model.BlockSystemListOutput;
import com.turkcell.blockmail.model.BlockTypeListOutput;

public interface BlockCommonController {
	
	public BlockSystemListOutput getBlockSystemList();
	
	public BlockTypeListOutput getBlockType();

	public BlockInfoOutput saveBlockSystem(BlockSystemListDocument input);
	
	public BlockInfoOutput saveBlockType(BlockTypeListDocument input);
	
	public BlockHoursOutput getBlockHours(BlockOnlyIdInput input);
	
	public void trySendEndDayReportMail();
	
}
