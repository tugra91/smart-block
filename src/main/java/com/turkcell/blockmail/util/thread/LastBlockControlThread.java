package com.turkcell.blockmail.util.thread;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.util.thread.service.LastBlockThreadService;


@Service
public class LastBlockControlThread implements Runnable {
	
	

	private LastBlockThreadService lastBlockThreadService;
	
	private long createDate;
	private DeferredResult<BlockInfoAsTimeOutput> result;
	private LastBlockSettingThread latestBlockSettingThread;
	
	private SseEmitter emitter;


	@Override
	public void run() {
		
		boolean isNewBlock =  false;
		
		synchronized (result) {
			while(!isNewBlock) {
				try {
					try {
						emitter.send("OK");
						isNewBlock = lastBlockThreadService.isAddNewBlock(createDate);
					} catch(IOException e) {
						isNewBlock = true;
						System.out.println("Expire oldu bebiş");
					} 
					
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
			
			if(isNewBlock) {
				latestBlockSettingThread.setWaitControl(false);
				result.notify();
			}
		}
		
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public DeferredResult<BlockInfoAsTimeOutput> getResult() {
		return result;
	}

	public void setResult(DeferredResult<BlockInfoAsTimeOutput> result) {
		this.result = result;
	}
	

	public LastBlockSettingThread getLatestBlockSettingThread() {
		return latestBlockSettingThread;
	}

	public void setLatestBlockSettingThread(LastBlockSettingThread latestBlockSettingThread) {
		this.latestBlockSettingThread = latestBlockSettingThread;
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
