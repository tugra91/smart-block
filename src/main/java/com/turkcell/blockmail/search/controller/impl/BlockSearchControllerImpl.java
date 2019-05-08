package com.turkcell.blockmail.search.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.model.BlockSearchInput;
import com.turkcell.blockmail.model.BlockSearchOutput;
import com.turkcell.blockmail.search.controller.BlockSearchController;
import com.turkcell.blockmail.search.service.BlockSearchService;

@RestController
public class BlockSearchControllerImpl implements BlockSearchController {
	
	@Autowired
	private BlockSearchService blockSearchService;

	@Override
	@RequestMapping(value = "/searchBlock")
	public BlockSearchOutput searchBlock(@RequestParam("limit") int limit, @RequestParam("skip") long skip, 
			@RequestBody BlockSearchInput searchInput) {
		BlockSearchOutput output = new BlockSearchOutput();
		output.setSearchOutputList(blockSearchService.searchBlock(limit, skip, searchInput));
		return output;
	}

}
