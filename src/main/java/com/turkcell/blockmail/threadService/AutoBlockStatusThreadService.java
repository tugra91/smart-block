package com.turkcell.blockmail.threadService;

import java.io.IOException;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.turkcell.blockmail.document.BlockServiceResponseInfoDocument;

public class AutoBlockStatusThreadService implements Runnable{


	private String serviceName;
	private long lastUpdate;
//	private BlockMailService blockMailService;
	private SseEmitter emitter;
	private DeferredResult<BlockServiceResponseInfoDocument> result;

	public AutoBlockStatusThreadService(String serviceName, long lastUpdate, DeferredResult<BlockServiceResponseInfoDocument> result,
			 SseEmitter emitter) {
		this.serviceName = serviceName;
		this.lastUpdate = lastUpdate;
//		this.blockMailService = blockMailService;
		this.emitter = emitter;
		this.result = result;
	}

	@Override
	public void run() {

//		boolean isNewTime = false;
//		BlockServiceResponseInfoDocument blockServiceInfo = new BlockServiceResponseInfoDocument();
//
//		while(!isNewTime) {
//			try {
//				emitter.send("OK");
//				blockServiceInfo = blockMailService.getBlockServiceResponse(serviceName);
//				if(blockServiceInfo.getLastUpdate() != lastUpdate) {
//					result.setResult(blockServiceInfo);
//					isNewTime = true;
//				}
//			}catch (IOException e) {
//				isNewTime = true;
//			}
//		}
	}

}
