package com.turkcell.blockmail.document;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlockDateControlDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1306461575833680671L;
	
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "BlockDateControlDocument [date=" + date + "]";
	}
	
	
	

}
