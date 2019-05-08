package com.turkcell.blockmail.model;

import java.io.Serializable;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;

public class BlockWeekResultModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4276531109644244194L;
	
	private long startedDate;
	private long sum;
	private long sumHours;
	private BlockInfoDocumentInput blockLar;
	public long getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(long startedDate) {
		this.startedDate = startedDate;
	}
	public long getSum() {
		return sum;
	}
	public void setSum(long sum) {
		this.sum = sum;
	}
	public long getSumHours() {
		return sumHours;
	}
	public void setSumHours(long sumHours) {
		this.sumHours = sumHours;
	}
	public BlockInfoDocumentInput getBlockLar() {
		return blockLar;
	}
	public void setBlockLar(BlockInfoDocumentInput blockLar) {
		this.blockLar = blockLar;
	}
	
	
}
