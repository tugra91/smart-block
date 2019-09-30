package com.turkcell.blockmail.daterange.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.turkcell.blockmail.common.service.BlockCommonService;
import com.turkcell.blockmail.daterange.dao.BlockDataRangeDao;
import com.turkcell.blockmail.daterange.service.BlockDateRangeService;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.model.BlockPieChartInfoModel;
import com.turkcell.blockmail.model.BlockPieChartOutput;
import com.turkcell.blockmail.util.CalendarUtil;

@Service
public class BlockDateRangeServiceImpl implements BlockDateRangeService {
	
	
	@Autowired
	private BlockDataRangeDao blockDataRangeDao;
	
	@Autowired
	private BlockCommonService blockCommonService;
	
	@Override
	public List<Document> getBlockToday(boolean isService, long skip, long limit, String segment) {
		List<Document> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		long pastTimeMilis = 0l;
		long endTimeMilis = 0l;
		calendar.setTimeInMillis(System.currentTimeMillis());
		int dayWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
		if(dayWeekNumber>=Calendar.MONDAY && dayWeekNumber <= Calendar.FRIDAY) {
			calendar.set(Calendar.HOUR_OF_DAY, 07);
			calendar.set(Calendar.MINUTE, 30);
			pastTimeMilis = calendar.getTimeInMillis();
			calendar.set(Calendar.HOUR_OF_DAY, 16);
			calendar.set(Calendar.MINUTE, 30);
			endTimeMilis = calendar.getTimeInMillis();
			if(isService) {
				endTimeMilis = CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis());
			}
			result = blockDataRangeDao.getBlockForTime(pastTimeMilis,endTimeMilis, skip, limit, segment);
		}
		return result;
	}

	@Override
	public List<Document> getBlockWeek(long startTimeMilis, boolean isService, long skip, long limit, String segment) {
		List<Document> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		long pastTimeMilis = 0l;
		long endTimeMilis = 0l;
		long sysTimeMilis = 0l;
		calendar.setTimeInMillis(startTimeMilis);
		int dayWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
		if(dayWeekNumber>=Calendar.MONDAY && dayWeekNumber <= Calendar.FRIDAY) {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 07);
			calendar.set(Calendar.MINUTE, 30);
			pastTimeMilis = calendar.getTimeInMillis();
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 16);
			calendar.set(Calendar.MINUTE, 30);
			endTimeMilis = calendar.getTimeInMillis();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, 16);
			calendar.set(Calendar.MINUTE, 30);
			sysTimeMilis = calendar.getTimeInMillis();
			endTimeMilis = isSysDateLessThanEndDate(sysTimeMilis, endTimeMilis) ? sysTimeMilis : endTimeMilis;
			
			if(isService) {
				endTimeMilis = CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis());
			}
		} else if(dayWeekNumber == Calendar.SATURDAY || dayWeekNumber == Calendar.SUNDAY) {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 07);
			calendar.set(Calendar.MINUTE, 30);
			pastTimeMilis = calendar.getTimeInMillis();
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 16);
			calendar.set(Calendar.MINUTE, 30);
			endTimeMilis = calendar.getTimeInMillis();	
		}
		result = blockDataRangeDao.getBlockForTime(pastTimeMilis,endTimeMilis, skip, limit, segment);
		return result;
	}



	@Override
	public List<Document> getBlockMonth(long startTimeMilis, boolean isService, long skip, long limit, String segment) {
		List<Document> result = new ArrayList<>();
		List<Document> weekResult = new ArrayList<>();
		long pastTimeMilis = 0l;
		long endTimeMilis = 0l;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startTimeMilis);
		int today = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		
		endTimeMilis = endTimeMilis == 0 ? System.currentTimeMillis() : endTimeMilis;
		Calendar calendarToday = Calendar.getInstance();
		calendarToday.setTimeInMillis(CalendarUtil.endTimeMilisInWorkTime(endTimeMilis));
		long sysTimeMilis  = calendarToday.getTimeInMillis();
		
		int month = calendar.get(Calendar.MONTH);
		for(int i = 0; i<calendarToday.get(Calendar.DAY_OF_MONTH); i = calendar.get(Calendar.DAY_OF_MONTH)) {
			if(calendar.get(Calendar.DAY_OF_MONTH) > today) {
				break;
			}
			if(calendar.get(Calendar.DAY_OF_MONTH) >= 1 && month != calendar.get(Calendar.MONTH)) {
				break;
			}
			int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if(dayWeek >= Calendar.MONDAY && dayWeek <= Calendar.FRIDAY) {
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				calendar.set(Calendar.HOUR_OF_DAY, 07);
				calendar.set(Calendar.MINUTE, 30);
				pastTimeMilis = calendar.getTimeInMillis();
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				if(calendar.get(Calendar.DAY_OF_MONTH) > today) {
					while(true) {
						calendar.add(Calendar.DAY_OF_MONTH, -1);
						if(today == calendar.get(Calendar.DAY_OF_MONTH)) {
							break;
						}
					}
				}
				if(calendar.get(Calendar.DAY_OF_MONTH) >= 1 && month != calendar.get(Calendar.MONTH)) {
					while(true) {
						calendar.add(Calendar.DAY_OF_MONTH, -1);
						if(month == calendar.get(Calendar.MONTH)) {
							break;
						}
					}
				}
				calendar.set(Calendar.HOUR_OF_DAY, 16);
				calendar.set(Calendar.MINUTE, 30);
				endTimeMilis = calendar.getTimeInMillis()> sysTimeMilis ? calendarToday.getTimeInMillis() : calendar.getTimeInMillis();
//				endTimeMilis = isSysDateLessThanEndDate(System.currentTimeMillis(), endTimeMilis) ? System.currentTimeMillis() : calendar.getTimeInMillis();
//				if(isService) {
//					endTimeMilis = CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis());
//				}
				weekResult = getBlockOfMonthly(pastTimeMilis, endTimeMilis, skip, limit, segment);
				result = Stream.concat(weekResult.stream(), result.stream()).collect(Collectors.toList());
				
			}
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		

		result = arrangeMonthlyBlockList(result);
	
		
		return result;
	}


	@Override
	public BlockPieChartOutput getBlockPiechartInfoForToday(boolean isService,String env, String segment) {
		List<Document> blockList = this.getBlockToday(isService,0, 0, segment);
		return calculatePieChartInfo(blockList, env);
	}

	@Override
	public BlockPieChartOutput getBlockPiechartInfoForWeekAndMonth(long startDateMilis, 
			boolean isService, String env, boolean isWeek, String segment) {
		List<Document> blockList = new ArrayList<>();
		if(isWeek) {
			blockList = this.getBlockWeek(startDateMilis, isService, 0, 0, segment);
		} else {
			blockList = this.getBlockMonth(startDateMilis, isService, 0, 0, segment);
		}

		return calculatePieChartInfo(blockList, env);
	}
	
	@Override
	public List<Document> getBlockOfParameter(long pastTimeMilis, long endTimeMilis, long skip, long limit, String segment) {
		List<Document> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pastTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		pastTimeMilis = calendar.getTimeInMillis();
		calendar.setTimeInMillis(endTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE,30);
		endTimeMilis = calendar.getTimeInMillis();
		result = blockDataRangeDao.getBlockForTime(pastTimeMilis, endTimeMilis, skip, limit, segment);
		return result;
	}
	
	@Override
	public List<Document> getBlockOfParameter(long pastTimeMilis, long endTimeMilis, String segment) {
		List<Document> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pastTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		pastTimeMilis = calendar.getTimeInMillis();
		calendar.setTimeInMillis(endTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE,30);
		endTimeMilis = calendar.getTimeInMillis();
		result = blockDataRangeDao.getBlockForTime(pastTimeMilis, endTimeMilis, segment);
		return result;
	}
	
	private List<Document> arrangeMonthlyBlockList(List<Document> result) {
		List<Document> editResult = new ArrayList<>();
		Gson gson = new Gson();
		for(Document rs : result) {
			BlockInfoDocumentInput blockDetail = gson.fromJson(gson.toJson(rs.get("blockDetail")), BlockInfoDocumentInput.class);
			boolean isExist = editResult.stream().anyMatch(s ->{
				BlockInfoDocumentInput blockDetailInner = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
				if(StringUtils.equalsIgnoreCase(blockDetail.getUserId().toHexString(), blockDetailInner.getUserId().toHexString()) 
						&& blockDetail.getCreateDate() == blockDetailInner.getCreateDate()
						&& StringUtils.equalsIgnoreCase(blockDetail.getBlockDesc(), blockDetailInner.getBlockDesc())) {
					return true;
				}
				return false;
			});
			
			if(isExist) {
				continue;
			}
			
			double otherBlockHours = result.stream().filter(s -> {
					BlockInfoDocumentInput blockDetailInner = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
					if(StringUtils.equalsIgnoreCase(blockDetail.getUserId().toHexString(), blockDetailInner.getUserId().toHexString()) 
							&& blockDetail.getCreateDate() == blockDetailInner.getCreateDate()
							&& StringUtils.equalsIgnoreCase(blockDetail.getBlockDesc(), blockDetailInner.getBlockDesc())
							&& s.getDouble("blockHours") != rs.getDouble("blockHours")) {
						return true;
					}
					return false;
				}).reduce(new Double("0"), (sum, p1) -> sum = sum + p1.getDouble("blockHours"), (s1,s2) -> s1 +s2);
		

			if(otherBlockHours != 0) {
				rs.put("blockHours", rs.getDouble("blockHours") + otherBlockHours);
			}
			editResult.add(rs);
		}
		return editResult;
	}
	
	private List<Document> getBlockOfMonthly(long pastTimeMilis, long endTimeMilis, long skip, long limit, String segment) {
		List<Document> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pastTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		pastTimeMilis = calendar.getTimeInMillis();
		calendar.setTimeInMillis(endTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE,30);
		endTimeMilis = calendar.getTimeInMillis();
		result = blockDataRangeDao.getBlockForMontly(pastTimeMilis, endTimeMilis, skip, limit, segment);
		return result;
	}
	
 	private BlockPieChartOutput calculatePieChartInfo(List<Document> blockList, String env) {

		BlockPieChartOutput output = new BlockPieChartOutput();

		List<BlockPieChartInfoModel> blockSystemList = new ArrayList<>();
		List<BlockPieChartInfoModel> affectSystemList = new ArrayList<>();
		List<BlockPieChartInfoModel> blockTypeList = new ArrayList<>();
		List<BlockPieChartInfoModel> blockSystemPieceList = new ArrayList<>();
		List<BlockPieChartInfoModel> affectSystemPieceList = new ArrayList<>();
		List<BlockPieChartInfoModel> blockTypePieceList = new ArrayList<>();
		List<BlockSystemListDocument> systemList  = blockCommonService.getSystemList().getSystemList();
		List<BlockTypeListDocument> typeList = blockCommonService.getBlockType().getTypeList();

		Gson gson = new Gson();
		double totalBlockSum = 0;
		NumberFormat numberFormat = new DecimalFormat("#0.00");
		for(BlockSystemListDocument rs: systemList) {
			BlockPieChartInfoModel blockSystemModel = new BlockPieChartInfoModel();
			totalBlockSum = blockList
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.reduce(0d, (sum,p1) -> sum += p1.getDouble("blockHours").doubleValue(), (sum1, sum2) -> sum1 + sum2);
			blockSystemModel.setLabel(rs.getSystemName());
			
			blockSystemModel.setValue(Double.parseDouble(numberFormat.format(totalBlockSum).replace(",", ".")));
			blockSystemList.add(blockSystemModel);

			BlockPieChartInfoModel blockSystemPieceModel = new BlockPieChartInfoModel();
			totalBlockSum = 0;
			totalBlockSum = blockList
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.count();
			blockSystemPieceModel.setLabel(rs.getSystemName());
			blockSystemPieceModel.setValue(totalBlockSum);
			blockSystemPieceList.add(blockSystemPieceModel);


			BlockPieChartInfoModel affectSystemModel = new BlockPieChartInfoModel();
			totalBlockSum = 0;
			totalBlockSum = blockList
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.reduce(0d, (sum,p1) -> sum += p1.getDouble("blockHours").doubleValue(), (sum1, sum2) -> sum1 + sum2);
			affectSystemModel.setLabel(rs.getSystemName());
			affectSystemModel.setValue(Double.parseDouble(numberFormat.format(totalBlockSum).replace(",", ".")));
			affectSystemList.add(affectSystemModel);

			BlockPieChartInfoModel affectSystemPieceModel = new BlockPieChartInfoModel();
			totalBlockSum = 0;
			totalBlockSum = blockList
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.count();
			affectSystemPieceModel.setLabel(rs.getSystemName());
			affectSystemPieceModel.setValue(totalBlockSum);
			affectSystemPieceList.add(affectSystemPieceModel);
		}

		totalBlockSum = 0;

		for(BlockTypeListDocument rs: typeList) {
			BlockPieChartInfoModel blockTypeModel = new BlockPieChartInfoModel();
			totalBlockSum = blockList
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockType(), rs.getBlockType());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.reduce(0d, (sum,p1) -> sum += p1.getDouble("blockHours").doubleValue(), (sum1, sum2) -> sum1 + sum2);
			blockTypeModel.setLabel(rs.getBlockType());
			blockTypeModel.setValue(Double.parseDouble(numberFormat.format(totalBlockSum).replace(",", ".")));
			blockTypeList.add(blockTypeModel);

			BlockPieChartInfoModel blockTypePieceModel = new BlockPieChartInfoModel();
			totalBlockSum = blockList
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockType(), rs.getBlockType());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.count();
			blockTypePieceModel.setLabel(rs.getBlockType());
			blockTypePieceModel.setValue(totalBlockSum);
			blockTypePieceList.add(blockTypePieceModel);
		}


		output.setBlockSystem(blockSystemList);
		output.setAffectSystem(affectSystemList);
		output.setBlockType(blockTypeList);
		output.setBlockSystemPiece(blockSystemPieceList);
		output.setAffectSystemPiece(affectSystemPieceList);
		output.setBlockTypePiece(blockTypePieceList);

		return output;
	}

	@Override
	public BlockPieChartOutput getBlockPiechartInfoAsBlockList(List<Document> blockList, String env) {
		return calculatePieChartInfo(blockList, env);
	}

	@Override
	public List<Document> getBlockOfYesterday(String segment) {
		List<Document> result = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		long pastTimeMilis = 0l;
		long endTimeMilis = 0l;
		calendar.setTime(new Date(System.currentTimeMillis()));
		int dayWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
		if(dayWeekNumber>=2 && dayWeekNumber <=6) {
			int controlValue = dayWeekNumber - 1;
			if(controlValue != 1) {
				calendar.add(Calendar.DAY_OF_WEEK, -1);
			} else if(controlValue == 1) {
				calendar.add(Calendar.DAY_OF_MONTH, -3);
			}
			calendar.set(Calendar.HOUR_OF_DAY, 07);
			calendar.set(Calendar.MINUTE, 30);
			pastTimeMilis = calendar.getTimeInMillis();
			calendar.set(Calendar.HOUR_OF_DAY, 16);
			calendar.set(Calendar.MINUTE, 30);
			endTimeMilis = calendar.getTimeInMillis();
			result = blockDataRangeDao.getBlockForTime(pastTimeMilis, endTimeMilis, segment);
		}
		return result;
	}

	@Override
	public List<Document> getBlockForAllStatus(long pastTimeMilis, long endTimeMilis, String segment) {
		return blockDataRangeDao.getBlockForAllStatus(pastTimeMilis, endTimeMilis, segment);
	}
	
	
	private boolean isSysDateLessThanEndDate(long sysDateMilis, long endTimeMilis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(sysDateMilis);
		int sysMonth = calendar.get(Calendar.MONTH);
		int sysDay = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.setTimeInMillis(endTimeMilis);
		int endMonth = calendar.get(Calendar.MONTH);
		int endDay = calendar.get(Calendar.DAY_OF_MONTH);

		boolean result = false;


		if(endMonth == sysMonth 
				&& sysDay < endDay) {
			result  = true;
		} else if (endMonth > sysMonth) {
			result = true;
		}

		return result;

	}

	
	
