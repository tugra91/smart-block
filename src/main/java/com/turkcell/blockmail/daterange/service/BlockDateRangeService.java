package com.turkcell.blockmail.daterange.service;

import java.util.List;

import org.bson.Document;

import com.turkcell.blockmail.model.BlockPieChartOutput;

public interface BlockDateRangeService {
	
	
	public List<Document> getBlockToday(boolean isService, long skip, long limit);
	
	public List<Document> getBlockWeek(long startTimeMilis, boolean isService, long skip, long limit);
	
	public List<Document> getBlockMonth(long startTimeMilis, boolean isService, long skip, long limit);
	
	public BlockPieChartOutput getBlockPiechartInfoForToday(boolean isService, String env);

	public BlockPieChartOutput getBlockPiechartInfoForWeekAndMonth(long startDateMilis, boolean isService, String env, boolean isWeek);
	
	public List<Document> getBlockOfParameter(long pastTimeMilis, long endTimeMilis, long skip, long limit);
	
	public BlockPieChartOutput getBlockPiechartInfoAsBlockList(List<Document> blockList, String env);
	
	public List<Document> getBlockOfYesterday();
	
	public List<Document> getBlockOfParameter(long pastTimeMilis, long endTimeMilis);
	
	public List<Document> getBlockForAllStatus(long pastTimeMilis, long endTimeMilis);
}
