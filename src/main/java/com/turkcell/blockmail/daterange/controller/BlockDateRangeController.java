package com.turkcell.blockmail.daterange.controller;

import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartOutput;

public interface BlockDateRangeController {
	
	public BlockInfoAsTimeOutput getBlockToday(long limit, long skip);


	public BlockInfoAsTimeOutput getBlockWeek(long limit, long skip, long startDate);
	

	public BlockInfoAsTimeOutput getBlockMonth(long limit, long skip, long startDate);
	

	public BlockPieChartOutput getPiechartInfoToday(String env);


	public BlockPieChartOutput getPiechartInfoWeek(long startDateMilis, String env);


	public BlockPieChartOutput getPiechartInfoMonth(long startDateMilis, String env);
}
