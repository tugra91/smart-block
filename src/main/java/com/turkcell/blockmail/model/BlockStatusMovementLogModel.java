package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockStatusMovementLogModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -808352192437590732L;
	
	private String statusLabel;
	private String excProcessUser;
	private long processDate;
	
	public String getStatusLabel() {
		return statusLabel;
	}
	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}
	public String getExcProcessUser() {
		return excProcessUser;
	}
	public void setExcProcessUser(String excProcessUser) {
		this.excProcessUser = excProcessUser;
	}
	public long getProcessDate() {
		return processDate;
	}
	public void setProcessDate(long processDate) {
		this.processDate = processDate;
	}
	
	@Override
	public String toString() {
		return "BlockStatusMovementLogModel [statusLabel=" + statusLabel + ", excProcessUser=" + excProcessUser
				+ ", processDate=" + processDate + "]";
	}
	
	

}
