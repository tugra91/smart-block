package com.turkcell.blockmail.util.thread.dao;

import java.util.List;

import org.bson.Document;

public interface LastBlockThreadDao {
	
	public long getLastCreateDate(String segment);
	
	public List<Document> fetchLastAddedBlock(long createDate, String segment);

}
