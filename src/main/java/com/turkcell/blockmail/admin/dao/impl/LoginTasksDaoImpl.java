package com.turkcell.blockmail.admin.dao.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.admin.dao.LoginTasksDao;
import com.turkcell.blockmail.model.UserInformationModel;


@Repository
public class LoginTasksDaoImpl implements LoginTasksDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<UserInformationModel> getWaitUsers() {
		Query query = new Query(Criteria.where("isApproved").is(false))
				.with(Sort.by(Direction.DESC, "createdDate"));
		return mongoTemplate.find(query, UserInformationModel.class);
	}

	@Override
	public void deleteUser(ObjectId id) {
		mongoTemplate.remove(new Query(Criteria.where("id").is(id)), UserInformationModel.class);
	}

	@Override
	public void applyUser(ObjectId id, String applyUserName) {
		mongoTemplate.findAndModify(new Query(Criteria.where("id").is(id)), new Update().set("isApproved", true).set("appliedUser", applyUserName), new FindAndModifyOptions().upsert(false).returnNew(false).remove(false), UserInformationModel.class);
	}

}
