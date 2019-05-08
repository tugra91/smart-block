package com.turkcell.blockmail.admin.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.admin.dao.ServiceRegisterDao;
import com.turkcell.blockmail.document.ServiceRegisterDocument;

@Repository
public class ServiceRegisterDaoImpl implements ServiceRegisterDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void saveOrUpdate(ServiceRegisterDocument input) {
		mongoTemplate.save(input);
	}

	@Override
	public ServiceRegisterDocument getOneService(BigInteger id) {
		return mongoTemplate.findById(id, ServiceRegisterDocument.class);
	}

	@Override
	public List<ServiceRegisterDocument> getAllService() {
		return mongoTemplate.findAll(ServiceRegisterDocument.class);
	}

}
