package com.turkcell.blockmail.admin.controller.impl;

import java.math.BigInteger;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.admin.controller.ServiceRegisterController;
import com.turkcell.blockmail.admin.service.ServiceRegisterService;
import com.turkcell.blockmail.document.ServiceRegisterDocument;
import com.turkcell.blockmail.model.ServiceAdminUserInputModel;
import com.turkcell.blockmail.model.ServiceRegisterDetailInput;
import com.turkcell.blockmail.model.ServiceRegisterDetailOutput;
import com.turkcell.blockmail.model.ServiceRegisterListOutput;
import com.turkcell.blockmail.model.ServiceRegisterTestOutput;

@RestController
@RequestMapping("/admin")
public class ServiceRegisterControllerImpl implements ServiceRegisterController {
	
	@Autowired
	private ServiceRegisterService serviceRegisterService;

	@Override
	@RequestMapping(value = "/serviceRegisterSave")
	public void saveOrUpdate(@RequestBody ServiceRegisterDocument input) {
		serviceRegisterService.saveOrUpdate(input);
		
	}

	@Override
	@RequestMapping(value = "/getOneService")
	public ServiceRegisterDocument getOneService(@RequestParam("id") BigInteger id) {
		return serviceRegisterService.getOneService(id);
	}

	@Override
	@RequestMapping(value = "/getAllServices")
	public ServiceRegisterListOutput getAllService() {
		return serviceRegisterService.getAllServices();
	}

	@Override
	@RequestMapping(value = "/serviceTest")
	public ServiceRegisterTestOutput serviceTest(@RequestBody ServiceRegisterDocument input) {
		return serviceRegisterService.serviceTest(input);
	}

	@Override
	@RequestMapping(value = "/getServiceDetail")
	public ServiceRegisterDetailOutput getServiceDetail(@RequestBody ServiceRegisterDetailInput input) {
		return serviceRegisterService.getServiceDetail(input);
	}

	@Override
	@RequestMapping(value = "/deneme")
	public String deneme(Principal princapal) {
		// TODO Auto-generated method stub
		return "HELLO WORLD";
	}

	@Override
	public String saveUser(ServiceAdminUserInputModel user) {
		return "Süper";
	}
	
	

}
