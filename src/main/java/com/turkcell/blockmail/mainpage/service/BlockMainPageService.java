package com.turkcell.blockmail.mainpage.service;

import java.util.List;

import org.bson.Document;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartOutput;

public interface BlockMainPageService {
	
	public List<Document> getLastBlocks(long skip, int limit);
	
	public DeferredResult<SseEmitter> getSseEmitter(String clientId);
	
	public DeferredResult<BlockInfoAsTimeOutput> getLongPoll(long lastCreatedDate, String clientId);
	
	public BlockPieChartOutput getBlockPiechartInfoForLastBlocks(long skip, int limit, String env, boolean isActive);

}
