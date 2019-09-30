package com.turkcell.blockmail.mainpage.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.ROOT;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.mainpage.dao.BlockMainPageDao;
import com.turkcell.blockmail.util.CalendarUtil;
import com.turkcell.blockmail.util.DaoUtil;

@Repository
public class BlockMainPageDaoImpl implements BlockMainPageDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Document> getLastBlocks(long skip, int limit, String segment) throws NullPointerException {
		
		List<Document> result = new ArrayList<>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 30);
		long pastTimeMilis = calendar.getTimeInMillis();
		
		System.out.println("PastTimeMilis: " + pastTimeMilis);
		
		long endTimeMilis = CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis());
		
		System.out.println("EndTimeMilis: " + endTimeMilis);

		MatchOperation matchOperation = new MatchOperation(where("status").exists(true).and("startDate").gte(pastTimeMilis));
		
		GroupOperation groupOperation = new GroupOperation(Fields.fields("createDate","blockDesc","blockName","status","startDate","endDate"))
				.first("createDate").as("createDate")
				.push(ROOT).as("block");
		SortOperation sortOperation = new SortOperation(Sort.by(Direction.DESC, Aggregation.previousOperation(), "createDate" ));
		
		TypedAggregation<BlockInfoDocumentInput> aggregation = DaoUtil.getAggretionQuery(pastTimeMilis, endTimeMilis, skip, limit, matchOperation, sortOperation, groupOperation, segment);
		
		
		AggregationResults<Document> resultList = mongoTemplate.aggregate(aggregation, Document.class);
		
		result = DaoUtil.parseWeekendFromBlockList(resultList.getMappedResults());
		
		
		
		return result;
	}


}
