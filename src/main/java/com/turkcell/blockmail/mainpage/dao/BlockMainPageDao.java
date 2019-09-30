package com.turkcell.blockmail.mainpage.dao;

import java.util.List;

import org.bson.Document;

public interface BlockMainPageDao {
	
	
	public List<Document> getLastBlocks(long skip, int limit, String segment) throws NullPointerException;
	
}
