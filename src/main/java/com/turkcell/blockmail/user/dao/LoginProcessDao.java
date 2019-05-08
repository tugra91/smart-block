package com.turkcell.blockmail.user.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;

import com.turkcell.blockmail.document.AccessTokenInformationDocument;
import com.turkcell.blockmail.model.UserInformationModel;

public interface LoginProcessDao {
	
	public void saveUser(UserInformationModel userModel) throws Exception;
	public UserInformationModel updateUser(UserInformationModel userModel, Update update) throws Exception;
	public UserInformationModel findByUsername(String username);
	public UserInformationModel findByEmail(String email);
	public UserInformationModel findById(ObjectId id);
	public List<UserInformationModel> findAllAdmin();
	public void saveAccessToken(AccessTokenInformationDocument accessTokenDocument) throws Exception;
	public AccessTokenInformationDocument getAccessToken(String accessToken) throws Exception;
	public void deleteOldAccessToken(String accessToken) throws Exception;
	
	

}
