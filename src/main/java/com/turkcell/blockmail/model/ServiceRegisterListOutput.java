package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

import com.turkcell.blockmail.document.ServiceRegisterDocument;


public class ServiceRegisterListOutput implements Serializable {

	private static final long serialVersionUID = 2213622484713341427L;
	
	private List<ServiceRegisterDocument> serviceList;

	public List<ServiceRegisterDocument> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<ServiceRegisterDocument> serviceList) {
		this.serviceList = serviceList;
	}
	
	

}
