package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockHoursOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5338966043134761752L;
	
	
	private double blockHours;


	public double getBlockHours() {
		return blockHours;
	}


	public void setBlockHours(double blockHours) {
		this.blockHours = blockHours;
	}


	@Override
	public String toString() {
		return "BlockHoursOutput [blockHours=" + blockHours + "]";
	}
	
	

}
