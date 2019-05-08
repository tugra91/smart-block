package com.turkcell.blockmail.util.thread;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.util.thread.service.LastBlockThreadService;

@Service
public class LastBlockSettingThread implements Runnable {
	
	
	private LastBlockThreadService lastBlockThreadService;

	
	private long lastCreatedDate;
	private DeferredResult<BlockInfoAsTimeOutput> result;
	private boolean waitControl;
	private SseEmitter emitter;
	
	
	
	@Override
	public void run() {
		BlockInfoAsTimeOutput output = new BlockInfoAsTimeOutput();
		
		synchronized (result) {
			try {
				
				while(waitControl) {
					result.wait();
				}
				
				if(!result.isSetOrExpired()) {
					output.setBlockDetail(lastBlockThreadService.fetchLastAddedBlocks(lastCreatedDate));
					result.setResult(output);
				}
				emitter.complete();
				
				
			} catch (InterruptedException e) {
			}
		}
		
	}

	public boolean isWaitControl() {
		return waitControl;
	}

	public void setWaitControl(boolean waitControl) {
		this.waitControl = waitControl;
	}

	public long getLastCreatedDate() {
		return lastCreatedDate;
	}

	public void setLastCreatedDate(long lastCreatedDate) {
		this.lastCreatedDate = lastCreatedDate;
	}

	public DeferredResult<BlockInfoAsTimeOutput> getResult() {
		return result;
	}

	public void setResult(DeferredResult<BlockInfoAsTimeOutput> result) {
		this.result = result;
	}

	public SseEmitter getEmitter() {
		return emitter;
	}

	public void setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
	}

	public LastBlockThreadService getLastBlockThreadService() {
		return lastBlockThreadService;
	}

	public void setLastBlockThreadService(LastBlockThreadService lastBlockThreadService) {
		this.lastBlockThreadService = lastBlockThreadService;
	}
	
	
	
	

}
