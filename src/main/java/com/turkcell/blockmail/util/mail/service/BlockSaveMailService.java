package com.turkcell.blockmail.util.mail.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.model.BlockGenericResultOutput;
import com.turkcell.blockmail.model.BlockSaveMailInformationInput;
import com.turkcell.blockmail.util.mail.document.ReportMailModelDocument;

public interface BlockSaveMailService {
	
	public BlockGenericResultOutput saveMailInformatin(BlockSaveMailInformationInput input);
	
	public BlockGenericResultOutput deleteMailInformation(ObjectId id);
	
	public List<ReportMailModelDocument> getAllMailInformation();

	List<ReportMailModelDocument> getMailInformationViaRole(String role);

}
