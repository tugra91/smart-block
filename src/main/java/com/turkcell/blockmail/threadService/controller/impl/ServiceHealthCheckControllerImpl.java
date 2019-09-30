package com.turkcell.blockmail.threadService.controller.impl;

import com.turkcell.blockmail.threadService.controller.ServiceHealthCheckController;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.threadService.model.*;
import com.turkcell.blockmail.threadService.service.ServiceHealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceHealthCheckControllerImpl implements ServiceHealthCheckController{

    @Autowired
    private ServiceHealthCheckService serviceHealthCheckService;

    @Override
    @RequestMapping(value = "/threadService/saveService")
    public ResponseEntity<GenericResultOutput> saveHealthCheckService(@RequestBody HealthCheckServiceModel input) {
        return serviceHealthCheckService.saveHealthCheckService(input);
    }

    @Override
    @RequestMapping(value = "/threadService/updateService")
    public ResponseEntity<GenericResultOutput> updateHealthCheckService(@RequestBody HealthCheckServiceModel serviceModel) {
        return serviceHealthCheckService.updateHealthCheckService(serviceModel);
    }

    @Override
    @RequestMapping(value = "/threadService/getService")
    public ResponseEntity<HealthCheckServiceModel> getHealthCheckService(@RequestParam(value = "serviceName") String serviceName,
                                                                         @RequestParam(value = "serviceUrl") String serviceURL){
        return serviceHealthCheckService.getHealthCheckService(serviceName, serviceURL);
    }

    @Override
    @RequestMapping(value = "/threadService/deleteService")
    public ResponseEntity<GenericResultOutput> deleteHealthCheckService(@RequestParam(value = "serviceName") String serviceName,
                                                                        @RequestParam(value = "serviceUrl") String serviceURL){
        return serviceHealthCheckService.deleteHealthCheckService(serviceName, serviceURL);
    }

    @Override
    @RequestMapping(value = "/oThreadService/getServices")
    public ResponseEntity<List<HealthCheckServiceModel>> getHealthCheckServices(){
        return serviceHealthCheckService.getHealthCheckServices();
    }

    @Override
    @RequestMapping(value = "/threadService/testService")
    public ResponseEntity<GenericResultOutput> checkServiceResponse(@RequestBody HealthCheckServiceModel serviceModel) {
        return serviceHealthCheckService.checkServiceResponse(serviceModel);
    }

    @Override
    @RequestMapping(value = "/threadService/getBindigns")
    public ResponseEntity<HealthCheckServiceBindingModel> getOperationsFromWsdl(@RequestBody GetBindingListInputModel serviceModel) {
        return serviceHealthCheckService.getOperationsFromWsdl(serviceModel);
    }

    @Override
    @RequestMapping(value = "/threadService/getDocument")
    public ServiceHealthCheckDocument getDocument(@RequestParam(value = "servicename") String serviceName,
                                                  @RequestParam(value = "serviceurl")String serviceURL,
                                                  @RequestBody String denemeBody){
        return serviceHealthCheckService.getDocument(serviceName,serviceURL);
    }

    @Override
    @RequestMapping(value = "/threadService/getHasOperationList")
    public ResponseEntity<GetIsOperationOutputModel> getOperationsByParameter(@RequestBody GetIsOperationInputModel input) {
        return serviceHealthCheckService.getOperationsByParameter(input.getWsdlURL(), input.getBindingName());
    }
}
