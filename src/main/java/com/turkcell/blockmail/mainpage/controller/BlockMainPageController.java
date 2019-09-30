package com.turkcell.blockmail.mainpage.controller;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartLastBlocksInput;
import com.turkcell.blockmail.model.BlockPieChartOutput;

public interface BlockMainPageController {
	
	public BlockInfoAsTimeOutput getLastBlocks(long skip,  int limit, String segment);
	
	public DeferredResult<SseEmitter> getSseEmitter(String clientId);
	
	public DeferredResult<BlockInfoAsTimeOutput> getLongPoll(long lastCreatedDate, String clientId, String segment);
	
	public BlockPieChartOutput getPiechartInfoLastBlocks(BlockPieChartLastBlocksInput input);
}
