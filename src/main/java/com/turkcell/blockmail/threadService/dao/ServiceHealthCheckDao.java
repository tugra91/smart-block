package com.turkcell.blockmail.threadService.dao;

import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import org.bson.types.ObjectId;

import java.util.List;

public interface ServiceHealthCheckDao {

    void saveHealthCheckService(ServiceHealthCheckDocument input) throws Exception;

    void updateHeathCheckService(ServiceHealthCheckDocument existService, ObjectId id) throws Exception;

    void updateHealthCheckServiceStatus(ObjectId id, boolean status);

    void updateHealthCheckServiceUptime(ObjectId id, Long uptime);

    ServiceHealthCheckDocument getHealthCheckServiceDocument(ObjectId id) throws Exception;

    List<ServiceHealthCheckDocument> getOperationsByParameter(String wsdlURL, String bindingName);

    void deleteHeathCheckService(ObjectId id) throws Exception;

    List<ServiceHealthCheckDocument> getHealthCheckServices() throws Exception;

    ObjectId getServiceId(String serviceName, String serviceURL) throws  Exception;

    ObjectId getSameServiceOperation(String serviceURL, String bindingName, String operationName) throws  Exception;

    ServiceHealthCheckDocument getServiceIds(String serviceName, String serviceURL);
}
