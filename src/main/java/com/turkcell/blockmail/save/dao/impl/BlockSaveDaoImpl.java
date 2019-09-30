package com.turkcell.blockmail.save.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.math.BigInteger;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockUpdateInformationModel;
import com.turkcell.blockmail.save.dao.BlockSaveDao;


@Repository
public class BlockSaveDaoImpl implements BlockSaveDao {


	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public BlockInfoDocumentInput getFindByOneBlock(ObjectId id) {
		Query query = new Query(where("id").is(id));
		return mongoTemplate.findOne(query, BlockInfoDocumentInput.class);
	}

	@Override
	public void saveBlock(BlockInfoDocumentInput input) {
		//		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		//		BlockDateControlDocument dateControlInput = new BlockDateControlDocument();
		//		dateControlInput.setDate(sdf.format(new Date(System.currentTimeMillis())));
		try {
			mongoTemplate.insert(input);
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void deleteBlock(ObjectId id) throws Exception {
		Query query = new Query(Criteria.where("id").is(id));
		mongoTemplate.findAndRemove(query, BlockInfoDocumentInput.class);
	}

	@Override
	public BlockInfoDocumentInput updateBlock(BigInteger id, BlockUpdateInformationModel input, boolean status) {
		Query query = new Query()
				.addCriteria(where("id").is(id));

		Update update = new Update()
				.set("updateInfo", input)
				.set("status", status);

		FindAndModifyOptions options = new FindAndModifyOptions()
				.upsert(false)
				.returnNew(true)
				.remove(false);

		return mongoTemplate.findAndModify(query, update, options,BlockInfoDocumentInput.class);
	}

	@Override
	public BlockInfoDocumentInput getFindBlockByCreateDate(long createDate) {
		Query query = new Query(where("createDate").is(createDate));
		return mongoTemplate.findOne(query, BlockInfoDocumentInput.class);
	}

	@Override
	public BlockInfoDocumentInput updateBlockTemp(ObjectId id, long endDate, String endBlockUser) {
		Query query = new Query()
				.addCriteria(where("id").is(id));

		Update update = new Update()
				.set("endDate", endDate)
				.set("endBlockUser", endBlockUser)
				.set("status", false);


		FindAndModifyOptions options = new FindAndModifyOptions()
				.upsert(false)
				.returnNew(true)
				.remove(false);

		return mongoTemplate.findAndModify(query, update, options,BlockInfoDocumentInput.class);
	}

	@Override
	public List<BlockInfoDocumentInput> getActiveBlockList(String segment) {
		Query query = new Query().addCriteria(where("status").is(true).and("segment").is(segment));
		return mongoTemplate.find(query, BlockInfoDocumentInput.class);
	}

	@Override
	public List<BlockInfoDocumentInput> getFindByServiceId(ObjectId id) {
		return mongoTemplate.find(new Query(where("serviceId").is(id).and("status").is(true)), BlockInfoDocumentInput.class);
	}


}
