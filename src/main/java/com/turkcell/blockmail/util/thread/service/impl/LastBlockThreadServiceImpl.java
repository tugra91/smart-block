package com.turkcell.blockmail.util.thread.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.blockmail.util.thread.dao.LastBlockThreadDao;
import com.turkcell.blockmail.util.thread.service.LastBlockThreadService;

@Service
public class LastBlockThreadServiceImpl implements LastBlockThreadService {
	
	
	@Autowired
	private LastBlockThreadDao lastBlockThreadDao;

	@Override
	public boolean isAddNewBlock(long lastCreatedDate) {
		boolean result = false;

		long newCreatedDate = lastBlockThreadDao.getLastCreateDate();
		
		if(lastCreatedDate < newCreatedDate) {
			result = true;
		}

		return result;
	}

	

	@Override
	public List<Document> fetchLastAddedBlocks(long createDate) {
//		Type listType = new TypeToken<List<Document>>() {}.getType();
//		List<Document> result = new Gson().fromJson(new Gson().toJson(lastBlockThreadDao.fetchLastAddedBlock(createDate)), listType);
		return lastBlockThreadDao.fetchLastAddedBlock(createDate);
	}

}
