package com.turkcell.blockmail.search.controller;

import com.turkcell.blockmail.model.BlockSearchInput;
import com.turkcell.blockmail.model.BlockSearchOutput;

public interface BlockSearchController {
	
	public BlockSearchOutput searchBlock(int limit,long skip,BlockSearchInput searchInput);
}
