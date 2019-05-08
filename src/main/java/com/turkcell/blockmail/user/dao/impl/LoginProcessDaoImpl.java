package com.turkcell.blockmail.user.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.AccessTokenInformationDocument;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.user.dao.LoginProcessDao;

@Repository
public class LoginProcessDaoImpl implements LoginProcessDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void saveUser(UserInformationModel userModel) throws Exception {
		try {
			mongoTemplate.insert(userModel);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public UserInformationModel updateUser(UserInformationModel userModel, Update update) throws Exception {
		try {

			Query query = new Query(Criteria.where("id").is(userModel.getId()));
			
			FindAndModifyOptions options = new FindAndModifyOptions();
			options.upsert(false);
			options.returnNew(true);
			options.remove(false);
			return mongoTemplate.findAndModify(query, update, options, UserInformationModel.class);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public UserInformationModel findByUsername(String username) {
		Query query = new Query(Criteria.where("username").is(username)).limit(1);	
		List<UserInformationModel> userList = new ArrayList<>();
		userList = mongoTemplate.find(query, UserInformationModel.class);
		return userList.isEmpty() ? null : userList.get(0);
	}
	
	@Override
	public UserInformationModel findByEmail(String email) {
		Query query = new Query(Criteria.where("email").is(email)).limit(1);	
		List<UserInformationModel> userList = new ArrayList<>();
		userList = mongoTemplate.find(query, UserInformationModel.class);
		return userList.isEmpty() ? null : userList.get(0);
	}
	
	@Override
	public List<UserInformationModel> findAllAdmin() {
		Query query = new Query(Criteria.where("roles").in("ROLE_ADMIN"));
		List<UserInformationModel> userList =  mongoTemplate.find(query, UserInformationModel.class);
		return userList;
	}

	@Override
	public UserInformationModel findById(ObjectId id) {
		Query query = new Query(Criteria.where("id").is(id)).limit(1);	
		List<UserInformationModel> userList = new ArrayList<>();
		userList = mongoTemplate.find(query, UserInformationModel.class);
		return userList.isEmpty() ? null : userList.get(0);
	}

	@Override
	public void saveAccessToken(AccessTokenInformationDocument accessTokenDocument) {
		try {
			mongoTemplate.insert(accessTokenDocument);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	public AccessTokenInformationDocument getAccessToken(String accessToken) throws Exception {
		Query query = new Query(Criteria.where("accessToken").is(accessToken));
		AccessTokenInformationDocument result = null;
		try {
			result = mongoTemplate.findOne(query, AccessTokenInformationDocument.class);
		} catch (Exception e) {
			throw e;
		}
		return result;
		
	}

	@Override
	public void deleteOldAccessToken(String accessToken) throws Exception {
		Query query = new Query(Criteria.where("accessToken").is(accessToken));
		try {
			mongoTemplate.findAndRemove(query, AccessTokenInformationDocument.class);
		} catch (Exception e) {
			throw e;
		}
		
	}

	

	

}
