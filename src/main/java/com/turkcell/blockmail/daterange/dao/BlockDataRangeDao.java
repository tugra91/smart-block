package com.turkcell.blockmail.daterange.dao;

import java.util.List;

import org.bson.Document;

public interface BlockDataRangeDao {
	
	
	public List<Document> getBlockForTime(long pastTimeMilis, long endTimeMilis, String segment);
	
	public List<Document> getBlockForTime(long pastTimeMilis, long endTimeMilis, long skip, long limit, String segment);
	
	public List<Document> getBlockForMontly(long pastTimeMilis, long endTimeMilis, long skip, long limit, String segment);
	
	public List<Document> getBlockForAllStatus(long pastTimeMilis, long endTimeMilis, String segment);
	
}
