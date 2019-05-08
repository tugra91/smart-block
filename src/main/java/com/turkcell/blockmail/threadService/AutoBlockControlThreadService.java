package com.turkcell.blockmail.threadService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.blockmail.document.BlockServiceResponseInfoDocument;

@Service
public class AutoBlockControlThreadService implements Runnable {

//	@Autowired
//	private BlockSendMailService blockSendMail;
//	
//	@Autowired
//	private BlockMailService blockMailService;

	private String repairFaultMessage = "";
	private int repairFaultStatus = 0;
	private int countErrorNumber = 0;
	boolean isRepairFault = false;

	@Override
	public void run() {

//		boolean isTimeRange = true;
//		int statusCode = 0;
//		double conResponseNanoMilis = 0;
//		double conResponseSecond = 0;
//		double constNanoMilis = 1000000000;
//		long conStartTime = 0;
//		long conEndTime = 0;
//		
//		BlockServiceResponseInfoDocument blockServiceResponse = new BlockServiceResponseInfoDocument();
//
//		while(isTimeRange) {
//			try {
//				conResponseSecond = 0;
//				
//				
//				URL url = new URL("http://localhost:8081/healthCheck");
//				HttpURLConnection con = (HttpURLConnection) url.openConnection();
//				con.setRequestMethod("GET");
//				con.setRequestProperty("Content-Type", "application-json");
//				conStartTime = System.nanoTime();
//				statusCode = con.getResponseCode();
//				conEndTime = System.nanoTime();
//				conResponseNanoMilis = conEndTime - conStartTime;
//				conResponseSecond = conResponseNanoMilis / constNanoMilis;
//				if(statusCode == 200) {
//					Thread.sleep(3000);
//					if(isRepairFault) {
//						blockSendMail.sendRepairFaultBlockMail(repairFaultMessage, repairFaultStatus);
//						isRepairFault = false;
//						countErrorNumber = 0;
//					}
//				} else {
//					isRepairFault = true;
//					if(countErrorNumber == 0) {
//						sendFaultMessage(con, statusCode, "");
//					}
//					Thread.sleep(5000);
//					countErrorNumber++;
//				}
//				con.disconnect();
//			} catch (MalformedURLException e) {
//				throw new RuntimeException("URL String Hatası alındı.. HATA DETAYI ", e);
//			} catch(IOException e) {
//				isRepairFault = true;
//				if(countErrorNumber == 0) {
//					StringWriter sw = new StringWriter();
//					e.printStackTrace(new PrintWriter(sw));
//					sendFaultMessage(null, 0, sw.toString());
//					System.out.println(sw.toString() + "  hatası aldı.");
//				}
//				countErrorNumber++;
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e1) {
//					throw new RuntimeException("Thread Sleep Fonksiyonu Hata Aldı.. HATA DETAYI " , e);
//				}
//			} catch (InterruptedException e) {
//					throw new RuntimeException("Thread Sleep Fonksiyonu Hata Aldı.. HATA DETAYI " , e);
//			} finally {
//				blockServiceResponse.setResponseTime(conResponseSecond);
//				blockServiceResponse.setServiceName("Health Check");
//				blockServiceResponse.setStatus(!isRepairFault);
//				blockServiceResponse.setTimeoutTime(45000);
//				blockServiceResponse.setLastUpdate(System.currentTimeMillis());
//				blockMailService.saveOrUpdateBlockServiceResponse(blockServiceResponse);
//			}
//		}

	}

	private void sendFaultMessage(HttpURLConnection con, int statusCode, String conError) {
		String body = "";
		
		if(con != null) {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getErrorStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();

			try {
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				in.close();
				body = content.toString();
			} catch (IOException e) {
				throw new RuntimeException("Error Response Okunurken Hata Alındı.. HATA DETAYI ", e);
			}
		} else {
			body = conError;
		}
		repairFaultStatus = statusCode;
		repairFaultMessage = body;
//		blockSendMail.sendFaultBlockMail(body, statusCode);
	}


}
