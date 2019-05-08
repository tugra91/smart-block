package com.turkcell.blockmail.admin.dao;

import java.math.BigInteger;
import java.util.List;

import com.turkcell.blockmail.document.ServiceRegisterDocument;

public interface ServiceRegisterDao {

	
	public void saveOrUpdate(ServiceRegisterDocument input);
	
	public ServiceRegisterDocument getOneService(BigInteger id);
	
	public List<ServiceRegisterDocument> getAllService();
	
}
