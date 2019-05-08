package com.turkcell.blockmail.common.service;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.model.BlockHoursOutput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockSystemListOutput;
import com.turkcell.blockmail.model.BlockTypeListOutput;

public interface BlockCommonService {
	
	public BlockSystemListOutput getSystemList();
	
	public BlockTypeListOutput getBlockType();
	
	public BlockInfoOutput saveSystemList(BlockSystemListDocument input);
	
	public BlockInfoOutput saveBlockType(BlockTypeListDocument input);
	
	public BlockHoursOutput getBlockHours(ObjectId id);

	
}
