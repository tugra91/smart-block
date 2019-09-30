package com.turkcell.blockmail.util.mail.service.impl;

import java.util.List;

import org.apache.axis.utils.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.turkcell.blockmail.model.BlockGenericResultOutput;
import com.turkcell.blockmail.model.BlockSaveMailInformationInput;
import com.turkcell.blockmail.util.mail.dao.BlockSendMailDao;
import com.turkcell.blockmail.util.mail.document.ReportMailModelDocument;
import com.turkcell.blockmail.util.mail.service.BlockSaveMailService;

@Service
public class BlockSaveMailServiceImpl implements BlockSaveMailService {
	
	
	@Autowired
	private BlockSendMailDao blockSendMailDao;
	

	@Override
	public BlockGenericResultOutput saveMailInformatin(BlockSaveMailInformationInput input) {
	
		BlockGenericResultOutput output = new BlockGenericResultOutput();
		
		
		if(StringUtils.isEmpty(input.getSegment()) 
				|| StringUtils.isEmpty(input.getRole()) 
				|| CollectionUtils.isEmpty(input.getToList())) {
			output.setSuccess(false);
			output.setErrorLog("Lütfen Segment, Rol ve To listesini eksiksiz doldurunuz.");
		} else {
		
		
			ReportMailModelDocument documentInput = new ReportMailModelDocument();
			documentInput.setSegment(input.getSegment());
			documentInput.setToList(input.getToList());
			documentInput.setCcList(input.getCcList());
			documentInput.setRole(input.getRole());
			
			
			try {
				blockSendMailDao.saveMailInformation(documentInput);
				output.setSuccess(true);
				output.setResult("BAŞARILI");
			} catch (Exception e) {
				output.setSuccess(false);
				output.setErrorLog(e.getMessage());
			}
		
		}
		
		return output;
	}


	@Override
	public BlockGenericResultOutput deleteMailInformation(ObjectId id) {
		BlockGenericResultOutput output = new BlockGenericResultOutput();
		
		if(id == null) {
			output.setErrorLog("Lütfen geçerli bir id giriniz...");
			output.setSuccess(false);
		} else {
			try {
				blockSendMailDao.deleteMailInformation(id);
				output.setSuccess(true);
			} catch (Exception e) {
				output.setErrorLog(e.getMessage());
				output.setSuccess(false);
			}
		}
		
		return output;
	}


	@Override
	public List<ReportMailModelDocument> getAllMailInformation() {
		try {
			return blockSendMailDao.getMailInformation();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ReportMailModelDocument> getMailInformationViaRole(String role) {
		try {
			return blockSendMailDao.getMailInformationViaRole(role);
		} catch (Exception e) {
			return null;
		}
	}

}
