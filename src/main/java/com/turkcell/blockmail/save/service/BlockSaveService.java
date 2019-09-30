package com.turkcell.blockmail.save.service;

import java.math.BigInteger;
import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockTempUpdateOutput;
import com.turkcell.blockmail.model.BlockUpdateInformationModel;

public interface BlockSaveService {
	
	
	public BlockInfoDocumentInput getBlockById(ObjectId id);
	
	public BlockInfoOutput saveBlock(BlockInfoDocumentInput input);
	
	public BlockInfoOutput deleteBlock(ObjectId id, String deleteBlockUser);
	
	public void updateBlockMail(BigInteger id, BlockUpdateInformationModel updateInformationModel);
	
	public BlockTempUpdateOutput updateBlockMailTemp(ObjectId id, long endDate, String endBlockUser);
	
	public List<BlockInfoDocumentInput> getActiveBlockList(String segment);

	List<BlockInfoDocumentInput> getBlockByServiceId(ObjectId serviceId);
	
}
