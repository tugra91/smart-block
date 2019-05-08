package com.turkcell.blockmail.search.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.search.dao.BlockSearchDao;

@Repository
public class BlockSearchDaoImpl implements BlockSearchDao{
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<BlockInfoDocumentInput> searchBlock(int limit, long skip, Query query) {
		
		TextIndexDefinition indexDefinition = TextIndexDefinition.builder()
				.onField("blockName", 3F)
				.onField("blockDesc", 2F)
				.onField("openBlockUser", 1F).build();
		mongoTemplate.indexOps(BlockInfoDocumentInput.class).ensureIndex(indexDefinition);
	
		return mongoTemplate.find(query, BlockInfoDocumentInput.class);
	}

}
