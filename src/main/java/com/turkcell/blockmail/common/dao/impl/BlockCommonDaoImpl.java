package com.turkcell.blockmail.common.dao.impl;

import java.util.Calendar;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.common.dao.BlockCommonDao;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.util.CalendarUtil;
import com.turkcell.blockmail.util.DaoUtil;

@Repository
public class BlockCommonDaoImpl implements BlockCommonDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<BlockSystemListDocument> getBlockSystemList() {
		return mongoTemplate.findAll(BlockSystemListDocument.class);
	}

	@Override
	public List<BlockTypeListDocument> getBlockTypeList() {
		return mongoTemplate.findAll(BlockTypeListDocument.class);
	}

	@Override
	public void saveBlockSystem(BlockSystemListDocument input) {
		try {
			mongoTemplate.insert(input);
		}catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	public void saveBlockType(BlockTypeListDocument input) {
		try {
			mongoTemplate.insert(input);
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public double getBlockHours(ObjectId id) {
		
		MatchOperation matchOperation = new MatchOperation(Criteria.where("id").is(id));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		TypedAggregation<BlockInfoDocumentInput> aggregation = DaoUtil.getAggretionQuery(calendar.getTimeInMillis(),CalendarUtil.endTimeMilisInWorkTime(System.currentTimeMillis()) , 0, 0, matchOperation, null,null);
		
		AggregationResults<Document> resultList = mongoTemplate.aggregate(aggregation, Document.class);
		
		List<Document> result = DaoUtil.parseWeekendFromBlockList(resultList.getMappedResults());
		
		double blockHours = !result.isEmpty() ? result.get(0).getDouble("blockHours") : 0;
		
		return blockHours;
	}

}
