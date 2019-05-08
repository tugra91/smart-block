package com.turkcell.blockmail.admin.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.blockmail.admin.dao.LoginTasksDao;
import com.turkcell.blockmail.admin.service.LoginTasksService;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.user.service.LoginProcessService;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;

@Service
public class LoginTasksServiceImpl implements LoginTasksService {

	@Autowired
	private LoginTasksDao loginTasksDao;

	@Autowired
	private LoginProcessService loginPageService;

	@Autowired
	private BlockSendMailService blockSendMailService;


	@Override
	public List<UserInformationModel> getWaitUsers(String username) {
		UserInformationModel infoModel = loginPageService.getUserInformation(username);
		if(infoModel != null && infoModel.getRoles().stream().anyMatch(s -> StringUtils.equalsIgnoreCase(s, "ROLE_ADMIN"))) {
			return loginTasksDao.getWaitUsers();
		}
		return null;
	}


	@Override
	public BlockInfoOutput deleteUser(UserInformationModel user) {
		BlockInfoOutput output = new BlockInfoOutput();
		try {
			UserInformationModel existUser = loginPageService.getUserInformationWithId(user.getId());
			if(existUser.isApproved() || user.isApproved()) {
				output.setResult("Bu kullanıcıyı sizden önce başka bir sistem yöneticisi onaylanmış o yüzden silinemez. Bir dahakine daha hızlı olacağına inanıyorum üzülme :)");
				output.setStatus(false);
				return output;
			}
			loginTasksDao.deleteUser(user.getId());
			output = sendDeleteOrApplyEmail(false, user);
		} catch (Exception e) {
			output.setStatus(false);
			output.setResult("Silme İşlemi Başarılı Olamadı. Lütfen Daha Sonra Tekrar Deneyiniz. ");
		}

		return output;
	}


	@Override
	public BlockInfoOutput applyuser(UserInformationModel user, String applyUserName) {
		BlockInfoOutput output = new BlockInfoOutput();
		try {
			UserInformationModel existUser = loginPageService.getUserInformationWithId(user.getId());
			if(existUser.isApproved() || user.isApproved()) {
				output.setResult("Bu kullanıcıyı sizden önce başka bir sistem yöneticisi onaylanmış tekrardan onaylanaMAz. Bir dahakine daha hızlı olacağına inanıyorum üzülme :)");
				output.setStatus(false);
				return output;
			}
			loginTasksDao.applyUser(user.getId(), applyUserName);
			output = sendDeleteOrApplyEmail(true, user);
		} catch (Exception e) {
			output.setResult("Onaylama işlemi başarılı olamadı. Lütfen Daha Sonra Tekrar Deneyiniz");
			output.setStatus(false);
		}
		return output;
	}


	private BlockInfoOutput sendDeleteOrApplyEmail(boolean isApply, UserInformationModel user) {
		BlockInfoOutput output = new BlockInfoOutput();
		try {
			if(isApply) {
				blockSendMailService.sendApplyUserMail(user);
				output.setResult("Başarıyla Kullanıcı Onaylandı.");
				output.setStatus(true);
			} else {
				blockSendMailService.sendDeleteUserMail(user);
				output.setResult("Başarıyla Kullanıcı Silindi.");
				output.setStatus(true);
			}
		} catch (Exception e) {
			output.setResult("Kullanıcı BAŞARIYLA silindi fakat mail gönderimiyle ilgili sorunlar yaşıyorum bu yüzden kullanıcıya bilgilendirme maili gönderilemedi. ");
			output.setStatus(true);
		}
		return output;
	}


}
