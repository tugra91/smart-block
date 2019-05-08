package com.turkcell.blockmail.util.thread.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.util.DaoUtil;
import com.turkcell.blockmail.util.thread.dao.LastBlockThreadDao;

@Repository
public class LastBlockThreadDaoImpl implements LastBlockThreadDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public long getLastCreateDate() {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "createDate")).limit(1);
		return mongoTemplate.findOne(query, BlockInfoDocumentInput.class).getCreateDate();
	}

	@Override
	public List<Document> fetchLastAddedBlock(long createDate) {
		
		MatchOperation matchOperation = new MatchOperation(where("createDate").gt(createDate));
	
		TypedAggregation<BlockInfoDocumentInput> aggregation = DaoUtil.getLastBlocks(matchOperation, 0l, 0);
		
		AggregationResults<Document> resultList = mongoTemplate.aggregate(aggregation, Document.class);
		
		return resultList.getMappedResults();
//		Query query = new Query(where("createDate").gt(createDate))
//				.with(new Sort(Sort.Direction.DESC, "createDate"));
//		return mongoTemplate.find(query, BlockInfoDocumentInput.class);
	}

}