//	private long endTimeMilisInWorkTime (long endTimeMilis) {
//		Calendar endTimeCalendar = Calendar.getInstance();
//		endTimeCalendar.setTimeInMillis(endTimeMilis);
//		if((endTimeCalendar.get(Calendar.HOUR_OF_DAY) > 0 && endTimeCalendar.get(Calendar.HOUR_OF_DAY) < 7) 
//				|| (endTimeCalendar.get(Calendar.HOUR_OF_DAY) == 7 && endTimeCalendar.get(Calendar.MINUTE) < 30) ) {
//			endTimeCalendar.add(Calendar.DAY_OF_YEAR, -1);
//			endTimeCalendar.set(Calendar.HOUR_OF_DAY, 16);
//			endTimeCalendar.set(Calendar.MINUTE, 30);
//		} else if((endTimeCalendar.get(Calendar.HOUR_OF_DAY) > 16 && endTimeCalendar.get(Calendar.HOUR_OF_DAY) <= 23) 
//				|| (endTimeCalendar.get(Calendar.HOUR_OF_DAY) == 16 && endTimeCalendar.get(Calendar.MINUTE) >= 30)) {
//			endTimeCalendar.set(Calendar.HOUR_OF_DAY, 16);
//			endTimeCalendar.set(Calendar.MINUTE, 30);
//		}
//		
//		return endTimeCalendar.getTimeInMillis();
//	}



}
