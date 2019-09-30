package com.turkcell.blockmail.util.schedular.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.turkcell.blockmail.daterange.service.BlockDateRangeService;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.save.service.BlockSaveService;
import com.turkcell.blockmail.util.mail.document.ReportMailModelDocument;
import com.turkcell.blockmail.util.mail.service.BlockSaveMailService;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;
import com.turkcell.blockmail.util.schedular.service.SchedularReportMailService;

@Service
public class SchedularReportMailServiceImpl implements SchedularReportMailService {
	
	@Autowired
	private BlockSendMailService blockSendMailService;
	
	@Autowired
	private BlockSaveMailService blockSaveMailService;
	
	@Autowired
	private BlockSaveService blockSaveService;
	
	@Autowired
	private BlockDateRangeService blockDateRangeService;

	@Override
	@Scheduled(cron = "0 30 16 * * MON-FRI")
	public void sendEndDayReportMonFriday() {
		blockSendMailService.sendEndDayReportMail();
	}

	@Override
	@Scheduled(cron = "0 30 15 * * MON-FRI")
	public void sendEndDayWarningMail() {
		
		
		// TODO sadece segmentlerin bulunduğu bir document oluştur. Daha sonra buradan onları çek ve onları gönder bu şekilde gönderirsen duplicate mailler gider. 
		
		List<ReportMailModelDocument> reportMailList = blockSaveMailService.getMailInformationViaRole("TEAM");
		
		
		for(ReportMailModelDocument rs: reportMailList) {
		
			List<BlockInfoDocumentInput> activeList = blockSaveService.getActiveBlockList(rs.getSegment());
			List<Document> closedList;
			
			if(!activeList.isEmpty()) {
				blockSendMailService.senEndDayWarningMail(activeList, rs);
			} else {
				closedList = blockDateRangeService.getBlockOfParameter(System.currentTimeMillis(), System.currentTimeMillis(), rs.getSegment());
				if(closedList.isEmpty()) {
					blockSendMailService.sendEndDayNoBlockMail(rs);
				} else {
					blockSendMailService.sendEndDayExistBlockMail(closedList.size(), rs);
				}
			}
		}
	}

}
