package com.turkcell.blockmail.util.mail.dao.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockDateControlDocument;
import com.turkcell.blockmail.util.mail.dao.BlockSendMailDao;

@Repository
public class BlockSendMailDaoImpl implements BlockSendMailDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<String> getDateControlList() {
		List<String> result = new ArrayList<>();
		TypedAggregation<BlockDateControlDocument> aggregation = newAggregation(BlockDateControlDocument.class,
				group("date"));
		try {
			AggregationResults<String> returnList = mongoTemplate.aggregate(aggregation, String.class);
			result = returnList.getMappedResults();
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}

}
