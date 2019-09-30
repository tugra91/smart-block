package com.turkcell.blockmail.threadService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class AutoBlockStatusThreadService implements Runnable{



	private ServiceHealthCheckDao serviceHealthCheckDao;
	private ServiceHealthCheckDocument serviceDoc;
	private BlockSaveService blockSaveService;
	private BlockSendMailService blockSendMailService;
	private ThreadPoolExecutor threadPoolExecutor;

	private Object lock = new Object();
	private boolean isWait = true;


	@Override
	public void run() {


		boolean isRepair = false;


		while (!isRepair) {

			System.out.println("Status " + serviceDoc.getServiceName() + "  Çalışıyor.... ");

			HttpURLConnection connection = null;

			try {
				Thread.sleep(90000);
			} catch (Exception e) {
				System.out.println(Thread.currentThread().getName() + " Sistemsel Hata yaşanıyor. Thread uyku durumundayken hata aldı. Thread durduruluyor. ");
				isRepair = true;
				continue;
			}

			try {

				setServiceDoc(serviceHealthCheckDao.getHealthCheckServiceDocument(serviceDoc.getId()));

				connection = ServiceUtil.getConnection(ServiceUtil.convertServiceDocToModel(serviceDoc));

				if (connection.getResponseCode() == HttpStatus.OK.value()) {

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
						continue;
					}

					isRepair = true;
					startControlThread();
					sendReRunnigMessage();
					updateUptime(stopWatch.getTotalTimeMillis());
					updateBlock();
					System.out.println(Thread.currentThread().getName() + " Servis Ayağa Kalktı. Thread sona erdiriliyor. ");

				}

			} catch (IOException e) {
				continue;
			} catch (Exception e) {
				System.out.println(Thread.currentThread().getName() + " Status Güncelleme yapılamadı. Sistemsel bir hata yaşanıyor. Thread sona erdiriliyor. ");
				isRepair = true;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
	}

	private void startControlThread() {
		AutoBlockControlThreadService autoBlockControlThreadService = new AutoBlockControlThreadService();
		autoBlockControlThreadService.setServiceHealthCheckDao(serviceHealthCheckDao);
		autoBlockControlThreadService.setServiceDoc(serviceDoc);
		autoBlockControlThreadService.setBlockSendMailService(blockSendMailService);
		autoBlockControlThreadService.setBlockSaveService(blockSaveService);
		autoBlockControlThreadService.setThreadPoolExecutor(threadPoolExecutor);
		Thread controlThread = new Thread(autoBlockControlThreadService);
		controlThread.setName("CONTROL - THREAD : " + serviceDoc.getServiceName());
		serviceHealthCheckDao.updateHealthCheckServiceStatus(serviceDoc.getId(), true);
		threadPoolExecutor.execute(controlThread);
	}

	private void updateUptime(long stopWatchValue) {
		Map<String, Object> processValue = new HashMap<>();
		processValue.put("uptime", Long.valueOf(stopWatchValue));
		runProcessThread(Integer.valueOf(0), processValue);
	}

	private void updateBlock() {
		runProcessThread(Integer.valueOf(4), new HashMap<>());
	}

	private void sendReRunnigMessage() {
		Map<String, Object> processValue = new HashMap<>();
		processValue.put("serviceModel", ServiceUtil.convertServiceDocToModel(serviceDoc));
		runProcessThread(Integer.valueOf(2), processValue);
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
			autoBlockProcessThreadService.setAutoBlockStatusThreadService(this);
			autoBlockProcessThreadService.setAutoBlockControlThreadService(null);
			autoBlockProcessThreadService.setLock(lock);
			Thread tAutoBlockProcess = new Thread(autoBlockProcessThreadService);
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

	public void setServiceDoc(ServiceHealthCheckDocument serviceDoc) {
		this.serviceDoc = serviceDoc;
	}

	public ServiceHealthCheckDao getServiceHealthCheckDao() {
		return serviceHealthCheckDao;
	}

	public void setServiceHealthCheckDao(ServiceHealthCheckDao serviceHealthCheckDao) {
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
