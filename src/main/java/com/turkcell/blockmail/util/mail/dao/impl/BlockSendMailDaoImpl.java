package com.turkcell.blockmail.util.mail.dao.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockDateControlDocument;
import com.turkcell.blockmail.util.mail.dao.BlockSendMailDao;
import com.turkcell.blockmail.util.mail.document.ReportMailModelDocument;

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

	@Override
	public List<ReportMailModelDocument> getMailInformation() throws Exception {
		return mongoTemplate.findAll(ReportMailModelDocument.class);
	}

	@Override
	public ReportMailModelDocument getMailInformationViaSegment(String segment) throws Exception {
		return mongoTemplate.find(new Query().addCriteria(Criteria.where("segment").is(segment)), ReportMailModelDocument.class).get(0);
	}

	@Override
	public ReportMailModelDocument getMailInformationViaSegmentAndRole(String segment, String role) {
		return mongoTemplate.find(new Query().addCriteria(Criteria.where("segment").is(segment).and("role").is(role)), ReportMailModelDocument.class).get(0);
	}

	@Override
	public List<ReportMailModelDocument> getMailInformationViaRole(String role) throws Exception {
		return mongoTemplate.find(new Query().addCriteria(Criteria.where("role").is(role)), ReportMailModelDocument.class);
	}

	@Override
	public void saveMailInformation(ReportMailModelDocument input) throws Exception {
		mongoTemplate.save(input);
	}

	@Override
	public void deleteMailInformation(ObjectId id) throws Exception {
		mongoTemplate.findAndRemove(new Query().addCriteria(Criteria.where("id").is(id)), ReportMailModelDocument.class);
	}

	

}
