package com.turkcell.blockmail.daterange.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.daterange.controller.BlockDateRangeController;
import com.turkcell.blockmail.daterange.service.BlockDateRangeService;
import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartOutput;

@RestController
public class BlockDateRangeControllerImpl implements BlockDateRangeController {
	
	@Autowired
	private BlockDateRangeService blockDateRangeService;
	

	@Override
	@RequestMapping(value = "/getBlockToday")
	public BlockInfoAsTimeOutput getBlockToday(@RequestParam("limit") long limit, @RequestParam("skip") long skip) {
		BlockInfoAsTimeOutput output = new BlockInfoAsTimeOutput();
		output.setBlockDetail(blockDateRangeService.getBlockToday(true, skip, limit));
		return output;
	}

	@Override
	@RequestMapping(value = "/getBlockWeek")
	public BlockInfoAsTimeOutput getBlockWeek(@RequestParam("limit") long limit, 
			@RequestParam("skip") long skip, @RequestParam("startDate") long startDate) {
		BlockInfoAsTimeOutput output = new BlockInfoAsTimeOutput();
		output.setBlockDetail(blockDateRangeService.getBlockWeek(startDate, true, skip, limit));
		return output;

	}
	
	@Override
	@RequestMapping(value = "/getBlockMonth")
	public BlockInfoAsTimeOutput getBlockMonth(@RequestParam("limit") long limit, 
			@RequestParam("skip") long skip, @RequestParam("startDate") long startDate) {
		BlockInfoAsTimeOutput output = new BlockInfoAsTimeOutput();
		output.setBlockDetail(blockDateRangeService.getBlockMonth(startDate, true, skip, limit));
		return output;
	}

	@Override
	@RequestMapping(value = "/getPieChartToday")
	public BlockPieChartOutput getPiechartInfoToday(@RequestParam("env") String env) {
		return blockDateRangeService.getBlockPiechartInfoForToday(true,env);
	}

	@Override
	@RequestMapping(value = "/getPieChartWeek")
	public BlockPieChartOutput getPiechartInfoWeek(@RequestParam("startDate") long startDateMilis, @RequestParam("env") String env) {
		return blockDateRangeService.getBlockPiechartInfoForWeekAndMonth(startDateMilis,true, env, true);
	}

	@Override
	@RequestMapping(value = "/getPieChartMonth")
	public BlockPieChartOutput getPiechartInfoMonth(@RequestParam("startDate") long startDateMilis, @RequestParam("env") String env) {
		return blockDateRangeService.getBlockPiechartInfoForWeekAndMonth(startDateMilis,true, env, false);
	}

}
