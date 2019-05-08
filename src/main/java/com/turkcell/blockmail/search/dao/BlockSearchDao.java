package com.turkcell.blockmail.search.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;

public interface BlockSearchDao {
	
	public List<BlockInfoDocumentInput> searchBlock(int limit, long skip, Query query);
}
