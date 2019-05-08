package com.turkcell.blockmail.search.service;

import java.util.List;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockSearchInput;

public interface BlockSearchService {
	
	public List<BlockInfoDocumentInput> searchBlock(int limit, long skip, BlockSearchInput searchInput);
}
