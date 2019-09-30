package com.turkcell.blockmail.daterange.service;

import java.util.List;

import org.bson.Document;

import com.turkcell.blockmail.model.BlockPieChartOutput;

public interface BlockDateRangeService {
	
	
	public List<Document> getBlockToday(boolean isService, long skip, long limit, String segment);
	
	public List<Document> getBlockWeek(long startTimeMilis, boolean isService, long skip, long limit, String segment);
	
	public List<Document> getBlockMonth(long startTimeMilis, boolean isService, long skip, long limit, String segment);
	
	public BlockPieChartOutput getBlockPiechartInfoForToday(boolean isService, String env, String segment);

	public BlockPieChartOutput getBlockPiechartInfoForWeekAndMonth(long startDateMilis, boolean isService, String env, boolean isWeek, String segment);
	
	public List<Document> getBlockOfParameter(long pastTimeMilis, long endTimeMilis, long skip, long limit, String segment);
	
	public BlockPieChartOutput getBlockPiechartInfoAsBlockList(List<Document> blockList, String env);
	
	public List<Document> getBlockOfYesterday(String segment);
	
	public List<Document> getBlockOfParameter(long pastTimeMilis, long endTimeMilis, String segment);
	
	public List<Document> getBlockForAllStatus(long pastTimeMilis, long endTimeMilis, String segment);
}
