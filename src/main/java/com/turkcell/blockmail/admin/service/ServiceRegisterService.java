package com.turkcell.blockmail.admin.service;

import java.math.BigInteger;

import com.turkcell.blockmail.document.ServiceRegisterDocument;
import com.turkcell.blockmail.model.ServiceAdminUserInputModel;
import com.turkcell.blockmail.model.ServiceRegisterDetailInput;
import com.turkcell.blockmail.model.ServiceRegisterDetailOutput;
import com.turkcell.blockmail.model.ServiceRegisterListOutput;
import com.turkcell.blockmail.model.ServiceRegisterTestOutput;

public interface ServiceRegisterService {
	
	public void saveOrUpdate(ServiceRegisterDocument input);
	
	public ServiceRegisterDocument getOneService(BigInteger id);
	
	public ServiceRegisterListOutput getAllServices();
	
	public ServiceRegisterTestOutput serviceTest(ServiceRegisterDocument input);
	
	public ServiceRegisterDetailOutput getServiceDetail(ServiceRegisterDetailInput input);
	
	public void saveUser (ServiceAdminUserInputModel user);
	
}
