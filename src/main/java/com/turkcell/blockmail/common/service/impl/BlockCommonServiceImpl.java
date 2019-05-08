package com.turkcell.blockmail.common.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.blockmail.common.dao.BlockCommonDao;
import com.turkcell.blockmail.common.service.BlockCommonService;
import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.model.BlockHoursOutput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockSystemListOutput;
import com.turkcell.blockmail.model.BlockTypeListOutput;

@Service
public class BlockCommonServiceImpl implements BlockCommonService {
	
	@Autowired
	private BlockCommonDao blockCommonDao;

	@Override
	public BlockSystemListOutput getSystemList() {
		BlockSystemListOutput output = new BlockSystemListOutput();
		output.setSystemList(blockCommonDao.getBlockSystemList());
		return output;
	}

	@Override
	public BlockTypeListOutput getBlockType() {
		BlockTypeListOutput output = new BlockTypeListOutput();
		output.setTypeList(blockCommonDao.getBlockTypeList());
		return output;
	}

	@Override
	public BlockInfoOutput saveSystemList(BlockSystemListDocument input) {
		BlockInfoOutput output = new BlockInfoOutput();
		try {
			blockCommonDao.saveBlockSystem(input);
			output.setResult("Success");
			output.setStatus(true);
		}catch (Exception e) {
			output.setResult(e.getMessage());
			output.setStatus(false);
		}
		return output;
	}

	@Override
	public BlockInfoOutput saveBlockType(BlockTypeListDocument input) {
		BlockInfoOutput output = new BlockInfoOutput();
		try {
			blockCommonDao.saveBlockType(input);
			output.setResult("Success");
			output.setStatus(true);
		}catch (Exception e) {
			output.setResult(e.getMessage());
			output.setStatus(false);
		}
		return output;
	}

	@Override
	public BlockHoursOutput getBlockHours(ObjectId id) {
		BlockHoursOutput output = new BlockHoursOutput();
		output.setBlockHours(blockCommonDao.getBlockHours(id));
		return output;
	}

	

}
