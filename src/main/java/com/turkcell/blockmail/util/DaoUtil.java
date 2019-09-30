package com.turkcell.blockmail.util;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.ROOT;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;

public class DaoUtil {
	
	public static TypedAggregation<BlockInfoDocumentInput> getAggretionQuery(long pastTimeMilis, long endTimeMilis, 
			long skip, long limit, MatchOperation matchOperation, 
			SortOperation sortOperation, GroupOperation groupOperation, String segment) {
		
		Calendar calendar = Calendar.getInstance();
		long endDateControl = 0l;
		long systemTimeMilis = System.currentTimeMillis();
		calendar.setTimeInMillis(systemTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE, 30);
		systemTimeMilis = calendar.getTimeInMillis();
		endTimeMilis = endTimeMilis == 0 ? 	CalendarUtil.endTimeMilisInWorkTime(systemTimeMilis) : endTimeMilis;
		calendar.setTimeInMillis(endTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		long latestDayBlockHour = calendar.getTimeInMillis();
		
		calendar.setTimeInMillis(pastTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE, 30);
		long firstDayBlockHour = calendar.getTimeInMillis();
		
		
		Criteria cSegment = segment != null ? where("segment").is(segment) : where("startDate").exists(true);

		limit = limit == 0l ? 1000000000000000000l : limit;

		MatchOperation variableMatchOperation = matchOperation == null ? new MatchOperation(cSegment
				.orOperator(
								where("startDate").lte(pastTimeMilis).andOperator(where("status").is(true)),
								where("startDate").lte(pastTimeMilis).andOperator(where("endDate").gte(pastTimeMilis).lte(endTimeMilis),where("status").is(false)),
								where("startDate").gte(pastTimeMilis).lte(endTimeMilis).andOperator(where("endDate").gte(pastTimeMilis).lte(endTimeMilis)),
								where("startDate").gte(pastTimeMilis).lte(endTimeMilis).andOperator(where("status").is(true))
								)) : matchOperation;
		
		GroupOperation variableGroupOperation = groupOperation == null ? new GroupOperation(Fields.fields("startDate","endDate","blockDesc","blockName","status"))
				.first("startDate").as("startDate")
				.push(ROOT).as("block") : groupOperation;
		
		SortOperation variableSortOperation = sortOperation == null ? 
				new SortOperation(Sort.by(Direction.DESC, Aggregation.previousOperation(), "startDate")) : sortOperation;
		
		ProjectionOperation firstProject = new ProjectionOperation()
				.and("block").arrayElementAt(0).as("blockToObject")
				.and("startDate").as("startDate");
		
		
		ProjectionOperation decisionEndDateProject = new ProjectionOperation(Fields.fields("blockToObject","startDate"))
				.and("endDateForControl").applyCondition(Cond.newBuilder().when(Criteria.where("blockToObject.endDate").is(endDateControl))
					.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0", endTimeMilis))
					.otherwiseValueOf(AggregationSpELExpression.expressionOf("blockToObject.endDate-0")));
		
		
		
		ProjectionOperation decisionDateControlProject = new ProjectionOperation(Fields.fields("endDateForControl","blockToObject", "startDate"))
				.and("endDateForEndmilisControl").applyCondition(Cond.newBuilder().when(Criteria.where("endDateForControl").gt(endTimeMilis))
					.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0", endTimeMilis))
					.otherwiseValueOf(AggregationSpELExpression.expressionOf("endDateForControl-0")))
				.and("startDateForEndmilisControl").applyCondition(Cond.newBuilder().when(where("blockToObject.startDate").lt(pastTimeMilis))
						.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0", pastTimeMilis))
						.otherwise(AggregationSpELExpression.expressionOf("blockToObject.startDate-0")));
		
		ProjectionOperation primalCalculateBlockHours = new ProjectionOperation(Fields.fields("endDateForEndmilisControl","startDateForEndmilisControl"))
				.and("blockToObject").as("blockDetail")
				.and("latestDayBlockHourControlWithDecimals").applyCondition(Cond.newBuilder().when(where("endDateForEndmilisControl").lt(latestDayBlockHour))
						.thenValueOf(AggregationSpELExpression.expressionOf("([0] - endDateForEndmilisControl)/86400000", latestDayBlockHour))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
						)
				.and("firstDayBlockHourControlWithDecimals").applyCondition(Cond.newBuilder().when(where("startDateForEndmilisControl").gt(firstDayBlockHour))
						.thenValueOf(AggregationSpELExpression.expressionOf("(startDateForEndmilisControl - [0])/86400000", firstDayBlockHour))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
						)
				.and("blockHoursControlWithDecimals").applyCondition(Cond.newBuilder().when(where("blockToObject.startDate").gte(pastTimeMilis))
						.thenValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-startDate)/86400000"))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-[0])/86400000",pastTimeMilis))
						);
		
		ProjectionOperation rollBlockHoursProject = new ProjectionOperation(Fields.fields("blockDetail", "blockHoursControlWithDecimals","endDateForEndmilisControl","startDateForEndmilisControl","latestDayBlockHourControlWithDecimals", "firstDayBlockHourControlWithDecimals"))
				.and("latestDayBlockHourControlWithDecimals").trunc().as("latestDayBlockHourControl")
				.and("firstDayBlockHourControlWithDecimals").trunc().as("firstDayBlockHourControl")
				.and("blockHoursControlWithDecimals").trunc().as("blockHoursControl");
		
		ProjectionOperation firstAndLastDayBlockHourComputeProject = new ProjectionOperation(Fields.fields("blockDetail", "blockHoursControlWithDecimals","endDateForEndmilisControl"
						,"startDateForEndmilisControl","latestDayBlockHourControlWithDecimals"
						, "firstDayBlockHourControlWithDecimals","firstDayBlockHourControl","latestDayBlockHourControl","blockHoursControl"))
				.andExpression("[0]-86400000*(latestDayBlockHourControl+1)", latestDayBlockHour).as("latestDayBlockHourControlCompute")
				.andExpression("[0]+86400000*(firstDayBlockHourControl+1)", firstDayBlockHour).as("firstDayBlockHourControlCompute");
		
		ProjectionOperation computeParameterForFinalBlockHoursProject = new ProjectionOperation(Fields.fields("blockDetail","latestDayBlockHourControlCompute","firstDayBlockHourControlCompute","blockHoursControlWithDecimals"
						,"endDateForEndmilisControl","startDateForEndmilisControl"
						,"firstDayBlockHourControl","latestDayBlockHourControl"
						,"latestDayBlockHourControlWithDecimals", "firstDayBlockHourControlWithDecimals","blockHoursControl"))
				.and("latestDayBlockHour").applyCondition(Cond.newBuilder().when(where("latestDayBlockHourControlWithDecimals").ne(0))
						.thenValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-latestDayBlockHourControlCompute)/3600000"))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-[0])/3600000", latestDayBlockHour))
						)
				.and("latestDayBlockHourControlPlusOne").applyCondition(Cond.newBuilder().when(where("latestDayBlockHourControlWithDecimals").ne(0))
						.thenValueOf(AggregationSpELExpression.expressionOf("latestDayBlockHourControl + 1"))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
						)
				.and("firstDayBlockHour").applyCondition(Cond.newBuilder().when(where("firstDayBlockHourControlWithDecimals").ne(0))
						.thenValueOf(AggregationSpELExpression.expressionOf("(firstDayBlockHourControlCompute-startDateForEndmilisControl)/3600000"))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("([0]-startDateForEndmilisControl)/3600000", firstDayBlockHour))
						)
				.and("firstDayBlockHourControlPlusOne").applyCondition(Cond.newBuilder().when(where("firstDayBlockHourControlWithDecimals").ne(0))
						.thenValueOf(AggregationSpELExpression.expressionOf("firstDayBlockHourControl + 1"))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
						);
		
		
		ProjectionOperation computeBlockBeetweenDaysControl = new ProjectionOperation(Fields.fields("blockDetail", "blockHoursControl","latestDayBlockHour","firstDayBlockHour","blockHoursControlWithDecimals"))
				.and(AggregationSpELExpression.expressionOf("[0]-(latestDayBlockHour+firstDayBlockHour)", 9)).as("blockBeetweenDaysControl");
		
		ProjectionOperation computeBlockBetweenDays = new ProjectionOperation(Fields.fields("blockDetail","blockBeetweenDaysControl","blockHoursControlWithDecimals","blockHoursControl","latestDayBlockHour","firstDayBlockHour"))
				.and("blockBeetweenDays").applyCondition(Cond.newBuilder().when(where("blockBeetweenDaysControl").lte(0))
						.thenValueOf(AggregationSpELExpression.expressionOf("blockHoursControl-1"))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("blockHoursControl-0")))
				.and("isDifferentDay").applyCondition(Cond.newBuilder().when(where("blockHoursControlWithDecimals").gt(0.375))
						.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0",1))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0",0)));
		
		ProjectionOperation computeFinalBlockHoursProject = new ProjectionOperation()
				.and("blockDetail").as("blockDetail")
				.and("blockHours").applyCondition(Cond.newBuilder().when(where("isDifferentDay").is(1))
						.thenValueOf(AggregationSpELExpression.expressionOf("blockBeetweenDays*[0] + latestDayBlockHour + firstDayBlockHour",9))
						.otherwiseValueOf(AggregationSpELExpression.expressionOf("(firstDayBlockHour+latestDayBlockHour) - 9")));
		
				
		
		SkipOperation skipOperation = new SkipOperation(skip);
		
		LimitOperation limitOperation = new LimitOperation(limit);
		
		TypedAggregation<BlockInfoDocumentInput> aggregation = new TypedAggregation<>(BlockInfoDocumentInput.class, 
				variableMatchOperation, 
				variableGroupOperation, 
				variableSortOperation,
				firstProject,
				decisionEndDateProject, 
				decisionDateControlProject,
				primalCalculateBlockHours,
				rollBlockHoursProject, 
				firstAndLastDayBlockHourComputeProject, 
				computeParameterForFinalBlockHoursProject,
				computeBlockBeetweenDaysControl,
				computeBlockBetweenDays,
				computeFinalBlockHoursProject, 
				skipOperation, 
				limitOperation
				);
		
		return aggregation;
		
