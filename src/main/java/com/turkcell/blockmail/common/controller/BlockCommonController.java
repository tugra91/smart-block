package com.turkcell.blockmail.common.controller;

import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.model.*;
import com.turkcell.blockmail.threadService.model.GenericResultOutput;

public interface BlockCommonController {
	
	public BlockSystemListOutput getBlockSystemList();
	
	public BlockTypeListOutput getBlockType();

	public BlockInfoOutput saveBlockSystem(BlockSystemListDocument input);
	
	public BlockInfoOutput saveBlockType(BlockTypeListDocument input);
	
	public BlockHoursOutput getBlockHours(BlockOnlyIdInput input);
	
	public void trySendEndDayReportMail();

	GenericResultOutput saveBlockSystemParameter(BlockSystemParameterInput input);

	
}
