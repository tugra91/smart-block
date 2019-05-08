package com.turkcell.blockmail.user.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;

public interface UserInformationDao {
	
	public List<BlockInfoDocumentInput> getUserBlockList(ObjectId id);

}
