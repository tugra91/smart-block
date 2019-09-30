package com.turkcell.blockmail.mainpage.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.turkcell.blockmail.mainpage.controller.BlockMainPageController;
import com.turkcell.blockmail.mainpage.service.BlockMainPageService;
import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartLastBlocksInput;
import com.turkcell.blockmail.model.BlockPieChartOutput;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;

@RestController
public class BlockMainPageControllerImpl implements BlockMainPageController {
	
	@Autowired
	private BlockMainPageService blockMainPageService;
	
	@Autowired
	private BlockSendMailService blockSendMail;

	@Override
	@RequestMapping(value = "/getLastBlocks")
	public BlockInfoAsTimeOutput getLastBlocks(@RequestParam("skip") long skip, 
			@RequestParam("limit") int limit, 
			@RequestParam("segment") String segment) {
		BlockInfoAsTimeOutput output = new BlockInfoAsTimeOutput();
		output.setBlockDetail(blockMainPageService.getLastBlocks(skip, limit, segment));
//		blockSendMail.sendEndDayReportMail();
		return output;
	}

	@Override
	@RequestMapping(value = "/getSse")
	public DeferredResult<SseEmitter> getSseEmitter(@RequestParam("clientId") String clientId) {
		return blockMainPageService.getSseEmitter(clientId);
	}

	@Override
	@RequestMapping(value = "/getLongPollingBlock/{lastCreatedDate}")
	public DeferredResult<BlockInfoAsTimeOutput> getLongPoll(@PathVariable long lastCreatedDate, @RequestParam("clientId") String clientId, @RequestParam("segment") String segment) {
		return blockMainPageService.getLongPoll(lastCreatedDate,clientId, segment);
	}

	@Override
	@RequestMapping(value = "/getPieChartLastBlocks")
	public BlockPieChartOutput getPiechartInfoLastBlocks(@RequestBody BlockPieChartLastBlocksInput input) {
		return blockMainPageService.getBlockPiechartInfoForLastBlocks(input.getSkip(), input.getLimit(), input.getEnv(), input.isActive(), input.getSegment());
	}

}
