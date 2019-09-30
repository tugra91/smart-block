package com.turkcell.blockmail.threadService.controller;

import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.threadService.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServiceHealthCheckController {



    public ResponseEntity<GenericResultOutput> saveHealthCheckService(HealthCheckServiceModel input);

    public ResponseEntity<GenericResultOutput> updateHealthCheckService(HealthCheckServiceModel serviceModel);

    public ResponseEntity<HealthCheckServiceModel> getHealthCheckService(String serviceName, String serviceURL);

    public ResponseEntity<GenericResultOutput> deleteHealthCheckService(String serviceName, String serviceURL);

    public ResponseEntity<List<HealthCheckServiceModel>> getHealthCheckServices();

    public ResponseEntity<GenericResultOutput> checkServiceResponse(HealthCheckServiceModel serviceModel);

    public ResponseEntity<HealthCheckServiceBindingModel> getOperationsFromWsdl(GetBindingListInputModel serviceModel);

    public ServiceHealthCheckDocument getDocument(String serviceName, String serviceURL, String denemeBody);

    ResponseEntity<GetIsOperationOutputModel> getOperationsByParameter(GetIsOperationInputModel input);

}
