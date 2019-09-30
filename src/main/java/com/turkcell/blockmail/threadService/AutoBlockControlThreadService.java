package com.turkcell.blockmail.threadService;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import com.turkcell.blockmail.save.service.BlockSaveService;
import com.turkcell.blockmail.threadService.dao.ServiceHealthCheckDao;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.util.ServiceUtil;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;


@Service
public class AutoBlockControlThreadService implements Runnable {



	private ServiceHealthCheckDao serviceHealthCheckDao;
	private BlockSendMailService blockSendMailService;
	private BlockSaveService blockSaveService;
	private ServiceHealthCheckDocument serviceDoc;
	private ThreadPoolExecutor threadPoolExecutor;

	private Object lock = new Object();
	private boolean isWait = true;


	@Override
	public void run() {

			boolean isFault = false;

			while(!isFault) {
				System.out.println( "Control " + serviceDoc.getServiceName() + " Çalışıyor... ");

				HttpURLConnection connection = null;

				try {

					setServiceDoc(serviceHealthCheckDao.getHealthCheckServiceDocument(serviceDoc.getId()));

					connection = ServiceUtil.getConnection(ServiceUtil.convertServiceDocToModel(serviceDoc));

					if(connection.getResponseCode() != HttpStatus.OK.value()) {
						runnigStatusThread();
						sendFaultMail(connection.getResponseMessage());
						saveBlockRecord(connection.getResponseMessage());
						isFault = true;
						System.out.println(Thread.currentThread().getName() + " Servis Göçtü. Thread sona erdiriliyor. ");
						continue;
					}

					StringBuilder response = new StringBuilder();

					StopWatch stopWatch = new StopWatch();
					stopWatch.start();

					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));


					String line;
					while ((line = rd.readLine()) != null) {
						response.append(line);
						response.append('\r');
					}
					rd.close();
					is.close();
					stopWatch.stop();

					String clearResponse = response.toString()
							.replace("<?xml version='1.0' encoding='UTF-8'?>", "")
							.replaceAll("\\s+", "");

					String clearModel = serviceDoc.getResponse()
							.replace("<?xml version='1.0' encoding='UTF-8'?>", "")
							.replaceAll("\\s+", "");




					if(!StringUtils.equalsIgnoreCase(clearResponse, clearModel) && serviceDoc.isReqResCheck()) {
						System.out.println("Statüs Thread Başlatılıyor... ");
						runnigStatusThread();
						String faultMessage = "Kayıtlı Response ile Servisten Dönen Response Eşleşmedi : " + response.toString()
								.replaceAll("<", "&lt;")
								.replaceAll(">", "&gt;");
						sendFaultMail(faultMessage);
						saveBlockRecord(faultMessage);
						System.out.println(Thread.currentThread().getName() + " Servis Göçtü. Thread sona erdiriliyor. ");
						isFault = true;
						continue;
					}
					updateUptime(stopWatch.getTotalTimeMillis()  + 1);


				} catch (IOException e) {
					runnigStatusThread();
					sendFaultMail(e.getMessage());
					saveBlockRecord(e.getMessage());
					isFault = true;
					System.out.println(Thread.currentThread().getName() + " Servis Göçtü. Thread sona erdiriliyor. ");
				} catch (Exception e) {
					System.out.println(Thread.currentThread().getName() + " Sistemsel Hata yaşanıyor. Thread Durduruluyor.  " + e.getCause());
					isFault = true;
				} finally {
					if(connection != null) {
						connection.disconnect();
					}
				}

