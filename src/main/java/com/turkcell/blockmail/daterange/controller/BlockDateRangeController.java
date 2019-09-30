package com.turkcell.blockmail.daterange.controller;

import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartOutput;

public interface BlockDateRangeController {
	
	public BlockInfoAsTimeOutput getBlockToday(long limit, long skip, String segment);


	public BlockInfoAsTimeOutput getBlockWeek(long limit, long skip, long startDate, String segment);
	

	public BlockInfoAsTimeOutput getBlockMonth(long limit, long skip, long startDate, String segment);
	

	public BlockPieChartOutput getPiechartInfoToday(String env, String segment);


	public BlockPieChartOutput getPiechartInfoWeek(long startDateMilis, String env, String segment);


	public BlockPieChartOutput getPiechartInfoMonth(long startDateMilis, String env, String segment);
}
