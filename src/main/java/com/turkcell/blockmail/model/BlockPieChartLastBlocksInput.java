package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockPieChartLastBlocksInput implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4821256075728233218L;
	
	private long skip;
	private int limit;
	private String env;
	private boolean isActive;
	
	
	public long getSkip() {
		return skip;
	}

	public void setSkip(long skip) {
		this.skip = skip;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "BlockPieChartLastBlocksInput [skip=" + skip + ", limit=" + limit + ", env=" + env + ", isActive="
				+ isActive + "]";
	}

	
	
	
	
	

}
