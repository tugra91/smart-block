package com.turkcell.blockmail.model;

import java.io.Serializable;

public class BlockPieChartInfoModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2405146573284946237L;
	
	
	private String label;
	private double value;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "BlockPieChartInfoModel [label=" + label + ", value=" + value + "]";
	}
	
	
	
}
