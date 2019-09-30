package com.turkcell.blockmail.save.dao;

import java.math.BigInteger;
import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockUpdateInformationModel;

public interface BlockSaveDao {
	
	public BlockInfoDocumentInput getFindByOneBlock(ObjectId id);
	
	public void saveBlock(BlockInfoDocumentInput input);
	
	public void deleteBlock(ObjectId id) throws Exception;
	
	public BlockInfoDocumentInput getFindBlockByCreateDate(long createDate);
	
	public BlockInfoDocumentInput updateBlock(BigInteger id, BlockUpdateInformationModel input, boolean status);
	
	public BlockInfoDocumentInput updateBlockTemp(ObjectId id, long endDate, String endBlockUser);
	
	public List<BlockInfoDocumentInput> getActiveBlockList(String segment);

	List<BlockInfoDocumentInput> getFindByServiceId(ObjectId id);

}
