package com.turkcell.blockmail.util.mail.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.bson.Document;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.turkcell.blockmail.daterange.service.BlockDateRangeService;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.model.BlockPieChartInfoModel;
import com.turkcell.blockmail.model.BlockPieChartOutput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.util.mail.dao.BlockSendMailDao;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

@Service
public class BlockSendMailServiceImpl implements BlockSendMailService {
	
	@Autowired
	private BlockDateRangeService blockDateRangeService;
	
	@Autowired
	private BlockSendMailDao blockSendMailDao;

	private Gson gson = new Gson();

	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	private SimpleDateFormat sdfWithHour = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	List<String> dateControlList = new ArrayList<>();


	private static final int CHART_WIDTH = 400;
	private static final int CHART_HEIGHT = 400;
	private static final float CHART_RESOLUTION = 1f;
	
	private long diffBlocks;
	private long countBlockDay;
	private long workDay;
	private long totalBlockNumber;
	private long totalBlockHours;
	
	private ByteArrayOutputStream bytePieChartToday;
	private ByteArrayOutputStream bytePieChartWeek;
	private ByteArrayOutputStream bytePieChartMonth;

	
	private EmailMessage getClientMessage() {
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials(StringUtils.newStringUtf8(Base64.getDecoder().decode("Example")), 
				StringUtils.newStringUtf8(Base64.getDecoder().decode("Example")), StringUtils.newStringUtf8(Base64.getDecoder().decode("Example")));
		service.setCredentials(credentials);
		try {
			service.setUrl(new URI("https://mail.example.com/ews/exchange.asmx"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		EmailMessage message = null;
		try {
			message = new EmailMessage(service);
			message.setFrom(new EmailAddress("SMART-BLOCK@example.com"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	
	@Override
	public void sendExampleMail() {
		EmailMessage message = getClientMessage();
		try {
			message.getToRecipients().add("example@example.com");
			MessageBody body = new MessageBody();
			body.setBodyType(BodyType.HTML);
			body.setText(this.createMailTemplate());
			message.setBody(body);
			message.send();
		} catch(Exception e) {
			
		}
	}

	@Override
	public void sendEndDayReportMail() {
		EmailMessage message = getClientMessage();
		try {
			
			

			
			message.getToRecipients().add("example@example.com");
		
			
			message.setSubject("XL-Blok Günlük Rapor - " + sdf.format(new Date(System.currentTimeMillis())));

			
	
			
			MessageBody body = new MessageBody();
			body.setBodyType(BodyType.HTML);
			body.setText(this.createMailTemplate());
			message.setBody(body);			
			
			FileAttachment inLineBlockTodayFile = message.getAttachments().addFileAttachment("today", getBytePieChartToday().toByteArray());
			inLineBlockTodayFile.setContentType("image/jpeg");
			inLineBlockTodayFile.setContentId("<pie-today>");
			inLineBlockTodayFile.setIsInline(true);
			
			FileAttachment inLineBlockWeekFile = message.getAttachments().addFileAttachment("week", getBytePieChartWeek().toByteArray());
			inLineBlockWeekFile.setContentType("image/jpeg");
			inLineBlockWeekFile.setContentId("<pie-week>");
			inLineBlockWeekFile.setIsInline(true);
			
			FileAttachment inLineBlockMonthFile = message.getAttachments().addFileAttachment("month", getBytePieChartMonth().toByteArray());
			inLineBlockMonthFile.setContentType("image/jpeg");
			inLineBlockMonthFile.setContentId("pie-month>");
			inLineBlockMonthFile.setIsInline(true);
			
			message.send();
		} catch (MessagingException e) {
			System.out.println(e);
		} catch (IOException e ) {
			System.out.println(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void senEndDayWarningMail(List<BlockInfoDocumentInput> blockList) {
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("<style type='text/css'> ");
		body.append(".tg  {border-collapse:collapse;border-spacing:0;} ");
		body.append(".tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;} ");
		body.append(".tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;} ");
		body.append(".tg .tg-juju{font-family:'Courier New', Courier, monospace !important;;text-align:left;vertical-align:top} ");
		body.append(".tg .tg-0lax{text-align:left;vertical-align:top} ");
		body.append("</style> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Merhabalar, </p> ");
		body.append("<p>Aşağıdaki bloklar hala devam etmektedir. Eğer kapanmış bloklarınız varsa kapatırsanız seviniriz. :) </p>");
		body.append("<p>Yaklaşık 1 saat sonra gün sonu XL-Blok maili üst yönetime raporlanacaktır. Bilginize.</p>");
		body.append("<br>");
		body.append("<table class='tg'> ");
		body.append("<tr> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>#</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Blok İsmi</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Blok Açıklaması</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Başlangıç Tarihi</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Ortam</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Bloklayan Sistem</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Etkilenen Sistem</span></th> ");
		body.append("<th class='tg-juju'><span style='font-weight:bold'>Açan Kullanıcı</span></th> ");
		body.append("</tr>  ");
		
		int i = 1;
		for(BlockInfoDocumentInput block : blockList) {
			String blockDesc = block.getBlockDesc().length() > 160 ? block.getBlockDesc().substring(0, 160) + "..." : block.getBlockDesc();
			body.append("<tr> ");
			body.append("<td class='tg-0lax'>"+i+"</td> ");
			body.append("<td class='tg-0lax'>"+Jsoup.parse(block.getBlockName()).text()+"</td> ");
			body.append("<td class='tg-0lax'>"+Jsoup.parse(blockDesc).text()+"</td> "); 
			body.append("<td class='tg-0lax'>"+sdfWithHour.format(new Date(block.getStartDate()))+"</td> ");
			body.append("<td class='tg-0lax'>"+block.getAffectEnvironment()+"</td> ");
			body.append("<td class='tg-0lax'>"+block.getBlockSystem()+"</td> ");
			body.append("<td class='tg-0lax'>"+block.getAffectSystem()+"</td> ");
			body.append("<td class='tg-0lax'>"+block.getOpenBlockUser()+"</td> ");
			body.append("</tr> ");
			i++;
		}
		body.append("</table> ");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");
		
		try {
			
			message.getToRecipients().add("example");
			message.getToRecipients().add("example");
			
			message.setSubject("SMART-BLOCK - Aktif Bloklar Hatırlatma");
			
			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			
			message.send();

		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendEndDayNoBlockMail() {
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Selamlar, </p> ");
		body.append("<p>SMART-Block olarak bugün aktif bloklarınız olmadığını görüyorum. </p>");
		body.append("<p>Eğer gerçekten XL-Block-larınız yoksa bu çok güzel sevindim ama blok oluşmuş ve hala giriş yapmadıysanız kırılırım :(  </p>");
		body.append("<p><b>Lütfen bugün bir blok yaşadıysanız giriş yapın çünkü yaklaşık 1 saat sonra bugünün blok raporunu gönderilecektir.</b>  </p>");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");
		
		try {
			message.getToRecipients().add("example");
			message.getToRecipients().add("example");
		
			message.setSubject("SMART-BLOCK - Blok Hatırlatma");
		

		
			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			message.send();

		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendEndDayExistBlockMail(int blockCount) {
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Selamlar, </p> ");
		body.append("<p>Bugün toplamda "+blockCount+" tane blok girişi yapıldı.  </p>");
		body.append("<p>Bunun dışında blok girişi yapmak isteyen varsa, son çağrı. Çünkü yaklaşık 1 saat sonra bugünün blok raporu üst yönetime raporlanacak. Bilginiz olsun :)  </p>");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");
		
		try {
			message.getToRecipients().add("example");
			message.getToRecipients().add("example");
		
			message.setSubject("SMART-BLOCK - Blok Hatırlatma");
		

		
			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			message.send();

		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void sendApplyUserMail(UserInformationModel userInfo) {
		
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Merhaba "+ userInfo.getName() + ", </p> ");
		body.append("<p>SMART-Block' a hoşgeldin üyeliğin onaylandı. Artık kullanıcı adın ve şifren ile SMART-BLOCK'a giriş yapıp BLOK girişlerini yapabilirsin.</p>");
		body.append("<p>Yaptığın blok girişlerin her iş günü, <b>gün sonunda raporlanacaktır.</b> Bu yüzden açtığın blokların eğer kapanırsa sistemden de bloklarını <b>kapatmayı unutma.</b> :)</p>");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");
		
		try {
			message.getToRecipients().add(userInfo.getEmail());
			
			message.setSubject("SMART-BLOCK - Üyeliğin Onaylandı :)");
			
			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			message.send();


		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void sendDeleteUserMail(UserInformationModel userInfo) {
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Merhaba "+ userInfo.getName() + ", </p> ");
		body.append("<p> Maalesef SMART-BLOCK'a üyeliğin reddedildi. Bunun için üzgünüz. </p>");
		body.append("<p>Eğer bir yanlışlık olduğunu düşünüyorsan lütfen <b>TEAM-SQUADTERFI-DEV</b> ile iletişime geçin. </p>");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");

		try {
			message.getToRecipients().add(userInfo.getEmail());
			message.setSubject("SMART-BLOCK - Üyeliğin Reddedildi :(");

			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			message.send();


		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void sendRegisterUserMail(UserInformationModel userInfo) {
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Merhaba "+ userInfo.getName() + ", </p> ");
		body.append("<p>SMART-Block'a kaydın başarıyla tamamlandı. </p>");
		body.append("<p>Şimdi sıra birazcık beklemekte. Sistem Yöneticileri üyeliğini onayladığı takdirde SMART-Block üzerinden seni engelleyen her şeyi raporlayalamaya başlayacaksın. </p>");
		body.append("<p>Üyeliğin <b>onaylansanda, onaylanMAsada</b> sana bilgilendirme maili göndereceğiz. Görüşmek üzere :)  </p>");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");

		try {
			message.getToRecipients().add(userInfo.getEmail());
			message.setSubject("SMART-BLOCK - Kaydın Oluşturuldu");

			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			message.send();


		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void sendAdminMailForInformation(UserInformationModel userInfo, List<UserInformationModel> adminList) {
		
		for(UserInformationModel admin : adminList) {
			EmailMessage message = getClientMessage();
			StringBuilder body = new StringBuilder();
			body.append("<html>");
			body.append("<head> ");
			body.append("</head> ");
			body.append("<body> ");
			body.append("<p>Merhaba "+ admin.getName() + ", </p> ");
			body.append("<p>Sana müjdeli bir haberimiz var. </p>");
			body.append("<p>Smart-Block'a yeni bir üye daha kayıt oldu adını merak ettiğini biliyorum. O yüzden hemen ismini söylüyorum: İsmi: "+userInfo.getName()+" Soyismi: "+userInfo.getSurname()+" </p>");
			body.append("<p>Email adresi ise şöyle: "+userInfo.getEmail()+"</p>");
			body.append("<p>Şimdilik bu kadar bilgi yeter. Daha fazlasını merak ediyorsan hemen SMART-Block'a gir ve kullanıcıyı incele</p>");
			body.append("<p>Eğer onaylamak istersen <b>Onayla</b> butonuna basman yeterli umarım onaylarsın :)</p>");
			body.append("<br>");
			body.append("<p>İyi Çalışmalar</p>");
			body.append("<p><b>SMART-BLOCK</b></p>");
			body.append("</body> ");
			body.append("</html> ");
	
			try {
				message.getToRecipients().add(admin.getEmail());
				message.setSubject("SMART-BLOCK - Yeni Üye Onayı");
				
				MessageBody mBody = new MessageBody();
				mBody.setBodyType(BodyType.HTML);
				mBody.setText(body.toString());
				message.setBody(mBody);			
				
				message.send();
			
	
	
			} catch (MessagingException e) {
				System.out.println(e);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void sendDeleteBlockMail(BlockInfoDocumentInput blockInfo, UserInformationModel blockOwnerInfo,
			UserInformationModel deleteUserInfo) {
		EmailMessage message = getClientMessage();
		StringBuilder body = new StringBuilder();
		body.append("<html>");
		body.append("<head> ");
		body.append("</head> ");
		body.append("<body> ");
		body.append("<p>Merhaba "+ blockOwnerInfo.getName() + ", </p> ");
		body.append("<p>Aşağıdaki açıkladığım bilgilere sahip BLOK'un <b>" +deleteUserInfo.getName() + " "+deleteUserInfo.getSurname()+"</b> tarafından silinmiştir. </p>");
		body.append("<p>Bilgine. </p>");
		body.append("<p><center>Blok Bilgileri</center></p>");
		body.append("<p> Blok İsmi: <b>"+blockInfo.getBlockName()+"</b></p>");
		body.append("<p> Blok Açıklaması: <b>"+blockInfo.getBlockDesc()+"</b></p>");
		body.append("<p> Blok Yaşatan Sistem: <b>"+blockInfo.getBlockSystem()+"</b></p>");
		body.append("<p> Blok YaşaYAn Sistem: <b>"+blockInfo.getAffectEnvironment()+"</b></p>");
		body.append("<p> Blok Ortam: <b>"+blockInfo.getAffectSystem()+"</b></p>");
		body.append("<p> Blok Tipi: <b>"+blockInfo.getBlockType()+"</b></p>");
		body.append("<p> Blok Başlangıç Tarihi: <b>"+sdfWithHour.format(new Date(blockInfo.getStartDate()))+"</b></p>");
		body.append("<br>");
		body.append("<p>İyi Çalışmalar</p>");
		body.append("<p><b>SMART-BLOCK</b></p>");
		body.append("</body> ");
		body.append("</html> ");

		try {
			message.getToRecipients().add(blockOwnerInfo.getEmail());
			message.getCcRecipients().add(deleteUserInfo.getEmail());

			message.setSubject("SMART-BLOCK - Blokunuz Bir Başkası Tarafından Silindi");

			MessageBody mBody = new MessageBody();
			mBody.setBodyType(BodyType.HTML);
			mBody.setText(body.toString());
			message.setBody(mBody);			
			
			message.send();

		} catch (MessagingException e) {
			System.out.println(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String createMailTemplate() throws JsonParseException, JsonMappingException, IOException {
		Calendar calendar = Calendar.getInstance();
		List<Document> getBlocksToday = blockDateRangeService.getBlockToday(false,0,0);
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		List<Document> getXLBlockForWeek = blockDateRangeService.getBlockWeek(calendar.getTimeInMillis(), false, 0,0);
		calendar.clear();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int thisMonthNumber = calendar.get(Calendar.MONTH);
		long thisMonthWorkDay = passingTimeBeetweenTwoDate(calendar.getTimeInMillis());
		List<Document> getXLBlockThisMonth = blockDateRangeService.getBlockMonth(calendar.getTimeInMillis(), false,0,0);
		calendar.add(Calendar.MONTH, -1);
		int lastMonthNumber = calendar.get(Calendar.MONTH);
		long lastMonthWorkDay = passingTimeBeetweenTwoDate(calendar.getTimeInMillis());
		List<Document> getXLBlockLastMonth = blockDateRangeService.getBlockMonth(calendar.getTimeInMillis(),false,0,0);
		calendar.add(Calendar.MONTH, -1);
		int twoMonthAgoNumber = calendar.get(Calendar.MONTH);
		long twoMonthAgoWorkDay = passingTimeBeetweenTwoDate(calendar.getTimeInMillis());
		List<Document> getXLBlockTwoMonthAgo = blockDateRangeService.getBlockMonth(calendar.getTimeInMillis(),false,0,0);
		
		
		
		

		dateControlList = blockSendMailDao.getDateControlList();


		BlockPieChartOutput pieOutputToday = blockDateRangeService.getBlockPiechartInfoAsBlockList(getBlocksToday, "");
		BlockPieChartOutput pieOutputWeek = blockDateRangeService.getBlockPiechartInfoAsBlockList(getXLBlockForWeek, "");
		BlockPieChartOutput pieOutputMonth = blockDateRangeService.getBlockPiechartInfoAsBlockList(getXLBlockThisMonth, "");

		DefaultPieDataset pieDataSetToday = this.generatePieDataset(pieOutputToday);
		DefaultPieDataset pieDataSetWeek = this.generatePieDataset(pieOutputWeek);
		DefaultPieDataset pieDataSetMonth = this.generatePieDataset(pieOutputMonth);

		

		JFreeChart blockChartToday = ChartFactory.createPieChart3D("XL-BLOCK Günlük", pieDataSetToday, true, true, false);
		JFreeChart blockChartWeek = ChartFactory.createPieChart3D("XL-BLOCK Haftalık", pieDataSetWeek, true, true, false);
		JFreeChart blockChartMonth = ChartFactory.createPieChart3D("XL-BLOCK Aylık", pieDataSetMonth, true, true, false);


		Color bgColor = new Color(248, 249, 250);


		PieSectionLabelGenerator generator = new PieSectionLabelGenerator() {

			@Override
			public String generateSectionLabel(PieDataset dataset, Comparable key) {

				float totalResult = 0.0f;

				for(Object rs:dataset.getKeys()) {
					Double value = (Double)dataset.getValue((String)rs);
					float fValue = value.floatValue();
					totalResult = totalResult +  fValue ;
				}

				Double value = (Double)dataset.getValue(key);
				float fValue = value.floatValue();
				String calculatePercent = String.format("%.2f",(fValue * 100) / totalResult);
				if(org.apache.commons.lang3.StringUtils.equals(calculatePercent, "0.00")) {
					return null;
				} 
				return String.valueOf(key) + " - %" + calculatePercent;
			}

			@Override
			public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
				return null;
			}
		};


		blockChartToday.setBackgroundPaint(bgColor);
		blockChartToday.getTitle().setFont(new Font("'Calibri',Monaco,monospace !important", Font.PLAIN, 20));
		PiePlot3D plotToday = (PiePlot3D) blockChartToday.getPlot();
		plotToday.setNoDataMessage("Veri olmadığından Grafik Oluşturulmadı.");
		plotToday.setCircular(false);
		plotToday.setLabelGenerator(generator);
		plotToday.setBackgroundPaint(bgColor);
		plotToday.setOutlinePaint(bgColor);
		plotToday.setOutlineVisible(false);
		plotToday.setLabelFont(new Font("'Calibri',Monaco,monospace !important", Font.ITALIC, 12));
		plotToday.setLabelBackgroundPaint(bgColor);
		for(Object rs:pieDataSetToday.getKeys()) {
			String key = String.valueOf(rs);
			plotToday.setSectionPaint(key, new Color((int)(Math.random() * 0x1000000)));
		}
		
		blockChartWeek.setBackgroundPaint(bgColor);
		blockChartWeek.getTitle().setFont(new Font("'Calibri',Monaco,monospace !important", Font.PLAIN, 20));
		PiePlot3D plotWeek = (PiePlot3D) blockChartWeek.getPlot();
		plotWeek.setNoDataMessage("Veri olmadığından Grafik Oluşturulmadı.");
		plotWeek.setLabelGap(0.02);
		plotWeek.setCircular(false);
		plotWeek.setLabelGenerator(generator);
		plotWeek.setBackgroundPaint(bgColor);
		plotWeek.setOutlinePaint(bgColor);
		plotWeek.setOutlineVisible(false);
		plotWeek.setLabelFont(new Font("'Calibri',Monaco,monospace !important", Font.ITALIC, 12));
		plotWeek.setLabelBackgroundPaint(bgColor);
		for(Object rs:pieDataSetWeek.getKeys()) {
			String key = String.valueOf(rs);
			plotWeek.setSectionPaint(key, new Color((int)(Math.random() * 0x1000000)));
		}
		
		
		blockChartMonth.setBackgroundPaint(bgColor);
		blockChartMonth.getTitle().setFont(new Font("'Calibri',Monaco,monospace !important", Font.PLAIN, 20));
		PiePlot3D plotMonth = (PiePlot3D) blockChartMonth.getPlot();
		plotMonth.setNoDataMessage("Veri olmadığından Grafik Oluşturulmadı.");
		plotMonth.setLabelGap(0.02);
		plotMonth.setCircular(false);
		plotMonth.setLabelGenerator(generator);
		plotMonth.setBackgroundPaint(bgColor);
		plotMonth.setOutlinePaint(bgColor);
		plotMonth.setOutlineVisible(false);
		plotMonth.setLabelFont(new Font("'Calibri',Monaco,monospace !important", Font.ITALIC, 12));
		plotMonth.setLabelBackgroundPaint(bgColor);
		for(Object rs:pieDataSetMonth.getKeys()) {
			String key = String.valueOf(rs);
			plotMonth.setSectionPaint(key, new Color((int)(Math.random() * 0x1000000)));
		}

		ByteArrayOutputStream byteBlockChartToday = new ByteArrayOutputStream();
		ByteArrayOutputStream byteBlockChartWeek = new ByteArrayOutputStream();
		ByteArrayOutputStream byteBlockChartMonth = new ByteArrayOutputStream();

		ChartUtils.writeChartAsJPEG(byteBlockChartToday, CHART_RESOLUTION, blockChartToday, CHART_WIDTH	, CHART_HEIGHT);
		ChartUtils.writeChartAsJPEG(byteBlockChartWeek, CHART_RESOLUTION, blockChartWeek, CHART_WIDTH	, CHART_HEIGHT);
		ChartUtils.writeChartAsJPEG(byteBlockChartMonth, CHART_RESOLUTION, blockChartMonth, CHART_WIDTH	, CHART_HEIGHT);
		
		setBytePieChartToday(byteBlockChartToday);
		setBytePieChartWeek(byteBlockChartWeek);
		setBytePieChartMonth(byteBlockChartMonth);

		StringBuilder builder = new StringBuilder();
		builder.append("	<html xmlns=\"http://www.w3.org/1999/xhtml\"> ");
		builder.append("	<head> ");
		builder.append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> ");
		builder.append("	<style type=\"text/css\"> ");
		builder.append("	.ExternalClass{width:100%}.ExternalClass,.ExternalClass p,.ExternalClass span,.ExternalClass font,.ExternalClass td,.ExternalClass div{line-height:150%}a{text-decoration:none}@media screen and (max-width: 600px){table.row th.col-1,table.row th.col-2,table.row th.col-3,table.row th.col-4,table.row th.col-5,table.row th.col-6,table.row th.col-7,table.row th.col-8,table.row th.col-9,table.row th.col-10,table.row th.col-11,table.row th.col-12{display:block;width:100% !important}.d-mobile{display:block !important}.d-desktop{display:none !important}.w-lg-25{width:auto !important}.w-lg-25>tbody>tr>td{width:auto !important}.w-lg-50{width:auto !important}.w-lg-50>tbody>tr>td{width:auto !important}.w-lg-75{width:auto !important}.w-lg-75>tbody>tr>td{width:auto !important}.w-lg-100{width:auto !important}.w-lg-100>tbody>tr>td{width:auto !important}.w-lg-auto{width:auto !important}.w-lg-auto>tbody>tr>td{width:auto !important}.w-25{width:25% !important}.w-25>tbody>tr>td{width:25% !important}.w-50{width:50% !important}.w-50>tbody>tr>td{width:50% !important}.w-75{width:75% !important}.w-75>tbody>tr>td{width:75% !important}.w-100{width:100% !important}.w-100>tbody>tr>td{width:100% !important}.w-auto{width:auto !important}.w-auto>tbody>tr>td{width:auto !important}.p-lg-0>tbody>tr>td{padding:0 !important}.pt-lg-0>tbody>tr>td,.py-lg-0>tbody>tr>td{padding-top:0 !important}.pr-lg-0>tbody>tr>td,.px-lg-0>tbody>tr>td{padding-right:0 !important}.pb-lg-0>tbody>tr>td,.py-lg-0>tbody>tr>td{padding-bottom:0 !important}.pl-lg-0>tbody>tr>td,.px-lg-0>tbody>tr>td{padding-left:0 !important}.p-lg-1>tbody>tr>td{padding:0 !important}.pt-lg-1>tbody>tr>td,.py-lg-1>tbody>tr>td{padding-top:0 !important}.pr-lg-1>tbody>tr>td,.px-lg-1>tbody>tr>td{padding-right:0 !important}.pb-lg-1>tbody>tr>td,.py-lg-1>tbody>tr>td{padding-bottom:0 !important}.pl-lg-1>tbody>tr>td,.px-lg-1>tbody>tr>td{padding-left:0 !important}.p-lg-2>tbody>tr>td{padding:0 !important}.pt-lg-2>tbody>tr>td,.py-lg-2>tbody>tr>td{padding-top:0 !important}.pr-lg-2>tbody>tr>td,.px-lg-2>tbody>tr>td{padding-right:0 !important}.pb-lg-2>tbody>tr>td,.py-lg-2>tbody>tr>td{padding-bottom:0 !important}.pl-lg-2>tbody>tr>td,.px-lg-2>tbody>tr>td{padding-left:0 !important}.p-lg-3>tbody>tr>td{padding:0 !important}.pt-lg-3>tbody>tr>td,.py-lg-3>tbody>tr>td{padding-top:0 !important}.pr-lg-3>tbody>tr>td,.px-lg-3>tbody>tr>td{padding-right:0 !important}.pb-lg-3>tbody>tr>td,.py-lg-3>tbody>tr>td{padding-bottom:0 !important}.pl-lg-3>tbody>tr>td,.px-lg-3>tbody>tr>td{padding-left:0 !important}.p-lg-4>tbody>tr>td{padding:0 !important}.pt-lg-4>tbody>tr>td,.py-lg-4>tbody>tr>td{padding-top:0 !important}.pr-lg-4>tbody>tr>td,.px-lg-4>tbody>tr>td{padding-right:0 !important}.pb-lg-4>tbody>tr>td,.py-lg-4>tbody>tr>td{padding-bottom:0 !important}.pl-lg-4>tbody>tr>td,.px-lg-4>tbody>tr>td{padding-left:0 !important}.p-lg-5>tbody>tr>td{padding:0 !important}.pt-lg-5>tbody>tr>td,.py-lg-5>tbody>tr>td{padding-top:0 !important}.pr-lg-5>tbody>tr>td,.px-lg-5>tbody>tr>td{padding-right:0 !important}.pb-lg-5>tbody>tr>td,.py-lg-5>tbody>tr>td{padding-bottom:0 !important}.pl-lg-5>tbody>tr>td,.px-lg-5>tbody>tr>td{padding-left:0 !important}.p-0>tbody>tr>td{padding:0 !important}.pt-0>tbody>tr>td,.py-0>tbody>tr>td{padding-top:0 !important}.pr-0>tbody>tr>td,.px-0>tbody>tr>td{padding-right:0 !important}.pb-0>tbody>tr>td,.py-0>tbody>tr>td{padding-bottom:0 !important}.pl-0>tbody>tr>td,.px-0>tbody>tr>td{padding-left:0 !important}.p-1>tbody>tr>td{padding:4px !important}.pt-1>tbody>tr>td,.py-1>tbody>tr>td{padding-top:4px !important}.pr-1>tbody>tr>td,.px-1>tbody>tr>td{padding-right:4px !important}.pb-1>tbody>tr>td,.py-1>tbody>tr>td{padding-bottom:4px !important}.pl-1>tbody>tr>td,.px-1>tbody>tr>td{padding-left:4px !important}.p-2>tbody>tr>td{padding:8px !important}.pt-2>tbody>tr>td,.py-2>tbody>tr>td{padding-top:8px !important}.pr-2>tbody>tr>td,.px-2>tbody>tr>td{padding-right:8px !important}.pb-2>tbody>tr>td,.py-2>tbody>tr>td{padding-bottom:8px !important}.pl-2>tbody>tr>td,.px-2>tbody>tr>td{padding-left:8px !important}.p-3>tbody>tr>td{padding:16px !important}.pt-3>tbody>tr>td,.py-3>tbody>tr>td{padding-top:16px !important}.pr-3>tbody>tr>td,.px-3>tbody>tr>td{padding-right:16px !important}.pb-3>tbody>tr>td,.py-3>tbody>tr>td{padding-bottom:16px !important}.pl-3>tbody>tr>td,.px-3>tbody>tr>td{padding-left:16px !important}.p-4>tbody>tr>td{padding:24px !important}.pt-4>tbody>tr>td,.py-4>tbody>tr>td{padding-top:24px !important}.pr-4>tbody>tr>td,.px-4>tbody>tr>td{padding-right:24px !important}.pb-4>tbody>tr>td,.py-4>tbody>tr>td{padding-bottom:24px !important}.pl-4>tbody>tr>td,.px-4>tbody>tr>td{padding-left:24px !important}.p-5>tbody>tr>td{padding:48px !important}.pt-5>tbody>tr>td,.py-5>tbody>tr>td{padding-top:48px !important}.pr-5>tbody>tr>td,.px-5>tbody>tr>td{padding-right:48px !important}.pb-5>tbody>tr>td,.py-5>tbody>tr>td{padding-bottom:48px !important}.pl-5>tbody>tr>td,.px-5>tbody>tr>td{padding-left:48px !important}.s-lg-1>tbody>tr>td,.s-lg-2>tbody>tr>td,.s-lg-3>tbody>tr>td,.s-lg-4>tbody>tr>td,.s-lg-5>tbody>tr>td{font-size:0 !important;line-height:0 !important;height:0 !important}.s-0>tbody>tr>td{font-size:0 !important;line-height:0 !important;height:0 !important}.s-1>tbody>tr>td{font-size:4px !important;line-height:4px !important;height:4px !important}.s-2>tbody>tr>td{font-size:8px !important;line-height:8px !important;height:8px !important}.s-3>tbody>tr>td{font-size:16px !important;line-height:16px !important;height:16px !important}.s-4>tbody>tr>td{font-size:24px !important;line-height:24px !important;height:24px !important}.s-5>tbody>tr>td{font-size:48px !important;line-height:48px !important;height:48px !important}}@media yahoo{.d-mobile{display:none !important}.d-desktop{display:block !important}.w-lg-25{width:25% !important}.w-lg-25>tbody>tr>td{width:25% !important}.w-lg-50{width:50% !important}.w-lg-50>tbody>tr>td{width:50% !important}.w-lg-75{width:75% !important}.w-lg-75>tbody>tr>td{width:75% !important}.w-lg-100{width:100% !important}.w-lg-100>tbody>tr>td{width:100% !important}.w-lg-auto{width:auto !important}.w-lg-auto>tbody>tr>td{width:auto !important}.p-lg-0>tbody>tr>td{padding:0 !important}.pt-lg-0>tbody>tr>td,.py-lg-0>tbody>tr>td{padding-top:0 !important}.pr-lg-0>tbody>tr>td,.px-lg-0>tbody>tr>td{padding-right:0 !important}.pb-lg-0>tbody>tr>td,.py-lg-0>tbody>tr>td{padding-bottom:0 !important}.pl-lg-0>tbody>tr>td,.px-lg-0>tbody>tr>td{padding-left:0 !important}.p-lg-1>tbody>tr>td{padding:4px !important}.pt-lg-1>tbody>tr>td,.py-lg-1>tbody>tr>td{padding-top:4px !important}.pr-lg-1>tbody>tr>td,.px-lg-1>tbody>tr>td{padding-right:4px !important}.pb-lg-1>tbody>tr>td,.py-lg-1>tbody>tr>td{padding-bottom:4px !important}.pl-lg-1>tbody>tr>td,.px-lg-1>tbody>tr>td{padding-left:4px !important}.p-lg-2>tbody>tr>td{padding:8px !important}.pt-lg-2>tbody>tr>td,.py-lg-2>tbody>tr>td{padding-top:8px !important}.pr-lg-2>tbody>tr>td,.px-lg-2>tbody>tr>td{padding-right:8px !important}.pb-lg-2>tbody>tr>td,.py-lg-2>tbody>tr>td{padding-bottom:8px !important}.pl-lg-2>tbody>tr>td,.px-lg-2>tbody>tr>td{padding-left:8px !important}.p-lg-3>tbody>tr>td{padding:16px !important}.pt-lg-3>tbody>tr>td,.py-lg-3>tbody>tr>td{padding-top:16px !important}.pr-lg-3>tbody>tr>td,.px-lg-3>tbody>tr>td{padding-right:16px !important}.pb-lg-3>tbody>tr>td,.py-lg-3>tbody>tr>td{padding-bottom:16px !important}.pl-lg-3>tbody>tr>td,.px-lg-3>tbody>tr>td{padding-left:16px !important}.p-lg-4>tbody>tr>td{padding:24px !important}.pt-lg-4>tbody>tr>td,.py-lg-4>tbody>tr>td{padding-top:24px !important}.pr-lg-4>tbody>tr>td,.px-lg-4>tbody>tr>td{padding-right:24px !important}.pb-lg-4>tbody>tr>td,.py-lg-4>tbody>tr>td{padding-bottom:24px !important}.pl-lg-4>tbody>tr>td,.px-lg-4>tbody>tr>td{padding-left:24px !important}.p-lg-5>tbody>tr>td{padding:48px !important}.pt-lg-5>tbody>tr>td,.py-lg-5>tbody>tr>td{padding-top:48px !important}.pr-lg-5>tbody>tr>td,.px-lg-5>tbody>tr>td{padding-right:48px !important}.pb-lg-5>tbody>tr>td,.py-lg-5>tbody>tr>td{padding-bottom:48px !important}.pl-lg-5>tbody>tr>td,.px-lg-5>tbody>tr>td{padding-left:48px !important}.s-lg-0>tbody>tr>td{font-size:0 !important;line-height:0 !important;height:0 !important}.s-lg-1>tbody>tr>td{font-size:4px !important;line-height:4px !important;height:4px !important}.s-lg-2>tbody>tr>td{font-size:8px !important;line-height:8px !important;height:8px !important}.s-lg-3>tbody>tr>td{font-size:16px !important;line-height:16px !important;height:16px !important}.s-lg-4>tbody>tr>td{font-size:24px !important;line-height:24px !important;height:24px !important}.s-lg-5>tbody>tr>td{font-size:48px !important;line-height:48px !important;height:48px !important}} ");
		builder.append(".tg  {border-collapse:collapse;border-spacing:0;} ");
		builder.append(".tg td{font-family:Arial, sans-serif;font-size:16px;padding:2px 0px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;} ");
		builder.append(".tg th{font-family:Arial, sans-serif;font-size:16px;font-weight:normal;padding:2px 0px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;} ");
		builder.append(".tg .tg-c3ow{border-color:inherit;text-align:center;vertical-align:top} ");
		builder.append(".tg .tg-yw4l{vertical-align:top} ");
		builder.append("	</style> ");
		builder.append("	</head> ");
		builder.append("	<body style=\"outline: 0; width: 100%; min-width: 100%; height: 100%; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; font-family: Helvetica, Arial, sans-serif; line-height: 24px; font-weight: normal; font-size: 16px; -moz-box-sizing: border-box; -webkit-box-sizing: border-box; box-sizing: border-box; margin: 0; padding: 0; border: 0;\"> ");


		builder.append("	<table valign=\"top\" class=\"bg-light body\" style=\"outline: 0; width: 100%; min-width: 100%; height: 100%; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; font-family: Helvetica, Arial, sans-serif; line-height: 24px; font-weight: normal; font-size: 16px; -moz-box-sizing: border-box; -webkit-box-sizing: border-box; box-sizing: border-box; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin: 0; padding: 0; border: 0;\" bgcolor=\"#f8f9fa\"> ");
		builder.append("	  <tbody> ");
		builder.append("	    <tr> ");
		builder.append("	     <td valign=\"top\" style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; margin: 0;\" align=\"left\" bgcolor=\"#f8f9fa\">	        ");





		builder.append("		<table class=\"container\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
		builder.append("		  <tbody>");
		builder.append("		    <tr> ");
		builder.append("	      <td align=\"left\" style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; margin: 0; padding: 0 16px;\"> ");
		builder.append("	        <!--[if (gte mso 9)|(IE)]> ");
		builder.append("	        <table align=\"left\"> ");
		builder.append("	           <tbody> ");
		builder.append("	              <tr> ");
		builder.append("	                <td width=\"600\"> ");
		builder.append("	        <![endif]--> ");
		builder.append("	        <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin: 0 auto;\"> ");
		builder.append("<tbody> ");
		builder.append(" <tr> ");
		builder.append("	           <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; margin: 0;\" align=\"left\">	        ");



		/* İlk Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		// Row İçerik 
		
		builder.append("	  <table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\"> ");
		builder.append("	 <tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\"> ");
		builder.append("<div> ");
		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("<tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\"> ");
		builder.append("	 <table class=\"table\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%; max-width: 100%; margin-bottom: 16px;\" bgcolor=\"#ffffff\"> ");
		builder.append("	   	<thead class=\"thead-dark\"> ");
		builder.append("	      <tr> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">Sistem</th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">07:00</th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">07:30</th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">08:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">08:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">09:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">09:30</th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">10:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">10:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">11:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">11:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">12:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">12:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">13:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">13:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">14:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">14:30</th> ");
		builder.append("		    <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">15:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">15:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">16:00</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">16:30</th> ");
		builder.append("			<th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">17:00</th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">Durum</th> ");
		builder.append("	      </tr> ");
		builder.append("	    </thead>  ");
		builder.append("	    <tbody> ");


		for(Document rs : getBlocksToday) {


			/* 
			 * Burada iki kere for yapmamak için aşağıdaki XLB-Bugün tablosu için gerekli olan valueları dolduruyoruz.
			 */


			/* 
			 * Document dosyasından gelen datayı POJO ya çevirip tabloyu boyamak için gerekli olan logic yazılıyor.
			 * 
			 */
			BlockInfoDocumentInput blockDetail = gson.fromJson(gson.toJson(rs.get("blockDetail")), BlockInfoDocumentInput.class);
			builder.append("	      <tr> ");
			builder.append("	        <td style=\"border-spacing: 0px; width: 1px; white-space: nowrap; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\"><b>"+ blockDetail.getBlockSystem() +" </b></td> ");

			/* ----- Genel Bilgi------
			 * Tablodaki hücre sayısını 07-17 arası olarak düşünürsek iki değer arasındaki hücre sayısı 21 olarak çıkıyor. 
			 * Aşağıda blokun başladığı ve bittiği saate göre 21 sayısına izdüşümü hesaplanıyor. Bu sayede blokun saatleri ile tablonun üstünde gösterilen saatler denk getirilmiş oluyor.
			 * İlk önce eğer blok saati 07 ve 17 arasında değilse, 07'den küçük saatler 07'ye 17'den büyük saatler 17'ye setleniyor.
			 * Blok dakikası ise 30 ve 00 dakikalarına setleniyor. Eğer 00 - 30 arasındaysa 00'a, 30'a eşit ve büyükse 30'a setleniyor.
			 */
			calendar.setTime(new Date(blockDetail.getStartDate()));
			int startHour = calendar.get(Calendar.HOUR_OF_DAY) >= 16 
					&& calendar.get(Calendar.HOUR_OF_DAY) <= 17 ? 16 : calendar.get(Calendar.HOUR_OF_DAY);
			int startMinute = calendar.get(Calendar.MINUTE) >= 30 ? 30 : 0;
			long startTime = calendar.getTimeInMillis();	

			calendar.setTime(new Date(blockDetail.getEndDate() == 0l ? System.currentTimeMillis() : blockDetail.getEndDate()));
			int endHour = calendar.get(Calendar.HOUR_OF_DAY) > 17 ? 17 : calendar.get(Calendar.HOUR_OF_DAY);
			endHour = endHour < 7 ? 8 : endHour;
			int endMinute = calendar.get(Calendar.MINUTE) >= 30 ? 30 : 0;
			long endTime = calendar.getTimeInMillis();


			/*
			 * Eğer blok bugün'den önce açılmışsa açılış saati default olarak 07 olarak setleniyor.
			 */
			if((endTime - startTime)/3600000 > 24 
					|| startHour < 7) {
				startHour = 7;
				startMinute = 0;
			}

			/*
			 * StartColumnNumber ve EndColumNumber 21 tane column arasında saatlere ve dakikalara göre hangi columların arasının kırmızıya boyanacağını hesaplanıyor.
			 * 7 ve 17 arasında 0.5 artıracak şekilde geziliyor. Eğer saatlerden biri denk gelirse set'lemeler yapılıyor ve tekrar eden bir sonraki saatte girilmemesi için bir flag koyuluyor. Bu flag
			 * startColumNumber'ın ve endColumNumber'ın artışını kontrol etmek içinde kullanılıyor. 
			 */
			int startColumnNumber = 0;
			int endColumnNumber = 0;
			boolean enterStart = false;
			boolean enterEnd = false;
			for(double i = 7; i<= 17; i = i + 0.5 ) {
				if(startHour == Double.valueOf(i).intValue() && !enterStart) {
					startColumnNumber = startMinute == 30 ? startColumnNumber + 1 : startColumnNumber; 
					enterStart = true;
				} 
				if(endHour == Double.valueOf(i).intValue() && !enterEnd) {
					endColumnNumber = endMinute == 30 ? endColumnNumber + 1 : endColumnNumber;
					enterEnd = true;
				}
				if(enterStart && enterEnd) {
					break;
				}
				startColumnNumber = !enterStart ? startColumnNumber + 1 : startColumnNumber;
				endColumnNumber = !enterEnd ? endColumnNumber + 1 : endColumnNumber;
			}

			/*
			 * Bu bölümde ise hesaplanan columNumber'lara göre tablonun hücreleri boyanacak şekilde döngüye giriliyor.
			 */
			for(int i = 0; i<21; i++) {
				if(startColumnNumber <= i && i <= endColumnNumber) {
					builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; background-color: red; border-right-color: white; border-right-width: 1px;border-right-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\"></td> ");
				} else {
					if(i%2 == 0) {
						builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; border-right-color: white;border-right-width: 1px;border-right-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\"></td> ");
					} else {
						builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; background-color: gray; border-right-color: white;border-right-width: 1px;border-right-style: solid; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\"></td> ");
					}
				}
			}

			if(blockDetail.isStatus()) {
				builder.append("	        <td style=\"border-spacing: 0px; width: 1px; white-space: nowrap; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; background-color: red; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">Devam Ediyor</td> ");
			} else {
				builder.append("	        <td style=\"border-spacing: 0px; width: 1px; white-space: nowrap; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; background-color: green; margin: 0; padding: 5px;\" align=\"left\" valign=\"top\">Sonlandı</td> ");
			}


			builder.append("	      </tr> ");
		}


		builder.append("	    </tbody> ");
		builder.append("	  </table> ");
		builder.append("</td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");
		builder.append("</div> ");
		builder.append("</td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");

		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* İlk Row Bitti */

		builder.append("	<br> ");
		builder.append("	<br> ");
		builder.append("	<br> ");



		/* PieChart  Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");


		//Row İçerik Başlıyor
		builder.append("   <th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");
		builder.append("<img src = \"cid:pie-today\"></img>");				
		builder.append("	</th> ");

		builder.append("   <th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");
		builder.append("<img src = \"cid:pie-week\"></img>");				
		builder.append("	</th> ");

		builder.append("   <th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");
		builder.append("<img src = \"cid:pie-month\"></img>");				
		builder.append("	</th> ");

		// Row İçerik Bitti 


		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* PieChart Row Bitti */

		builder.append("	<br> ");
		builder.append("	<br> ");
		builder.append("	<br> ");


		/* İlkinci  Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		// Row İçerik 
		builder.append("	  <table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\"> ");
		builder.append("	  <tbody> ");
		builder.append("	    <tr> ");
		builder.append("	     <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\"> ");
		builder.append("	       <div> ");
		builder.append("	    <table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("	  <tbody> ");
		builder.append("	    <tr> ");
		builder.append("	      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\"> ");
		builder.append("	       <div> ");
		builder.append("	      Aylık Özet ");
		builder.append("	    </div> "); 
		builder.append("	      </td> ");
		builder.append("	    </tr> ");
		builder.append("	  </tbody> ");
		builder.append("	</table> ");
		builder.append("	  </div> ");
		builder.append("	      </td> ");
		builder.append("	    </tr> ");
		builder.append("	  </tbody> ");
		builder.append("	</table> ");

		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* İlkinci Row Bitti */



		/* Üçüncü  Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		// Row İçerik 

		builder.append("	  <table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\"> ");
		builder.append("	  <tbody> ");
		builder.append("	    <tr> ");
		builder.append("	      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\"> ");
		builder.append("	        <div> ");
		builder.append("	   <table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("	  <tbody> ");
		builder.append("	    <tr> ");
		builder.append("	     <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\"> ");
		builder.append("	        <div> ");
		builder.append("	  <table class=\"table\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%; max-width: 100%; margin-bottom: 16px;\" bgcolor=\"#ffffff\"> ");
		builder.append("	    <thead class=\"thead-dark\"> ");
		builder.append("	      <tr> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"></th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>Günlük Blok Süresi</center></th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>Haftalık Blok Süresi</center></th>  "); 
		builder.append("	        <th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>Toplam Blok Süresi</center></th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>Toplam Blok Adeti</center></th> ");
		builder.append("	        <th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>Toplam Gün Sayısı</center></th> ");
		builder.append("	      </tr> ");
		builder.append("	    </thead> ");
		builder.append("	    <tbody> ");
		builder.append("	      <tr> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+this.getMonthName(thisMonthNumber)+"</td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+this.getBlockHoursSum(getBlocksToday)+"</center></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+this.getBlockHoursSum(getXLBlockForWeek)+"</center></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+this.getBlockHoursSum(getXLBlockThisMonth)+"</center></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+getXLBlockThisMonth.size()+"</center> </td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+thisMonthWorkDay+"</center> </td> ");
		builder.append("	      </tr> ");
		builder.append("	      <tr> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+this.getMonthName(lastMonthNumber)+"</td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+this.getBlockHoursSum(getXLBlockLastMonth)+"</center></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"> <center>"+getXLBlockLastMonth.size()+" </center> </td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"> <center>"+lastMonthWorkDay+"</center> </td> ");
		builder.append("	      </tr> ");
		builder.append("	      <tr> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+this.getMonthName(twoMonthAgoNumber)+"</td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"><center>"+this.getBlockHoursSum(getXLBlockTwoMonthAgo)+"</center></td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"> <center>"+getXLBlockTwoMonthAgo.size()+" </center> </td> ");
		builder.append("	        <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\"> <center>"+twoMonthAgoWorkDay+"</center> </td> ");
		builder.append("	      </tr> ");
		builder.append("		    </tbody> ");
		builder.append("	  </table> ");
		builder.append("	    </div> ");
		builder.append("	      </td> ");
		builder.append("	    </tr> ");
		builder.append("	  </tbody> ");
		builder.append("	</table> ");
		builder.append("	  </div> ");
		builder.append("	      </td> ");
		builder.append("	    </tr> ");
		builder.append("	  </tbody> ");
		builder.append("	</table> ");

		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* Üçüncü Row Bitti */

		builder.append("	<br> ");
		builder.append("	<br> ");
		builder.append("	<br> ");



		/* Dördüncü  Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		// Row İçerik 

		builder.append("	  <table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\"> ");
		builder.append("	 <tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\"> ");
		builder.append("<div> ");
		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("<tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\"> ");
		builder.append("<div> ");
		builder.append("          Toplam Özet ");
		builder.append("</div> ");
		builder.append("</td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");
		builder.append("</div> ");
		builder.append("</td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");

		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* Dördüncü Row Bitti */

		calendar.clear();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		calculateSummaryInfo(calendar.getTimeInMillis());
		String summaryStartDate = sdf.format(new Date(calendar.getTimeInMillis()));
		String summaryEndDate = sdf.format(new Date(System.currentTimeMillis()));
		calendar.clear();
	 

		/* Beşinci  Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		// Row İçerik 

		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\"> ");
		builder.append("<tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\"> ");
		builder.append("<div> ");
		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("<tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\"> ");


		builder.append("<div> ");
		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("       Çalışma Gün Sayısı ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("	  "+String.valueOf(getWorkDay())+" Gün ("+summaryStartDate+" - "+summaryEndDate+") ");
		builder.append("</th> ");
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");



		createHr(builder);
		
		

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("	  Çalışma Süresi ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("       " + String.valueOf(getWorkDay() * 8) + " Saat");
		builder.append("</th> ");
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");


		createHr(builder);

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("     Toplam Blok Süresi ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("  "+ String.valueOf(getTotalBlockHours() * 60) + " Dakika |≈ " + String.valueOf(getTotalBlockHours()) + "  ");
		builder.append("</th> ");
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");
		builder.append("<div class=\"hr \" style=\"width: 100%; margin: 20px 0; border: 0;\"> ");
		builder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("<tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #dddddd; border-top-style: solid; height: 1px; width: 100%; margin: 0;\" align=\"left\"></td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");
		builder.append("</div> ");
		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("      Toplam Blok Adedi ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("    " + String.valueOf(getTotalBlockNumber()) + " Adet ");
		builder.append("</th> ");
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");


		createHr(builder);


		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("  Blok Yaşanan Gün Sayısı ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("	  "+String.valueOf(getCountBlockDay())+" Gün ");
		builder.append("</th> ");
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");
		builder.append("<div class=\"hr \" style=\"width: 100%; margin: 20px 0; border: 0;\"> ");
		builder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\"> ");
		builder.append("<tbody> ");
		builder.append("<tr> ");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #dddddd; border-top-style: solid; height: 1px; width: 100%; margin: 0;\" align=\"left\"></td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");
		builder.append("</div> ");
		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("        Blok Yaşanmayan Gün Sayısı ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("            "+String.valueOf(getWorkDay() - getCountBlockDay())+" Gün ");
		builder.append("</th> ");
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");

		createHr(builder);

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append("<thead> ");
		builder.append("<tr> ");
		builder.append("<th class=\"col-2\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 16.666667%; margin: 0;\"> ");
		builder.append("   Sonuç ");
		builder.append("</th> ");
		builder.append("<th class=\"col-4\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 33.333333%; margin: 0;\"> ");
		builder.append("  "+String.valueOf(getWorkDay() * 8)+" Saat Çalışma Süresinde, "+String.valueOf(getDiffBlocks())+" Farklı "+String.valueOf(getTotalBlockNumber())+" Adet – "+String.valueOf(getTotalBlockHours())+" Saat Blok  ");
		builder.append("</th> "); 
		builder.append("</tr> ");
		builder.append("</thead> ");
		builder.append("</table> ");
		
		
//		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
//		builder.append("<thead> ");
//		builder.append("<tr> ");
//		builder.append("<th class=\"col-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; color:red; margin: 0;\"> ");
//		builder.append("   * Yukarıdaki blok süreleri tüm yılı kapsamaMAktadır. Manuel işlemden dolayı 12.02.2018 ve 16.08.2018 arasındaki değerler ile otomasyonun başladığı günden itibarenki tarihleri kapsamaktadır.  ");
//		builder.append("</th> ");
//		builder.append("</tr> ");
//		builder.append("</thead> ");
//		builder.append("</table> ");
		
		
		builder.append("</div> ");
		builder.append("</td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");
		builder.append("</div> ");
		builder.append("</td> ");
		builder.append("</tr> ");
		builder.append("</tbody> ");
		builder.append("</table> ");

		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* Beşinci Row Bitti */

		builder.append("	<br> ");
		builder.append("	<br> ");
		builder.append("	<br> ");






//		/* Altıncı  Row Başlıyor */
//
//		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
//		builder.append(" <thead> ");
//		builder.append("    <tr> ");
//		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");
//
//		// Row İçerik
//
//		createXLBTodayOrYesterday(builder, "XLB-Bugün", getBlocksToday,sdf.format(new Date(System.currentTimeMillis())));
//
//		// Row İçerik Bitti 
//
//		builder.append("	</th> ");
//		builder.append("	</tr> ");
//		builder.append("	</thead> ");
//		builder.append("	</table> ");
//
//		/* Altıncı Row Bitti */
//
//		builder.append("	<br> ");
//		builder.append("	<br> ");
//		builder.append("	<br> ");
//
//
//
//		/* Yedinci  Row Başlıyor */
//
//		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
//		builder.append(" <thead> ");
//		builder.append("    <tr> ");
//		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");
//
//		// Row İçerik
//
//
//		calendar.clear();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//
//		int dayWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
//		if(dayWeekNumber>=2 && dayWeekNumber <=6) {
//			int controlValue = dayWeekNumber - 1;
//			if(controlValue != 1) {
//				calendar.add(Calendar.DAY_OF_WEEK, -1);
//			} else if(controlValue == 1) {
//				calendar.add(Calendar.DAY_OF_MONTH, -3);
//			}
//		}
//		createXLBTodayOrYesterday(builder, "XLB-Dün",getXLBlockYesterday,sdf.format(calendar.getTime()));
//
//
//		// Row İçerik Bitti 
//
//		builder.append("	</th> ");
//		builder.append("	</tr> ");
//		builder.append("	</thead> ");
//		builder.append("	</table> ");
//
//		/* Yedinci Row Bitti */
//
//		builder.append("	<br> ");
//		builder.append("	<br> ");
//		builder.append("	<br> ");



		/* Sekinci Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		// Row İçerik

		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\">");
		builder.append("<div>");
		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\">");
		builder.append("<div>XLB-Bu Hafta</div>");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");
		builder.append("</div>");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");

		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* Sekizinci Row Bitti */


		/* Dokuzuncu Row Başlıyor */

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-lg-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; font-weight: normal; width: 100%; margin: 0;\"> ");

		//Row İçerik

		calendar.clear();
		calendar.setTimeInMillis(System.currentTimeMillis());

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-lg-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px;  font-weight: normal; width: 100%; margin: 0;\"> ");


		createWeekDayGeneric(builder, Calendar.MONDAY, "Pazartesi");

		builder.append("	</th> ");
		builder.append("   <th class=\"col-lg-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px;  font-weight: normal; width: 100%; margin: 0;\"> ");


		createWeekDayGeneric(builder, Calendar.TUESDAY, "Salı");

		builder.append("	</th> ");

		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");


		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-lg-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px;  font-weight: normal; width: 100%; margin: 0;\"> ");


		createWeekDayGeneric(builder, Calendar.WEDNESDAY, "Çarşamba");

		builder.append("	</th> ");
		builder.append("   <th class=\"col-lg-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px;  font-weight: normal; width: 100%; margin: 0;\"> ");


		createWeekDayGeneric(builder, Calendar.THURSDAY, "Perşembe");

		builder.append("	</th> ");

		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");


		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-lg-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px;  font-weight: normal; width: 100%; margin: 0;\"> ");


		createWeekDayGeneric(builder, Calendar.FRIDAY, "Cuma");

		builder.append("	</th> ");
		builder.append("   <th class=\"col-lg-6\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; font-weight: normal; width: 100%; margin: 0;\"> ");




		builder.append("	</th> ");

		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");




		// Row İçerik Bitti 

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");

		/* Dokuzuncu Row Bitti */

		builder.append("	<br> ");
		builder.append("	<br> ");
		builder.append("	<br> ");


//		/* Onuncu Row Başlıyor */
//
//		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%;\"> ");
//		builder.append(" <thead> ");
//		builder.append("    <tr> ");
//		builder.append("   <th class=\"col-lg-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; font-weight: normal; width: 100%; margin: 0;\"> ");
//		//Row İçerik Başladı
//
//		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\">");
//		builder.append("<tbody>");
//		builder.append("<tr>");
//		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\">");
//		builder.append("<div>");
//		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
//		builder.append("<tbody>");
//		builder.append("<tr>");
//		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\">");
//		builder.append("<table class=\"tg\" style=\"undefined;table-layout: fixed; width: 611px\"> ");
//		builder.append("<colgroup> ");
//		builder.append("<col style=\"width: 87px\"> ");
//		builder.append("<col style=\"width: 88px\"> ");
//		builder.append("<col style=\"width: 88px\"> ");
//		builder.append("<col style=\"width: 87px\"> ");
//		builder.append("<col style=\"width: 87px\"> ");
//		builder.append("<col style=\"width: 87px\"> ");
//		builder.append("<col style=\"width: 87px\"> ");
//		builder.append("</colgroup> ");
//		builder.append("<tr> ");
//		builder.append("<th class=\"tg-c3ow\">Pazartesi</th> ");
//		builder.append("<th class=\"tg-c3ow\">Salı</th> "); 
//		builder.append("<th class=\"tg-c3ow\">Çarşamba</th> "); 
//		builder.append("<th class=\"tg-c3ow\">Perşembe</th> ");
//		builder.append("<th class=\"tg-c3ow\">Cuma</th> ");
//		builder.append("<th class=\"tg-c3ow\">Cumartesi</th> ");
//		builder.append("<th class=\"tg-c3ow\">Pazar</th> ");
//		builder.append("</tr> ");
//
//
//		calendar.clear();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//		int month = calendar.get(Calendar.MONTH);
//		calendar.set(Calendar.DAY_OF_MONTH, 1);
//		calendar.set(Calendar.HOUR_OF_DAY, 07);
//		calendar.set(Calendar.MINUTE, 30);
//		for(int i = 0; i<31; i = calendar.get(Calendar.DAY_OF_MONTH)) {
//			if(calendar.get(Calendar.DAY_OF_MONTH) >= 1 && month != calendar.get(Calendar.MONTH)) {
//				break;
//			}
//			builder.append("<tr>");
//			createMonthTableColumn(builder, calendar, month,dateControlList, getXLBlockThisMonth);
//			builder.append("</tr>");
//		}
//		builder.append("</table> ");
//		builder.append("</td>");
//		builder.append("</tr>");
//		builder.append("</tbody>");
//		builder.append("</table>");
//		builder.append("</div>");
//		builder.append("</td>");
//		builder.append("</tr>");
//		builder.append("</tbody>");
//		builder.append("</table>");
//
//		// Row İçerik Bitti 
//
//		builder.append("	</th> ");
//		builder.append("	</tr> ");
//		builder.append("	</thead> ");
//		builder.append("	</table> ");
//
//		/* Onuncu Row Bitti */
//
//		builder.append("	<br> ");
//		builder.append("	<br> ");
//		builder.append("	<br> ");

		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");
		builder.append("<!--[if (gte mso 9)|(IE)]>");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");
		builder.append("<![endif]-->");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");



		builder.append("</td>"); 
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");


		builder.append("</body>");
		builder.append("</html>");

		return builder.toString();
	}


	private DefaultPieDataset generatePieDataset(BlockPieChartOutput pieOutput) {
		DefaultPieDataset pieDataSet = new DefaultPieDataset();
		for(BlockPieChartInfoModel rs :pieOutput.getBlockSystem() ) {
			Double value = new Double(rs.getValue());
			float fValue = value.floatValue();
			String result = String.format("%.2f", fValue);
			if(!result.equalsIgnoreCase("0,00")) {
				pieDataSet.setValue(rs.getLabel(), new Double(rs.getValue()));
			}
		}
		return pieDataSet;
	}

//	private void createMonthTableColumn(StringBuilder builder, Calendar calendar, int month, List<String> dateControlList , List<Document> thisMonthBlocks ) {
//		boolean lastWeekControl = false;
//		for(int i = 2; i<7; i++) {
//			builder.append(calendar.get(Calendar.DAY_OF_WEEK) == i ? "<td class=\"tg-yw4l\" "+ getBlockDateStyle(calendar, thisMonthBlocks)+ ">"+calendar.get(Calendar.DAY_OF_MONTH) +"</td>": "<td class=\"tg-yw4l\"></td>");
//			if(calendar.get(Calendar.DAY_OF_WEEK) == i) {
//				calendar.add(Calendar.DAY_OF_MONTH, 1);
//			}
//			if(calendar.get(Calendar.DAY_OF_MONTH) >= 1 && month != calendar.get(Calendar.MONTH)) {
//				while(true) {
//					calendar.add(Calendar.DAY_OF_MONTH, -1);
//					if(month == calendar.get(Calendar.MONTH)) {
//						break;
//					}
//				}
//				for(int y = calendar.get(Calendar.DAY_OF_WEEK); y <8; y++) {
//					builder.append("<td class=\"tg-yw4l\"></td>");
//				}
//				lastWeekControl = true;
//				break;
//			}
//		}
//		if(!lastWeekControl) {
//			builder.append(calendar.get(Calendar.DAY_OF_WEEK) == 7 ? "<td class=\"tg-yw4l\">"+calendar.get(Calendar.DAY_OF_MONTH) +"</td>": "<td class=\"tg-yw4l\"></td>");
//			if(calendar.get(Calendar.DAY_OF_WEEK) == 7) {
//				calendar.add(Calendar.DAY_OF_MONTH, 1);
//			}
//
//			builder.append(calendar.get(Calendar.DAY_OF_WEEK) == 1 ? "<td class=\"tg-yw4l\">"+calendar.get(Calendar.DAY_OF_MONTH) +"</td>": "<td class=\"tg-yw4l\"></td>");
//			if(calendar.get(Calendar.DAY_OF_WEEK) == 1) {
//				calendar.add(Calendar.DAY_OF_MONTH, 1);
//			}
//		} else {
//			calendar.add(Calendar.DAY_OF_MONTH, 1);
//		}
//	}

//	private String getBlockDateStyle(Calendar date, List<Document> thisMonthBlocks) {
//		String bgColor = "";
//		Calendar today = Calendar.getInstance();
//		today.setTimeInMillis(System.currentTimeMillis());
//		long blockDayControl = 0;
//		if(date.get(Calendar.DAY_OF_MONTH) <= today.get(Calendar.DAY_OF_MONTH)) {
//			long tempTimeMilis = date.getTimeInMillis();
//			date.set(Calendar.HOUR_OF_DAY, 07);
//			date.set(Calendar.MINUTE, 30);
//			long startDateMilis = date.getTimeInMillis();
//			date.set(Calendar.HOUR_OF_DAY, 16);
//			date.set(Calendar.MINUTE, 30);
//			long endDateMilis = date.getTimeInMillis();
//			date.setTimeInMillis(tempTimeMilis);
//			blockDayControl = thisMonthBlocks.stream()
//			.filter(s -> {
//				BlockInfoDocumentInput p1 = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
//				if(p1.getEndDate() == 0
//						&& p1.getStartDate() <= startDateMilis) {
//					return true;
//				}
//				if(p1.getEndDate() != 0) {
////					if(p1.getStartDate()<=startDateMilis
////							&& p1.getEndDate() > startDateMilis 
////							&& p1.getEndDate() <= endDateMilis) {
////						return true;
////					} else if(p1.getStartDate() >= startDateMilis
////							&& p1.getStartDate() <= endDateMilis) {
////						return true;
////					}
//					
//					
//					if(p1.getStartDate()<=startDateMilis 		  	//  |||||||||||tari|||h 
//							&& p1.getEndDate() > startDateMilis 
//							&& p1.getEndDate() <= endDateMilis) {
//						return true;
//					} else if(p1.getStartDate() <= startDateMilis 	// ||||||||||tarih|||||||||
//							&& p1.getEndDate() > startDateMilis
//							&& p1.getEndDate() > endDateMilis) {
//						return true;
//					} else if(p1.getStartDate() >= startDateMilis // t|||||ari||||h   
//							&& p1.getStartDate() < endDateMilis
//							&& p1.getEndDate() <= endDateMilis) {
//						return true;
//					} else if(p1.getStartDate() >= startDateMilis // t|||||ari|||||||h|||||||||
//							&& p1.getStartDate() < endDateMilis
//							&& p1.getEndDate() > endDateMilis) {
//						return true;
//					}
//				}
//				return false;
//			}).reduce(0l, (sum, p1) -> {
//				return sum + 1;
//			}, (sum1, sum2) -> sum1 + sum2);
//			
//			bgColor = blockDayControl > 0 ? "style = \"background-color:red\"" : "style = \"background-color:green\"";
//		}
//		return bgColor;
//	}


	private void createWeekDayGeneric(StringBuilder builder, int weekDay, String weekDayLabel) throws IOException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_WEEK, weekDay);
		String pastDate = sdf.format(new Date(calendar.getTimeInMillis()));
		long timeMilis = calendar.getTimeInMillis();
		List<Document> getWeekDayBlock = blockDateRangeService.getBlockOfParameter(timeMilis, timeMilis,0,0);

		calendar.setTimeInMillis(System.currentTimeMillis());
		newWeekTodayXLB(builder, getWeekDayBlock, pastDate, weekDayLabel, calendar.get(Calendar.DAY_OF_WEEK) < weekDay);
	}

	private void newWeekTodayXLB(StringBuilder builder, List<Document> blockList, String pastDate, String day, boolean isEmpty) {


		if(isEmpty) {
			return;
		}

		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%; \"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\">");
		builder.append("<div>");
		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\">");
		builder.append("<div>"+ day + " - " + pastDate +"</div>");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");
		builder.append("</div>");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");



		builder.append("<table class=\"row\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; margin-right: -15px; margin-left: -15px; table-layout: fixed; width: 100%; \"> ");
		builder.append(" <thead> ");
		builder.append("    <tr> ");
		builder.append("   <th class=\"col-12\" align=\"left\" valign=\"top\" style=\"line-height: 24px; font-size: 16px; min-height: 1px; padding-right: 15px; padding-left: 15px; font-weight: normal; width: 100%; margin: 0;\"> ");

		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\">");
		builder.append("<div>");
		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; width: 100%; margin: 0; padding: 15px;\" align=\"left\">");
		builder.append(" <table class=\"table\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%; max-width: 100%;\" bgcolor=\"#ffffff\"> ");
		builder.append(" <thead class=\"thead-light\"> ");
		builder.append(" 	 <tr> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Ortam</th> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Baş Tarih</th> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Bit. Tarih</th> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Tipi</th> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Süre</th> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Etkilenen</th> ");
		builder.append("       <th style=\"line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Durum</th> ");
		builder.append("    </tr> ");
		builder.append(" </thead>  ");
		builder.append("  <tbody> ");
		for(Document rs: blockList) {
			BlockInfoDocumentInput blockDetail = gson.fromJson(gson.toJson(rs.get("blockDetail")), BlockInfoDocumentInput.class);
			String blockHours = String.valueOf(rs.getDouble("blockHours"));
			String status = blockDetail.isStatus() ? "Devam Ediyor" : "Sonlandı";
			String startDate = sdfWithHour.format(new Date(blockDetail.getStartDate()));
			String endDate  = blockDetail.getEndDate() != 0 ? sdfWithHour.format(new Date(blockDetail.getEndDate())) : "";
			builder.append("   <tr> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getAffectEnvironment()+"</td> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+startDate+"</td> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+endDate+"</td> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getBlockType()+"</td> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockHours+"</td> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getOpenBlockUser()+"</td> ");
			builder.append("      <td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 8px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+status+"</td> ");
			builder.append("  </tr> ");
		}
		builder.append(" </tbody> ");
		builder.append(" </table> ");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");
		builder.append("</div>");
		builder.append("</td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");

		builder.append("	</th> ");
		builder.append("	</tr> ");
		builder.append("	</thead> ");
		builder.append("	</table> ");
	}




//	private void createXLBTodayOrYesterday(StringBuilder builder, String header, List<Document> blockList, String nowDate) {
//		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\">");
//		builder.append("<tbody>");
//		builder.append("<tr>");
//		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\">");
//		builder.append("<div>");
//		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
//		builder.append("<tbody>");
//		builder.append("<tr>");
//		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\">");
//		builder.append(header);
//		builder.append("</td>");
//		builder.append("</tr>");
//		builder.append("</tbody>");
//		builder.append("</table>");
//		builder.append("</div>");
//		builder.append("</td>");
//		builder.append("</tr>");
//		builder.append("</tbody>");
//		builder.append("</table>");
//
//		builder.append("<table class=\"card\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; width: 100%; overflow: hidden; border: 1px solid #dee2e6;\" bgcolor=\"#ffffff\">");
//		builder.append("<tbody>");
//		builder.append("<tr>");
//		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0;\" align=\"left\">");
//		builder.append("<div>");
//		builder.append("<table class=\"card-body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
//		builder.append("<tbody>");
//		builder.append("<tr>");
//		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; width: 100%; margin: 0; padding: 20px;\" align=\"left\">");
//		builder.append("<div>");
//		builder.append("<table class=\"table\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%; max-width: 100%; margin-bottom: 16px;\" bgcolor=\"#ffffff\">");
//		builder.append("<thead class=\"thead-dark\">");
//		builder.append("<tr>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Tarih</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Sistem</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Ortam</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Başlangıç Saati</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Bitiş Saati</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Süre</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Türü *</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Etkilenen</th>");
//		builder.append("<th style=\"line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">Blok Durum</th>");
//		builder.append("</tr>");
//		builder.append("</thead>");
//		builder.append("<tbody>");
//
//		for(Document rs: blockList) {
//			String blockHours = String.valueOf(rs.getDouble("blockHours").intValue());
//			BlockInfoDocumentInput blockDetail = gson.fromJson(gson.toJson(rs.get("blockDetail")), BlockInfoDocumentInput.class);
//
//			String status = blockDetail.isStatus() ? "Devam Ediyor" : "Sonlandı";
//			builder.append("<tr>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+nowDate+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getBlockSystem()+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getAffectEnvironment()+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+sdf.format(new Date(blockDetail.getStartDate()))+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+sdf.format(new Date(blockDetail.getEndDate()))+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockHours+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getBlockType()+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+blockDetail.getOpenBlockUser()+"</td>");
//			builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #e9ecef; border-top-style: solid; margin: 0; padding: 12px;\" align=\"left\" valign=\"top\">"+status+ "</td>");
//			builder.append("</tr>");
//		}
//		builder.append("</tbody>");
//		builder.append("</table>");
//		builder.append("</div>");
//		builder.append("</td>");
//		builder.append("</tr>");
//		builder.append("</tbody>");
//		builder.append("</table>");
//		builder.append("</div>");
//		builder.append("</td>");
//		builder.append("</tr>");
//		builder.append("</tbody>");
//		builder.append("</table>");
//	}


	private void createHr(StringBuilder builder) {
		builder.append("<div class=\"hr \" style=\"width: 100%; margin: 20px 0; border: 0;\">");
		builder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Helvetica, Arial, sans-serif; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0px; border-collapse: collapse; width: 100%;\">");
		builder.append("<tbody>");
		builder.append("<tr>");
		builder.append("<td style=\"border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-top-width: 1px; border-top-color: #dddddd; border-top-style: solid; height: 1px; width: 100%; margin: 0;\" align=\"left\"></td>");
		builder.append("</tr>");
		builder.append("</tbody>");
		builder.append("</table>");
		builder.append("</div>");
	}


	private String getMonthName(int monthNumber) {
		String result = "";
		switch(monthNumber) {
		case 0 :
			result = "Ocak";
			break;
		case 1 :
			result = "Şubat";
			break;
		case 2 :
			result = "Mart";
			break;
		case 3:
			result = "Nisan";
			break;
		case 4 :
			result = "Mayıs";
			break;
		case 5 :
			result = "Haziran";
			break;
		case 6 :
			result = "Temmuz";
			break;
		case 7 :
			result = "Ağustos";
			break;
		case 8 :
			result = "Eylül";
			break;
		case 9 :
			result = "Ekim";
			break;
		case 10 :
			result = "Kasım";
			break;
		case 11 :
			result = "Aralık";
			break;
		}
		return result;
	}
	
	private void calculateSummaryInfo(long startTimeMilis) {
		List<Document> getAllBlockListWithoutStatus = blockDateRangeService.getBlockForAllStatus(startTimeMilis, System.currentTimeMillis());
		List<String> tempList = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startTimeMilis);
		Calendar calendarToday = Calendar.getInstance();
		calendarToday.setTimeInMillis(System.currentTimeMillis());
		
		
		Gson gson = new Gson();
		long workDay = 0;
		long blockDayControl = 0;
		long blockDay = 0;
		long diffBlock = 0;
		long totalBlocks = 0;
		long totalBlockSum = 0;
		
		for(long i = 1; i< calendarToday.get(Calendar.DAY_OF_YEAR); i = calendar.get(Calendar.DAY_OF_YEAR)) {
			
			if(calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY) {
				workDay = workDay + 1;
				long tempTimeMilis = calendar.getTimeInMillis();
				calendar.set(Calendar.HOUR_OF_DAY, 07);
				calendar.set(Calendar.MINUTE, 30);
				long startDateMilis = calendar.getTimeInMillis();
				calendar.set(Calendar.HOUR_OF_DAY, 16);
				calendar.set(Calendar.MINUTE, 30);
				long endDateMilis = calendar.getTimeInMillis();
				calendar.setTimeInMillis(tempTimeMilis);
				
				blockDayControl = getAllBlockListWithoutStatus.stream()
				.filter(s -> {
					BlockInfoDocumentInput p1 = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
					if(p1.getEndDate() == 0
							&& p1.getStartDate() <= startDateMilis) {
						return true;
					}
					if(p1.getEndDate() != 0) {
						if(p1.getStartDate()<=startDateMilis 		  	//  |||||||||||tari|||h 
								&& p1.getEndDate() > startDateMilis 
								&& p1.getEndDate() <= endDateMilis) {
							return true;
						} else if(p1.getStartDate() <= startDateMilis 	// ||||||||||tarih|||||||||
								&& p1.getEndDate() > startDateMilis
								&& p1.getEndDate() > endDateMilis) {
							return true;
						} else if(p1.getStartDate() >= startDateMilis 	// t|||||ari||||h   
								&& p1.getStartDate() < endDateMilis
								&& p1.getEndDate() <= endDateMilis) {
							return true;
						} else if(p1.getStartDate() >= startDateMilis 	// t|||||ari|||||||h|||||||||
								&& p1.getStartDate() < endDateMilis
								&& p1.getEndDate() > endDateMilis) {
							return true;
						}
					}
					return false;
				}).reduce(0l, (sum, p1) -> {
					return sum + 1;
				}, (sum1, sum2) -> sum1 + sum2);
				
				if(blockDayControl > 0) {
					blockDay = blockDay + 1;
				}
			}
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		
		diffBlock = getAllBlockListWithoutStatus.stream()
				.reduce(0l, (sum,s) -> {
					BlockInfoDocumentInput p1 = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
					String temp  = p1.getBlockSystem() + "+" + p1.getBlockType() + "+" + p1.getAffectEnvironment() + "+" + p1.getAffectSystem();
					if(tempList.contains(temp)) {
						return sum;
					} else {
						tempList.add(temp);
						return sum + 1;
					}
				}, (sum1,sum2) -> sum1 + sum2);
		
		totalBlockSum = getAllBlockListWithoutStatus
				.stream()
				.reduce(0l,(sum, p1) -> {
					return sum + p1.getDouble("blockHours").intValue();
				}, (sum1, sum2) -> sum1 + sum2);
		
		totalBlocks = getAllBlockListWithoutStatus.size();
		
		setWorkDay(workDay+1);
		setCountBlockDay(blockDay);
		setDiffBlocks(diffBlock);
		setTotalBlockHours(totalBlockSum);
		setTotalBlockNumber(totalBlocks);
		
	}


	private long passingTimeBeetweenTwoDate(long startTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startTime);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 07);
		calendar.set(Calendar.MINUTE, 30);
		
		boolean controlTime = true;
		long workDay = 0;
		long monthNumber = calendar.get(Calendar.MONTH);
		
		while(controlTime) {
			if(monthNumber == calendar.get(Calendar.MONTH) && 
					calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && 
					calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY) {
				workDay++;
			}
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			
		
			if(monthNumber != calendar.get(Calendar.MONTH)) {
				controlTime = false;
			}
		}
		return workDay;
	}
	
	
	public String getBlockHoursSum(List<Document> blockList) {
		NumberFormat format = new DecimalFormat("#0.00");
		double blockSum = blockList
				.stream()
				.reduce(0d, (sum,p1) -> sum += p1.getDouble("blockHours").doubleValue(), (sum1, sum2) -> sum1 + sum2);
		return format.format(blockSum);
	}

	public long getDiffBlocks() {
		return diffBlocks;
	}

	public void setDiffBlocks(long diffBlocks) {
		this.diffBlocks = diffBlocks;
	}

	public long getCountBlockDay() {
		return countBlockDay;
	}

	public void setCountBlockDay(long countBlockDay) {
		this.countBlockDay = countBlockDay;
	}

	public long getWorkDay() {
		return workDay;
	}

	public void setWorkDay(long workDay) {
		this.workDay = workDay;
	}

	public long getTotalBlockNumber() {
		return totalBlockNumber;
	}

	public void setTotalBlockNumber(long totalBlockNumber) {
		this.totalBlockNumber = totalBlockNumber;
	}

	public long getTotalBlockHours() {
		return totalBlockHours;
	}

	public void setTotalBlockHours(long totalBlockHours) {
		this.totalBlockHours = totalBlockHours;
	}

	public ByteArrayOutputStream getBytePieChartToday() {
		return bytePieChartToday;
	}

	public void setBytePieChartToday(ByteArrayOutputStream bytePieChartToday) {
		this.bytePieChartToday = bytePieChartToday;
	}

	public ByteArrayOutputStream getBytePieChartWeek() {
		return bytePieChartWeek;
	}

	public void setBytePieChartWeek(ByteArrayOutputStream bytePieChartWeek) {
		this.bytePieChartWeek = bytePieChartWeek;
	}

	public ByteArrayOutputStream getBytePieChartMonth() {
		return bytePieChartMonth;
	}

	public void setBytePieChartMonth(ByteArrayOutputStream bytePieChartMonth) {
		this.bytePieChartMonth = bytePieChartMonth;
	}









	




	
	
	

}
