package com.turkcell.blockmail.save.service.impl;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.BlockTempUpdateOutput;
import com.turkcell.blockmail.model.BlockUpdateInformationModel;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.save.dao.BlockSaveDao;
import com.turkcell.blockmail.save.service.BlockSaveService;
import com.turkcell.blockmail.user.service.LoginProcessService;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;

@Service
public class BlockSaveServiceImpl implements BlockSaveService {
	
	
	@Autowired
	private BlockSaveDao blockSaveDao;
	
	@Autowired
	private BlockSendMailService blockSendMailService;
	
	@Autowired
	private LoginProcessService loginPageService;

	@Override
	public BlockInfoDocumentInput getBlockById(ObjectId id) {
		return blockSaveDao.getFindByOneBlock(id);
	}

	@Override
	public BlockInfoOutput saveBlock(BlockInfoDocumentInput input) {

		BlockInfoOutput output = new BlockInfoOutput();
		input.setSegment("MARKETING_SOLUTION");

		if(input.getServiceId() != null) {
			UserInformationModel userInfo = loginPageService.getUserInformation("AUTOBLOCK");
			input.setUserId(userInfo.getId());
			input.setOpenBlockUser(userInfo.getName() + " " + userInfo.getSurname());
		}

		try {
			if(input instanceof BlockInfoDocumentInput) {
				if(!StringUtils.isEmpty(input.getBlockName()) 
						&& !StringUtils.isEmpty(input.getBlockDesc()) 
						&& !StringUtils.isEmpty(input.getBlockSystem()) 
						&& !StringUtils.isEmpty(input.getBlockType())
						&& !StringUtils.isEmpty(input.getOpenBlockUser())
						&& !StringUtils.isEmpty(input.getAffectSystem())
						&& !StringUtils.isEmpty(input.getAffectEnvironment())
						&& !StringUtils.isEmpty(input.getUserId().toHexString())
						&& input.getStartDate() != 0
						) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(input.getStartDate());
						if(calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY 
								&& calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY
								&& ((calendar.get(Calendar.HOUR_OF_DAY) == 7 && calendar.get(Calendar.MINUTE) >= 30) 
									|| (calendar.get(Calendar.HOUR_OF_DAY) > 7 && calendar.get(Calendar.HOUR_OF_DAY) < 16)
									|| (calendar.get(Calendar.HOUR_OF_DAY) == 16 && calendar.get(Calendar.MINUTE) <= 30)) 
								&&	input.getStartDate() < System.currentTimeMillis()) {
									input.setCreateDate(System.currentTimeMillis());
									blockSaveDao.saveBlock(input);
//												blockSendMailService.sendBlockSaveMail(id, input.getBlockName(), input.getBlockDesc());
									output.setResult("Success");
									output.setStatus(true);
						} else {
							output.setResult("Lütfen doğru tarih aralıklarında giriş yapınız.");
							output.setStatus(false);
						}
					} else {
						output.setResult("Lütfen Gerekli Tüm Alanları Doldurunuz");
						output.setStatus(false);
					}
			} else {
				output.setResult("Lütfen Doğru Input Modelliyle Giriş Yapınız.");
				output.setStatus(false);
			}
		}catch (Exception e) {
			output.setResult(e.getMessage());
			output.setStatus(false);
		}
		return output;
	}

	@Override
	public void updateBlockMail(BigInteger id,BlockUpdateInformationModel updateInformationModel) {
		
//		boolean status = false;
//		
//		if(updateInformationModel.isRedirectDiffTeam()) {
//			status = true;
//		}
		
//		BlockInfoDocumentInput getUpdateDocument = blockSaveDao.updateBlock(id, updateInformationModel, status);
		
		/* Call Send Mail Function */
//		blockSendMailService.sendBlockUpdateMail(getUpdateDocument);
	}

	@Override
	public BlockTempUpdateOutput updateBlockMailTemp(ObjectId id, long endDate, String endBlockUser) {
		BlockTempUpdateOutput output = new BlockTempUpdateOutput();
		Gson gson = new Gson();
		Calendar calendar = Calendar.getInstance();
		if(endDate == 0) {
			calendar.setTimeInMillis(System.currentTimeMillis());
			if(calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY) {
				if(calendar.get(Calendar.HOUR_OF_DAY)>=17 && calendar.get(Calendar.HOUR_OF_DAY)<=23) {
					calendar.set(Calendar.HOUR_OF_DAY, 16);
					calendar.set(Calendar.MINUTE, 30);
				} else if(calendar.get(Calendar.HOUR_OF_DAY) == 16 && calendar.get(Calendar.MINUTE) > 30) {
					calendar.set(Calendar.HOUR_OF_DAY, 16);
					calendar.set(Calendar.MINUTE, 30);
				} else if(calendar.get(Calendar.HOUR_OF_DAY) >= 0 && calendar.get(Calendar.HOUR_OF_DAY) <=6) {
					calendar.set(Calendar.HOUR_OF_DAY, 7);
					calendar.set(Calendar.MINUTE, 30);
				} else if(calendar.get(Calendar.HOUR_OF_DAY) == 7 && calendar.get(Calendar.MINUTE) < 30 ) {
					calendar.set(Calendar.HOUR_OF_DAY, 7);
					calendar.set(Calendar.MINUTE, 30);
				}
				endDate = calendar.getTimeInMillis();
			} else {
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				calendar.set(Calendar.HOUR_OF_DAY, 16);
				calendar.set(Calendar.MINUTE, 30);
				endDate = calendar.getTimeInMillis();
			}
		}
		
		UserInformationModel userInfo = loginPageService.getUserInformation(endBlockUser);
		String userNameSurname = userInfo.getName() + " " + userInfo.getSurname(); 
		BlockInfoDocumentInput result = blockSaveDao.updateBlockTemp(id, endDate, userNameSurname);
		Document convertModel = gson.fromJson(gson.toJson(result), Document.class);
		
		output.setBlockDetail(convertModel);
		return output;
		
	}

	@Override
	public BlockInfoOutput deleteBlock(ObjectId id, String deleteBlockUser) {
		BlockInfoOutput output = new BlockInfoOutput();
		UserInformationModel deleteUserInfo = loginPageService.getUserInformation(deleteBlockUser);
		BlockInfoDocumentInput blockInfo = getBlockById(id);
		UserInformationModel blockOwnerInfo = loginPageService.getUserInformationWithId(blockInfo.getUserId());
		try {
			blockSaveDao.deleteBlock(id);
			output.setStatus(true);
			if(!StringUtils.equalsIgnoreCase(blockInfo.getUserId().toHexString(), deleteUserInfo.getId().toHexString())) {
				blockSendMailService.sendDeleteBlockMail(blockInfo, blockOwnerInfo, deleteUserInfo);
			}
		} catch (Exception e) {
			output.setResult("Bloku silemedik sistemsel bir hata oluştu lütfen Tuğra Er ile iletişime geçin");
			output.setStatus(false);
		}
		
		return output;
	}

	@Override
	public List<BlockInfoDocumentInput> getActiveBlockList(String segment) {
		return blockSaveDao.getActiveBlockList(segment);
	}

	@Override
	public List<BlockInfoDocumentInput> getBlockByServiceId(ObjectId serviceId) {
		return blockSaveDao.getFindByServiceId(serviceId);
	}

}
