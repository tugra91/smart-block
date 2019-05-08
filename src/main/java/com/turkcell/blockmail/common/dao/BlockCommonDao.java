package com.turkcell.blockmail.common.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;

public interface BlockCommonDao {
	
	
	public List<BlockSystemListDocument> getBlockSystemList();
	
	public List<BlockTypeListDocument> getBlockTypeList();
	
	public void saveBlockSystem(BlockSystemListDocument input);
	
	public void saveBlockType(BlockTypeListDocument input);
	
	public double getBlockHours(ObjectId id);
	

}
