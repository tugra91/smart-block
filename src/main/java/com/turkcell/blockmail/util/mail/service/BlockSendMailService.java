package com.turkcell.blockmail.util.mail.service;

import java.util.List;

import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.threadService.model.HealthCheckServiceModel;
import com.turkcell.blockmail.util.mail.document.ReportMailModelDocument;

public interface BlockSendMailService {
	
	public void sendEndDayReportMail();
	
	public void sendExampleMail();
	
	public void senEndDayWarningMail(List<BlockInfoDocumentInput> blockList, ReportMailModelDocument reportMail);
	
	public void sendEndDayNoBlockMail(ReportMailModelDocument reportMail);
	
	public void sendEndDayExistBlockMail(int blockCount, ReportMailModelDocument reportMail);
	
//	public void sendBlockSaveMail(BigInteger id, String blockName, String blockDesc);
//	
//	public void sendBlockUpdateMail(BlockInfoDocumentInput input);
	
	public void sendApplyUserMail(UserInformationModel userInfo);
	
	public void sendDeleteUserMail(UserInformationModel userInfo);
	
	public void sendRegisterUserMail(UserInformationModel userInfo);
	
	public void sendDeleteBlockMail(BlockInfoDocumentInput blockInfo, UserInformationModel blockOwnerInfo, UserInformationModel deleteUserInfo);
	
	public void sendAdminMailForInformation(UserInformationModel userInfo, List<UserInformationModel> adminList);

	void sendServiceFaultMail(String role, HealthCheckServiceModel serviceModel, String faultMessage);

	void sendServiceRerunnigMail(String role, HealthCheckServiceModel serviceModel );


}
