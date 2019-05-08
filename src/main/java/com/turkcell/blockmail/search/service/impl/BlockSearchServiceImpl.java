package com.turkcell.blockmail.search.service.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockSearchInput;
import com.turkcell.blockmail.search.dao.BlockSearchDao;
import com.turkcell.blockmail.search.service.BlockSearchService;

@Service
public class BlockSearchServiceImpl implements BlockSearchService {
	
	
	@Autowired
	private BlockSearchDao blockSearchDao;

	@Override
	public List<BlockInfoDocumentInput> searchBlock(int limit, long skip, BlockSearchInput searchInput) {

		Gson gson = new Gson();
		Document searchInputDocument = Document.parse(gson.toJson(searchInput, BlockSearchInput.class));



		long startDate = searchInput.getStartDate();
		long endDate = searchInput.getEndDate();
		List<String> criteriaList = new ArrayList<>();

		TextCriteria blockNameCriteria = null;
		Query query = null;

		if(StringUtils.isNotEmpty(searchInput.getSearchText())) {
			blockNameCriteria = TextCriteria.forDefaultLanguage()
					.caseSensitive(false).diacriticSensitive(false)
					.matchingAny("\""+searchInput.getSearchText()+"\"");

			query = TextQuery.queryText(blockNameCriteria).sortByScore();
		} else {
			query = new Query().with(new Sort(Sort.Direction.DESC, "createDate"));
		}

		for(Map.Entry<String, Object> rs: searchInputDocument.entrySet()) {
			if(StringUtils.isNotEmpty(String.valueOf(rs.getValue())) 
					&& !StringUtils.equalsIgnoreCase(rs.getKey(), "searchText")
					&& !StringUtils.isNumeric(String.valueOf(rs.getValue()))) {
				criteriaList = Arrays.asList(String.valueOf(rs.getValue()).split(","));
				query.addCriteria(where(rs.getKey()).in(criteriaList));
			}
		}

		if(startDate != 0 ) {
			query.addCriteria(where("startDate").gte(startDate));
		}

		if(endDate != 0) {
			query.addCriteria(where("endDate").lte(endDate));
		}

		query.limit(limit).skip(skip);


		return blockSearchDao.searchBlock(limit, skip, query);
	}


}
