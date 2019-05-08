package com.turkcell.blockmail.util.thread.service;

import java.util.List;

import org.bson.Document;

public interface LastBlockThreadService {
	
	public boolean isAddNewBlock(long lastCreatedDate);
	
	public List<Document> fetchLastAddedBlocks(long createDate);

}
