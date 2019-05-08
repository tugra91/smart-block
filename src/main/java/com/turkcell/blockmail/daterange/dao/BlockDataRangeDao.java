package com.turkcell.blockmail.daterange.dao;

import java.util.List;

import org.bson.Document;

public interface BlockDataRangeDao {
	
	
	public List<Document> getBlockForTime(long pastTimeMilis, long endTimeMilis);
	
	public List<Document> getBlockForTime(long pastTimeMilis, long endTimeMilis, long skip, long limit);
	
	public List<Document> getBlockForMontly(long pastTimeMilis, long endTimeMilis, long skip, long limit);
	
	public List<Document> getBlockForAllStatus(long pastTimeMilis, long endTimeMilis);
	
}
