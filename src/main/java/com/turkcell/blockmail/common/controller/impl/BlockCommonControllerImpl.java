package com.turkcell.blockmail.common.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.common.controller.BlockCommonController;
import com.turkcell.blockmail.common.service.BlockCommonService;
import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.model.BlockHoursOutput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockOnlyIdInput;
import com.turkcell.blockmail.model.BlockSystemListOutput;
import com.turkcell.blockmail.model.BlockTypeListOutput;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;

@RestController
public class BlockCommonControllerImpl implements BlockCommonController {
	
	@Autowired
	private BlockCommonService blockCommonService;
	
	@Autowired
	private BlockSendMailService blockSendMailService;


	@Override
	@RequestMapping(value = "/getBlockSystemList")
	public BlockSystemListOutput getBlockSystemList() {
		return blockCommonService.getSystemList();
	}

	@Override
	@RequestMapping(value = "/getBlockTypeList")
	public BlockTypeListOutput getBlockType() {
		return blockCommonService.getBlockType();
	}

	@Override
	@RequestMapping(value = "/saveSystemBlock")
	public BlockInfoOutput saveBlockSystem(@RequestBody BlockSystemListDocument input) {
		return blockCommonService.saveSystemList(input);
	}

	@Override
	@RequestMapping(value = "/saveBlockType")
	public BlockInfoOutput saveBlockType(@RequestBody BlockTypeListDocument input) {
		return blockCommonService.saveBlockType(input);
		
	}

	@Override
	@RequestMapping(value = "/getBlockHours")
	public BlockHoursOutput getBlockHours(@RequestBody BlockOnlyIdInput input) {
		return blockCommonService.getBlockHours(input.getId());
	}

	@Override
	@RequestMapping(value = "/sendEndDayReport")
	public void trySendEndDayReportMail() {
		blockSendMailService.sendEndDayReportMail();
	}

}
