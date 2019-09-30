package com.turkcell.blockmail.threadService;

import com.turkcell.blockmail.save.service.BlockSaveService;
import com.turkcell.blockmail.threadService.dao.ServiceHealthCheckDao;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class AutoBlockStartThreadService implements Runnable {

    @Autowired
    private ServiceHealthCheckDao serviceHealthCheckDao;


    @Autowired
    private BlockSaveService blockSaveService;

    @Autowired
    private BlockSendMailService blockSendMailService;

    @Override
    public void run() {


        List<ServiceHealthCheckDocument> serviceHealthCheckDocuments = null;

        try {
            serviceHealthCheckDocuments = serviceHealthCheckDao.getHealthCheckServices();

        } catch (Exception e) {
            System.out.println("Servisler Başlatılamadı. Veritabanından okunamadı. ");
        }

        if (!CollectionUtils.isEmpty(serviceHealthCheckDocuments)) {

            int listSize = 0;
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(serviceHealthCheckDocuments.size() * 3);
            boolean isChangeList = false;
            boolean initialize = true;
            while(true) {
                try {
                    if(isChangeList || initialize) {
                        if(isChangeList) {
                            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(serviceHealthCheckDocuments.size() * 3);
                        }
                        for (ServiceHealthCheckDocument rs : serviceHealthCheckDocuments) {
                            AutoBlockControlThreadService autoBlockControlThreadService = new AutoBlockControlThreadService();
                            autoBlockControlThreadService.setServiceDoc(rs);
                            autoBlockControlThreadService.setServiceHealthCheckDao(serviceHealthCheckDao);
                            autoBlockControlThreadService.setBlockSaveService(blockSaveService);
                            autoBlockControlThreadService.setBlockSendMailService(blockSendMailService);
                            autoBlockControlThreadService.setThreadPoolExecutor(executor);
                            Thread serviceThread = new Thread(autoBlockControlThreadService);
                            serviceThread.setName("CONTROL - THREAD : " + rs.getServiceName());
                            executor.execute(serviceThread);
                        }
                        isChangeList = false;
                        initialize = false;
                    }

                    List<ServiceHealthCheckDocument> tempList = serviceHealthCheckDao.getHealthCheckServices();

                    listSize = tempList.size();

                    if(serviceHealthCheckDocuments.size() != listSize) {
                        executor.shutdown();
                        Thread.sleep(10000);
                        serviceHealthCheckDocuments = tempList;
                        isChangeList = true;
                    }

                } catch (Exception e) {
                    System.out.println("Thread Pool Exceutro Oluşturulamadı.  ");
                }
            }
        }

    }
}
