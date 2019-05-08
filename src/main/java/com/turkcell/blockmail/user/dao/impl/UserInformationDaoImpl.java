package com.turkcell.blockmail.user.dao.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.user.dao.UserInformationDao;

@Repository
public class UserInformationDaoImpl implements UserInformationDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<BlockInfoDocumentInput> getUserBlockList(ObjectId objectId) {
		
		Query query = new Query(Criteria.where("userId").is(objectId));
		return mongoTemplate.find(query, BlockInfoDocumentInput.class);
	}



}
