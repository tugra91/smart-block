package com.turkcell.blockmail.common.service;

import com.turkcell.blockmail.document.BlockSystemParameterDocument;
import com.turkcell.blockmail.threadService.model.GenericResultOutput;
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

	GenericResultOutput saveBlockSystemParameter(String name, String value);

	String getBlockSystemParameter(String name);

	
}
