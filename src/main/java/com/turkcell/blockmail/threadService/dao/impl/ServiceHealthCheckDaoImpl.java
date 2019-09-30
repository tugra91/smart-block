package com.turkcell.blockmail.threadService.dao.impl;

import com.turkcell.blockmail.threadService.dao.ServiceHealthCheckDao;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ServiceHealthCheckDaoImpl implements ServiceHealthCheckDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void saveHealthCheckService(ServiceHealthCheckDocument input) throws Exception {
        mongoTemplate.insert(input);
    }

    @Override
    public void updateHeathCheckService(ServiceHealthCheckDocument existService, ObjectId id) throws Exception {
        Update update = new Update();
        update.set("password", existService.getPassword());
        update.set("userName", existService.getUserName());
        update.set("request", existService.getRequest());
        update.set("response", existService.getResponse());
        update.set("serviceName", existService.getServiceName());
        update.set("serviceType", existService.getServiceType());
        update.set("serviceURL", existService.getServiceURL());
        update.set("env", existService.getEnv());
        update.set("slaTime", existService.getSlaTime());
        update.set("sourceSystem", existService.getSourceSystem());
        update.set("targetSystem", existService.getTargetSystem());
        update.set("isReqResCheck", existService.isReqResCheck());
        update.set("closeTime", 0);
        update.set("runningTime", 0);
        FindAndModifyOptions options  = new FindAndModifyOptions();
        options.upsert(false);
        mongoTemplate.findAndModify(new Query().addCriteria(Criteria.where("id").is(id)), update, options, ServiceHealthCheckDocument.class);
    }

    @Override
    public void updateHealthCheckServiceStatus(ObjectId id, boolean status) {
       ServiceHealthCheckDocument serviceDoc =  mongoTemplate.findAndModify(new Query().addCriteria(Criteria.where("id").is(id)),
                new Update().set("status", status), ServiceHealthCheckDocument.class);

       Long closeTimes = status ? Long.valueOf((System.currentTimeMillis() - serviceDoc.getDownDate())/30000 ) : null;

       Update update = !status ? new Update()
                                .set("closeTime", serviceDoc.getCloseTime() + 1)
                                .set("downDate", System.currentTimeMillis())
               : new Update()
                .set("runningTime", serviceDoc.getRunningTime() + 1)
                .set("closeTime", serviceDoc.getCloseTime() + closeTimes );

       mongoTemplate.findAndModify(new Query().addCriteria(Criteria.where("id").is(id)), update, ServiceHealthCheckDocument.class);
    }

    @Override
    public void updateHealthCheckServiceUptime(ObjectId id, Long uptime) {
        ServiceHealthCheckDocument serviceDoc = mongoTemplate.findAndModify(new Query().addCriteria(Criteria.where("id").is(id)),
                new Update().set("uptime", uptime).set("status", true), ServiceHealthCheckDocument.class);
        mongoTemplate.findAndModify(new Query().addCriteria(Criteria.where("id").is(id)), new Update().set("runningTime", serviceDoc.getRunningTime() + 1), ServiceHealthCheckDocument.class);
    }

    @Override
    public ServiceHealthCheckDocument getHealthCheckServiceDocument(ObjectId id) throws Exception {
        return mongoTemplate.findById(id, ServiceHealthCheckDocument.class);
    }

    @Override
    public List<ServiceHealthCheckDocument> getOperationsByParameter(String wsdlURL, String bindingName) {
        return mongoTemplate.find(new Query(Criteria.where("serviceWsdlURL").is(wsdlURL).and("bindingName").is(bindingName)),
                ServiceHealthCheckDocument.class);
    }

    @Override
    public void deleteHeathCheckService(ObjectId id) throws Exception {
        mongoTemplate.findAndRemove(new Query().addCriteria(Criteria.where("id").is(id)), ServiceHealthCheckDocument.class);
    }

    @Override
    public List<ServiceHealthCheckDocument> getHealthCheckServices() throws Exception {
        return mongoTemplate.findAll(ServiceHealthCheckDocument.class);
    }

    @Override
    public ObjectId getServiceId(String serviceName, String serviceURL) throws  Exception {
        Collation collation = Collation.of("tr")
                .strength(Collation.ComparisonLevel.primary().excludeCase())
                .alternate(Collation.Alternate.shifted())
                .normalizationEnabled();
        Query query = new Query(Criteria.where("serviceName").is(serviceName).and("serviceURL").is(serviceURL)).collation(collation);
        return mongoTemplate.findOne(query, ServiceHealthCheckDocument.class).getId();
    }

    @Override
    public ObjectId getSameServiceOperation(String serviceURL, String bindingName, String operationName) throws Exception {
        return mongoTemplate.findOne(new Query(Criteria.where("serviceURL").is(serviceURL).and("bindingName").is(bindingName).and("operationName").is(operationName)), ServiceHealthCheckDocument.class).getId();

    }

    @Override
    public ServiceHealthCheckDocument getServiceIds(String serviceName, String serviceURL) {
        Collation collation = Collation.of("tr")
                .strength(Collation.ComparisonLevel.primary().excludeCase())
                .alternate(Collation.Alternate.shifted())
                .normalizationEnabled();
        Query query = new Query(Criteria.where("serviceName").is(serviceName)).collation(collation);
        return mongoTemplate.findOne(query, ServiceHealthCheckDocument.class);
    }
}
