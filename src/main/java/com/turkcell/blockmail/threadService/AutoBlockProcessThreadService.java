package com.turkcell.blockmail.threadService;

import com.google.gson.Gson;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.save.service.BlockSaveService;
import com.turkcell.blockmail.threadService.dao.ServiceHealthCheckDao;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.threadService.model.HealthCheckServiceModel;
import com.turkcell.blockmail.util.CalendarUtil;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;
import org.apache.commons.collections4.MapUtils;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;


public class AutoBlockProcessThreadService implements Runnable {


    private ServiceHealthCheckDao serviceHealthCheckDao;


    private BlockSendMailService blockSendMailService;


    private BlockSaveService blockSaveService;

    private AutoBlockControlThreadService autoBlockControlThreadService;

    private AutoBlockStatusThreadService autoBlockStatusThreadService;

    private ObjectId id = new ObjectId();
    private Integer processType;
    private Map<String, Object> processValue;


    private Object lock = new Object();




    @Override
    public void run() {




        synchronized (lock) {

            System.out.println(Thread.currentThread().getName() + ". Process Thread e Girildi" + " Process Type: " + processType);


            if (processType == null) {
                System.out.println("Process Type Null  ");
                return;
            }

            if(autoBlockControlThreadService != null) {


                if (processType.intValue() == 0) {
                    serviceHealthCheckDao.updateHealthCheckServiceUptime(id,
                            MapUtils.getLong(processValue, "uptime"));
                    autoBlockControlThreadService.setWait(false);
                    lock.notify();
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (processType.intValue() == 1) {
                    System.out.println("1. Process E girildi");
                    blockSendMailService.sendServiceFaultMail("TEAM",
                            new Gson().fromJson(new Gson().toJson(MapUtils.getObject(processValue, "serviceModel")), HealthCheckServiceModel.class),
                            MapUtils.getString(processValue, "faultMessage"));
                    autoBlockControlThreadService.setWait(false);
                    lock.notify();
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (processType.intValue() == 3) {
                    System.out.println("3. PROCESSS");
                    ServiceHealthCheckDocument serviceDoc = new Gson().fromJson(new Gson().toJson(MapUtils.getObject(processValue, "serviceModel")), ServiceHealthCheckDocument.class);
                    String faultMessage = MapUtils.getString(processValue, "faultMessage");

                    for (String rs : serviceDoc.getTargetSystem()) {
                        BlockInfoDocumentInput input = new BlockInfoDocumentInput();
                        input.setCreateDate(System.currentTimeMillis());
                        input.setAffectSystem(rs);
                        input.setAffectEnvironment(serviceDoc.getEnv());
                        input.setBlockDesc(faultMessage);
                        input.setBlockName(serviceDoc.getServiceName() + " Servisi Hata Verdi");
                        input.setBlockType("Kesinti");
                        input.setStartDate(System.currentTimeMillis());
                        input.setStatus(true);
                        input.setBlockSystem(serviceDoc.getSourceSystem());
                        input.setServiceId(id);
                        blockSaveService.saveBlock(input);
                    }

                    System.out.println("3. PROCESSS bitti");
                    autoBlockControlThreadService.setWait(false);
                    lock.notify();
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }



            } else if(autoBlockStatusThreadService != null) {


                if (processType.intValue() == 0) {
                    serviceHealthCheckDao.updateHealthCheckServiceUptime(id,
                            MapUtils.getLong(processValue, "uptime"));

                    autoBlockStatusThreadService.setWait(false);
                    lock.notify();
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (processType.intValue() == 2) {
                    System.out.println("2. PROCESSS");
                    blockSendMailService.sendServiceRerunnigMail("TEAM",
                            new Gson().fromJson(new Gson().toJson(MapUtils.getObject(processValue, "serviceModel")), HealthCheckServiceModel.class));
                    autoBlockStatusThreadService.setWait(false);
                    lock.notify();
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (processType.intValue() == 4) {

                    List<BlockInfoDocumentInput> blockDocument = blockSaveService.getBlockByServiceId(id);

                    for (BlockInfoDocumentInput blockInfoDocument : blockDocument) {
                        blockSaveService.updateBlockMailTemp(blockInfoDocument.getId(), System.currentTimeMillis(), "AUTOBLOCK");
                    }

                    autoBlockStatusThreadService.setWait(false);
                    lock.notify();
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        }


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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public Map<String, Object> getProcessValue() {
        return processValue;
    }

    public void setProcessValue(Map<String, Object> processValue) {
        this.processValue = processValue;
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public AutoBlockControlThreadService getAutoBlockControlThreadService() {
        return autoBlockControlThreadService;
    }

    public void setAutoBlockControlThreadService(AutoBlockControlThreadService autoBlockControlThreadService) {
        this.autoBlockControlThreadService = autoBlockControlThreadService;
    }

    public AutoBlockStatusThreadService getAutoBlockStatusThreadService() {
        return autoBlockStatusThreadService;
    }

    public void setAutoBlockStatusThreadService(AutoBlockStatusThreadService autoBlockStatusThreadService) {
        this.autoBlockStatusThreadService = autoBlockStatusThreadService;
    }
}