//		TypedAggregation<BlockInfoDocumentInput> aggretion = newAggregation(BlockInfoDocumentInput.class,
//				match(where("startDate").exists(true)
//						.orOperator(
//								where("startDate").lte(pastTimeMilis).andOperator(where("endDate").gte(0), where("status").is(true)),
//								where("startDate").gte(pastTimeMilis).lte(endTimeMilis).andOperator(where("endDate").gte(pastTimeMilis).lte(endTimeMilis)),
//								where("starDate").gte(pastTimeMilis).lte(endTimeMilis).andOperator(where("endDate").gte(0), where("status").is(true))
//								)
//						),
//				group("startDate","endDate","blockDesc","blockName","status")
//				.first("startDate").as("startedDate")
//				.push(ROOT).as("block"),
//				sort(Direction.DESC, previousOperation(),"startedDate"),
//				project()
//				.and("block").arrayElementAt(0).as("blockToObject")
//				.and("startedDate").as("startedDate"),
//				project("blockToObject","startedDate")
//					.and("endDateForControl").applyCondition(Cond.newBuilder().when(Criteria.where("blockToObject.endDate").is(endDateControl))
//							.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0", endTimeMilis))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("blockToObject.endDate-0"))
//							),
//				
//				project("endDateForControl","blockToObject", "startedDate")
//					.and("endDateForEndmilisControl").applyCondition(Cond.newBuilder().when(Criteria.where("endDateForControl").gt(endTimeMilis))
//							.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0", endTimeMilis))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("endDateForControl-0"))
//							)
//					.and("startDateForEndmilisControl").applyCondition(Cond.newBuilder().when(where("blockToObject.startDate").lt(pastTimeMilis))
//							.thenValueOf(AggregationSpELExpression.expressionOf("[0]-0", pastTimeMilis))
//							.otherwise(AggregationSpELExpression.expressionOf("blockToObject.startDate-0"))
//							),
//				
//				project("endDateForEndmilisControl","startDateForEndmilisControl")
//					.and("blockToObject").as("blockDetail")
//					.and("latestDayBlockHourControlWithDecimals").applyCondition(Cond.newBuilder().when(where("endDateForEndmilisControl").lt(latestDayBlockHour))
//							.thenValueOf(AggregationSpELExpression.expressionOf("([0] - endDateForEndmilisControl)/86400000", latestDayBlockHour))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
//							)
//					.and("firstDayBlockHourControlWithDecimals").applyCondition(Cond.newBuilder().when(where("startDateForEndmilisControl").gt(firstDayBlockHour))
//							.thenValueOf(AggregationSpELExpression.expressionOf("(startDateForEndmilisControl - [0])/86400000", firstDayBlockHour))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
//							)
//					.and("blockHoursControlWithDecimals").applyCondition(Cond.newBuilder().when(where("blockToObject.startDate").gte(pastTimeMilis))
//							.thenValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-startedDate)/86400000"))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-[0])/86400000",pastTimeMilis))
//							),
//				
//				project("blockDetail", "blockHoursControlWithDecimals","endDateForEndmilisControl","startDateForEndmilisControl","latestDayBlockHourControlWithDecimals", "firstDayBlockHourControlWithDecimals")
//					.and("latestDayBlockHourControlWithDecimals").trunc().as("latestDayBlockHourControl")
//					.and("firstDayBlockHourControlWithDecimals").trunc().as("firstDayBlockHourControl")
//					.and("blockHoursControlWithDecimals").trunc().as("blockHoursControl"),
//				
//				project("blockDetail", "blockHoursControlWithDecimals","endDateForEndmilisControl"
//						,"startDateForEndmilisControl","latestDayBlockHourControlWithDecimals"
//						, "firstDayBlockHourControlWithDecimals","firstDayBlockHourControl","latestDayBlockHourControl","blockHoursControl")
//				
//					.andExpression("[0]-86400000*(latestDayBlockHourControl+1)", latestDayBlockHour).as("latestDayBlockHourControlCompute")
//					.andExpression("[0]+86400000*(firstDayBlockHourControl+1)", firstDayBlockHour).as("firstDayBlockHourControlCompute"),
//				
//				project("blockDetail","latestDayBlockHourControlCompute","firstDayBlockHourControlCompute","blockHoursControlWithDecimals"
//						,"endDateForEndmilisControl","startDateForEndmilisControl"
//						,"firstDayBlockHourControl","latestDayBlockHourControl"
//						,"latestDayBlockHourControlWithDecimals", "firstDayBlockHourControlWithDecimals","blockHoursControl")
//				
//					.and("latestDayBlockHour").applyCondition(Cond.newBuilder().when(where("latestDayBlockHourControlWithDecimals").ne(0))
//							.thenValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-latestDayBlockHourControlCompute)/3600000"))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("(endDateForEndmilisControl-[0])/3600000", latestDayBlockHour))
//							)
//					.and("latestDayBlockHourControlPlusOne").applyCondition(Cond.newBuilder().when(where("latestDayBlockHourControlWithDecimals").ne(0))
//							.thenValueOf(AggregationSpELExpression.expressionOf("latestDayBlockHourControl + 1"))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
//							)
//					.and("firstDayBlockHour").applyCondition(Cond.newBuilder().when(where("firstDayBlockHourControlWithDecimals").ne(0))
//							.thenValueOf(AggregationSpELExpression.expressionOf("(firstDayBlockHourControlCompute-startDateForEndmilisControl)/3600000"))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("([0]-startDateForEndmilisControl)/3600000", firstDayBlockHour))
//							)
//					.and("firstDayBlockHourControlPlusOne").applyCondition(Cond.newBuilder().when(where("firstDayBlockHourControlWithDecimals").ne(0))
//							.thenValueOf(AggregationSpELExpression.expressionOf("firstDayBlockHourControl + 1"))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
//							)
//					.and("blockBeetweenDays").applyCondition(Cond.newBuilder().when(where("blockHoursControlWithDecimals").gt(1))
//							.thenValueOf(AggregationSpELExpression.expressionOf("blockHoursControl -1"))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("[0]-0", 0))
//							),
//				
//				project()
//					.and("blockDetail").as("blockDetail")
//					.and("blockHours").applyCondition(Cond.newBuilder().when(where("blockHoursControlWithDecimals").gt(1))
//							.thenValueOf(AggregationSpELExpression.expressionOf("blockBeetweenDays*[0] + latestDayBlockHour + firstDayBlockHour",9))
//							.otherwiseValueOf(AggregationSpELExpression.expressionOf("(latestDayBlockHour+firstDayBlockHour)-9"))),
//				skip(skip),
//				limit(limit)
//				);
	}
	
	public static TypedAggregation<BlockInfoDocumentInput> getLastBlocks(MatchOperation matchOperation, long skip, int limit, String segment) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 30);
		long pastTimeMilis = calendar.getTimeInMillis();
		
		long endTimeMilis = CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis());
		
		
		
		GroupOperation groupOperation = new GroupOperation(Fields.fields("startDate","endDate","blockDesc","blockName","status", "createDate"))
				.first("createDate").as("createDate")
				.push(ROOT).as("block");
		SortOperation sortOperation = new SortOperation(Sort.by(Direction.DESC, Aggregation.previousOperation(), "createDate" ));
		
		TypedAggregation<BlockInfoDocumentInput> aggregation = DaoUtil.getAggretionQuery(pastTimeMilis, endTimeMilis, skip, limit, matchOperation, sortOperation, groupOperation, segment);
		
		return aggregation;
	}
	
	public static List<Document> parseWeekendFromBlockList(List<Document> blockList) {
		List<Document> result = new ArrayList<>();
		result= blockList.stream()
			.map(s -> {
				double weekendTime = calculateWeekendTime((Long)s.get("startDate"), 
												(Long)s.get("endDate")  == 0 ? CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis()) : (Long)s.get("endDate"));
				double weekendBlockHours = weekendTime * Double.parseDouble("9");
				s.put("blockHours", (Double)s.get("blockHours") - weekendBlockHours);
				return s;
			}).collect(Collectors.toList());
		return result;
	}
	
	private static double calculateWeekendTime(long pastTimeMilis, long endTimeMilis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(endTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		long endTime = calendar.getTimeInMillis();
		calendar.clear();
		calendar.setTimeInMillis(pastTimeMilis);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		long startTime = calendar.getTimeInMillis();
		
		double weekendTime = 0;
		for(long i = startTime; i<endTime; i= calendar.getTimeInMillis()) {
			if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				weekendTime++;
			}
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		return weekendTime;
	}
}
