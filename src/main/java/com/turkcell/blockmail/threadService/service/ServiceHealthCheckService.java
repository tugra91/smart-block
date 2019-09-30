package com.turkcell.blockmail.threadService.service;

import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.threadService.model.*;
import org.springframework.http.ResponseEntity;

import javax.xml.ws.Response;
import java.util.List;

public interface ServiceHealthCheckService {


    public ResponseEntity<GenericResultOutput> saveHealthCheckService(HealthCheckServiceModel input);

    public ResponseEntity<GenericResultOutput> updateHealthCheckService(HealthCheckServiceModel serviceModel);

    public ResponseEntity<HealthCheckServiceModel> getHealthCheckService(String serviceName, String serviceURL);

    public ResponseEntity<GenericResultOutput> deleteHealthCheckService(String serviceName, String serviceURL);

    public ResponseEntity<List<HealthCheckServiceModel>> getHealthCheckServices();

    ResponseEntity<GetIsOperationOutputModel> getOperationsByParameter(String wsdlUrl, String bindingName);

    public ServiceHealthCheckDocument getDocument(String serviceName, String serviceURL);

    public ResponseEntity<GenericResultOutput> checkServiceResponse(HealthCheckServiceModel serviceModel);

    public ResponseEntity<HealthCheckServiceBindingModel> getOperationsFromWsdl (GetBindingListInputModel serviceModel);


}
