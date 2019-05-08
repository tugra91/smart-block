package com.turkcell.blockmail.admin.controller;

import java.math.BigInteger;
import java.security.Principal;

import org.springframework.security.core.userdetails.User;

import com.turkcell.blockmail.document.ServiceRegisterDocument;
import com.turkcell.blockmail.model.ServiceAdminUserInputModel;
import com.turkcell.blockmail.model.ServiceRegisterDetailInput;
import com.turkcell.blockmail.model.ServiceRegisterDetailOutput;
import com.turkcell.blockmail.model.ServiceRegisterListOutput;
import com.turkcell.blockmail.model.ServiceRegisterTestOutput;

public interface ServiceRegisterController {
	
	
	public void saveOrUpdate(ServiceRegisterDocument input);
	
	public ServiceRegisterDocument getOneService(BigInteger id);
	
	public ServiceRegisterListOutput getAllService();
	
	public ServiceRegisterTestOutput serviceTest(ServiceRegisterDocument input);
	
	public ServiceRegisterDetailOutput getServiceDetail(ServiceRegisterDetailInput input);
	
	public String deneme(Principal princapal);
	
	public String saveUser (ServiceAdminUserInputModel user);
	
	
	
}
