package com.turkcell.blockmail.util.mail.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.turkcell.blockmail.util.mail.document.ReportMailModelDocument;


public interface BlockSendMailDao {
	
	public List<String> getDateControlList();
	
	public List<ReportMailModelDocument> getMailInformation() throws Exception;
	
	public ReportMailModelDocument getMailInformationViaSegment(String segment) throws Exception;

	ReportMailModelDocument getMailInformationViaSegmentAndRole(String segment, String role);
	
	public List<ReportMailModelDocument> getMailInformationViaRole(String role) throws Exception;
	
	public void saveMailInformation(ReportMailModelDocument input) throws Exception;
	
	public void deleteMailInformation(ObjectId id) throws Exception;
	
}