				try {
					Thread.sleep(30000);
				} catch (Exception e) {
					System.out.println(Thread.currentThread().getName() + " Sistemsel Hata yaşanıyor. Thread uyku durumundayken hata aldı. Thread durduruluyor. ");
					isFault = true;
				}

		}

	}


	private void runnigStatusThread() {
		AutoBlockStatusThreadService autoBlockStatusThreadService = new AutoBlockStatusThreadService();
		autoBlockStatusThreadService.setServiceDoc(serviceDoc);
		autoBlockStatusThreadService.setServiceHealthCheckDao(serviceHealthCheckDao);
		autoBlockStatusThreadService.setBlockSaveService(blockSaveService);
		autoBlockStatusThreadService.setBlockSendMailService(blockSendMailService);
		autoBlockStatusThreadService.setThreadPoolExecutor(threadPoolExecutor);
		Thread statusThread = new Thread(autoBlockStatusThreadService);
		statusThread.setName("STATUS - THREAD : " + serviceDoc.getServiceName());
		serviceHealthCheckDao.updateHealthCheckServiceStatus(serviceDoc.getId(), false);
		threadPoolExecutor.execute(statusThread);
	}


	private void updateUptime(long stopWatchValue) {
		Map<String, Object> processValue = new HashMap<>();
		processValue.put("uptime", Long.valueOf(stopWatchValue));
		runProcessThread(Integer.valueOf(0), processValue);

	}

	private void sendFaultMail(String faultMessage) {
		Map<String, Object> processValue = new HashMap<>();
		processValue.put("serviceModel", ServiceUtil.convertServiceDocToModel(serviceDoc));
		processValue.put("faultMessage", faultMessage);
		runProcessThread(Integer.valueOf(1), processValue);
	}

	private void saveBlockRecord(String faultMessage) {
		Map<String, Object> processValue = new HashMap<>();
		processValue.put("serviceModel", ServiceUtil.convertServiceDocToModel(serviceDoc));
		processValue.put("faultMessage", faultMessage);
		runProcessThread(Integer.valueOf(3), processValue);
	}

	private void runProcessThread(Integer processType, Map<String, Object> processValue) {


        synchronized (lock) {
			AutoBlockProcessThreadService autoBlockProcessThreadService = new AutoBlockProcessThreadService();
			autoBlockProcessThreadService.setId(serviceDoc.getId());
			autoBlockProcessThreadService.setProcessType(processType);
			autoBlockProcessThreadService.setProcessValue(processValue);
			autoBlockProcessThreadService.setServiceHealthCheckDao(serviceHealthCheckDao);
			autoBlockProcessThreadService.setBlockSendMailService(blockSendMailService);
			autoBlockProcessThreadService.setBlockSaveService(blockSaveService);
			autoBlockProcessThreadService.setLock(lock);
			autoBlockProcessThreadService.setAutoBlockControlThreadService(this);
			autoBlockProcessThreadService.setAutoBlockStatusThreadService(null);
			Thread tAutoBlockProcess = new Thread(autoBlockProcessThreadService);
			tAutoBlockProcess.setName(serviceDoc.getServiceName() + " " + processType);
            threadPoolExecutor.execute(tAutoBlockProcess);
			try {
				while(isWait) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public ServiceHealthCheckDocument getServiceDoc() {
		return serviceDoc;
	}

	public  void setServiceDoc(ServiceHealthCheckDocument serviceDoc) {
		this.serviceDoc = serviceDoc;
	}

	public ServiceHealthCheckDao getServiceHealthCheckDao() {
		return serviceHealthCheckDao;
	}

	public  void setServiceHealthCheckDao(ServiceHealthCheckDao serviceHealthCheckDao) {
		this.serviceHealthCheckDao = serviceHealthCheckDao;
	}


	public BlockSendMailService getBlockSendMailService() {
		return blockSendMailService;
	}

	public void setBlockSendMailService(BlockSendMailService blockSendMailService) {
		this.blockSendMailService = blockSendMailService;
	}

	public BlockSaveService getBlockSaveService() {
		return blockSaveService;
	}

	public void setBlockSaveService(BlockSaveService blockSaveService) {
		this.blockSaveService = blockSaveService;
	}

	public boolean isWait() {
		return isWait;
	}

	public void setWait(boolean wait) {
		isWait = wait;
	}

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
