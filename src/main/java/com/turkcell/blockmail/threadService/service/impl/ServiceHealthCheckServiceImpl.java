package com.turkcell.blockmail.threadService.service.impl;


import com.turkcell.blockmail.threadService.dao.ServiceHealthCheckDao;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.threadService.model.*;
import com.turkcell.blockmail.threadService.service.ServiceHealthCheckService;
import com.turkcell.blockmail.util.ServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.internal.Base64;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Response;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceHealthCheckServiceImpl implements ServiceHealthCheckService {



    private static final String NULL_USERNAME_PASSWORD_FAULT_MESSAGE = "Lütfen Servisin Kullanıcı adını ve Şifresini tam ve eksiksiz olarak giriş yapınız.";
    private static final String NULL_REQUIRED_FIELDS_FAULT_MESSAGE = "Lütfen Gerekli Alanların Tümünü Doldurunuz. ";
    private static final String SUCCESS_SAVE_SERVICE_MESSAGE = "Servis Kaydı Başarıyla Oluşturuldu. Hayırlı Uğurlu Olsun :)";
    private static final String SUCCESS_UPDATE_SERVICE_MESSAGE = " adlı servis başarıyla güncellendi. Hayırlı Uğurlu Olsun :)";
    private static final String SYSTEM_FAULT_MESSAGE = "Sistemsel bir hata yaşıyoruz. Lütfen daha sonra tekrar deneyiniz. ";
    private static final String HAS_SERVICE_FAULT_MESSAGE = "Servis ismi bu endpoint üzerinden daha önce kayıt edilmiş. Tekrardan kayıt edemezsiniz. Üzgünüm ! :( ";
    private static final String HAS_SERVICE_OPERATION_FAULT_MESSAGE = "Bu operasyon daha önce kayıt edilmiş. Tekrardan kayıt edemezsiniz. Üzgünüm ! :( ";
    private static final String SERVICE_NOT_FOUND_FAULT_MESSAGE = "Böyle bir servis bulunamadı. ";
    private static final String SUCCESS_DELETE_SERVICE_FAULT_MESSAGE = " adlı servis başarıyla silindi. ";
    private static final String INVALID_URL_FAULT_MESSAGE = "Lütfen Geçerli bir URL giriniz. Geçersiz bir servis adresi girdiniz. ";
    private static final String INVALID_METHOD_TYPE_FAULT_MESSAGE = "Servis girdiğiniz method tipini desteklemiyor. Lütfen geçerli bir method tipi giriniz. ";
    private static final String SERVER_CONNECTION_FAULT_MESSAGE = "Servisten hata dönüyor bağlanamıyoruz. Lütfen servis sahibiyle görüşüp tekrar deneyin. ";
    private static final String WSDL_PARSE_FAULT_MESSAGE = "Girilen  adresteki dosya parse edilemiyor. Lütfen sistem adminlerine bilgi veriniz. ";


    @Autowired
    private ServiceHealthCheckDao serviceHealthCheckDao;



    @Override
    public ResponseEntity<GenericResultOutput> saveHealthCheckService(HealthCheckServiceModel input) {

        ResponseEntity<GenericResultOutput> result;
        GenericResultOutput output = new GenericResultOutput();

        try {

            GenericResultOutput authNullOutput = checkServiceAuthNullControl(input, output);
            if(authNullOutput != null)  {
                return ResponseEntity.status(HttpStatus.OK).body(authNullOutput);
            }


            GenericResultOutput checkNullOutput = checkNullableControl(input, output);
            if(checkNullOutput != null) {
                return ResponseEntity.status(HttpStatus.OK).body(checkNullOutput);
            }

            ObjectId isExistId = getExistId(input.getServiceName(), input.getServiceURL());

            if(isExistId != null) {
                output.setErrorCode(-3L);
                output.setMessage(HAS_SERVICE_FAULT_MESSAGE);
                output.setResult(true);
                return ResponseEntity.status(HttpStatus.OK).body(output);
            }

            ObjectId isOperationExistId = getSameServiceOperation(input.getServiceURL(), input.getBindingName(), input.getOperationName());

            if(isOperationExistId != null) {
                output.setErrorCode(-4L);
                output.setMessage(HAS_SERVICE_OPERATION_FAULT_MESSAGE);
                output.setResult(true);
                return ResponseEntity.status(HttpStatus.OK).body(output);
            }


            ServiceHealthCheckDocument document = ServiceUtil.convertServiceModelToDoc(input);
            document.setRunningTime(0L);
            document.setCloseTime(0L);
            document.setDownDate(0L);
            serviceHealthCheckDao.saveHealthCheckService(document);
            output.setMessage(SUCCESS_SAVE_SERVICE_MESSAGE);
            output.setResult(true);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (Exception e) {
            output.setResult(false);
            output.setErrorCode(100L);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        }

        return result;

    }

    @Override
    public ResponseEntity<GenericResultOutput> updateHealthCheckService(HealthCheckServiceModel serviceModel) {

        ResponseEntity<GenericResultOutput> result;
        GenericResultOutput output = new GenericResultOutput();
        try {

            GenericResultOutput authNullOutput = checkServiceAuthNullControl(serviceModel, output);
            if(authNullOutput != null)  {
                return ResponseEntity.status(HttpStatus.OK).body(authNullOutput);
            }

            GenericResultOutput checkNullOutput = checkNullableControl(serviceModel, output);
            if(checkNullOutput != null) {
                return ResponseEntity.status(HttpStatus.OK).body(checkNullOutput);
            }

            ObjectId isExistId = getSameServiceOperation(serviceModel.getServiceURL(), serviceModel.getBindingName(), serviceModel.getOperationName());



            if(isExistId == null) {
                output.setErrorCode(-5L);
                output.setMessage(SERVICE_NOT_FOUND_FAULT_MESSAGE);
                output.setResult(false);
                return ResponseEntity.status(HttpStatus.OK).body(output);
            }

            ServiceHealthCheckDocument document = ServiceUtil.convertServiceModelToDoc(serviceModel);


            serviceHealthCheckDao.updateHeathCheckService(document, isExistId);
            output.setMessage(serviceModel.getServiceName() +  SUCCESS_UPDATE_SERVICE_MESSAGE);
            output.setResult(true);
            result =  ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (Exception e) {
            output.setResult(false);
            output.setErrorCode(100L);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            result =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        }

        return result;

    }

    @Override
    public ResponseEntity<HealthCheckServiceModel> getHealthCheckService(String serviceName, String serviceURL) {
        ResponseEntity<HealthCheckServiceModel> result;
        HealthCheckServiceModel healthCheckServiceModel = new HealthCheckServiceModel();
        try {
            ObjectId id = getExistId(serviceName, serviceURL);

            if(id == null) {
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }

            ServiceHealthCheckDocument dbResult = serviceHealthCheckDao.getHealthCheckServiceDocument(id);
            healthCheckServiceModel = ServiceUtil.convertServiceDocToModel(dbResult);

            result = ResponseEntity.status(HttpStatus.OK).body(healthCheckServiceModel);

        } catch (Exception e) {
            healthCheckServiceModel.setResult(false);
            healthCheckServiceModel.setErrorCode(100L);
            healthCheckServiceModel.setMessage(SYSTEM_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(healthCheckServiceModel);
        }

        return result;
    }

    @Override
    public ResponseEntity<GenericResultOutput> deleteHealthCheckService(String serviceName, String serviceURL) {

        ResponseEntity<GenericResultOutput> result;
        GenericResultOutput output = new GenericResultOutput();
        try {
            ObjectId id = getExistId(serviceName, serviceURL);


            if(id == null) {
                output.setResult(false);
                output.setMessage(SERVICE_NOT_FOUND_FAULT_MESSAGE);
                output.setErrorCode(-5L);
                return ResponseEntity.status(HttpStatus.OK).body(output);
            }

            serviceHealthCheckDao.deleteHeathCheckService(id);
            output.setResult(true);
            output.setMessage(serviceName + SUCCESS_DELETE_SERVICE_FAULT_MESSAGE);
            result =  ResponseEntity.status(HttpStatus.OK).body(output);


        } catch (Exception e) {
            output.setResult(false);
            output.setErrorCode(100L);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        }

        return result;
    }

    @Override
    public ResponseEntity<List<HealthCheckServiceModel>> getHealthCheckServices() {
        ResponseEntity<List<HealthCheckServiceModel>> result;
        try {
            List<ServiceHealthCheckDocument> dbResult = serviceHealthCheckDao.getHealthCheckServices();
            List<HealthCheckServiceModel> serviceModels = new ArrayList<>();
            for(ServiceHealthCheckDocument rs: dbResult ) {
                HealthCheckServiceModel iRs = ServiceUtil.convertServiceDocToModel(rs);
                Float percent = Float.valueOf(rs.getRunningTime() * Long.valueOf(100))/(rs.getCloseTime() + rs.getRunningTime());
                iRs.setUptimePercent(percent);
                serviceModels.add(iRs);
            }
            result = ResponseEntity.status(HttpStatus.OK).body(serviceModels);

        } catch (Exception e) {
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return result;

    }

    @Override
    public ResponseEntity<GetIsOperationOutputModel> getOperationsByParameter(String wsdlUrl, String bindingName) {
        ResponseEntity<GetIsOperationOutputModel> result;
        GetIsOperationOutputModel output = new GetIsOperationOutputModel();

        try {

            List<ServiceHealthCheckDocument> dbResult = serviceHealthCheckDao.getOperationsByParameter(wsdlUrl, bindingName);
            List<HealthCheckServiceModel> operationList = new ArrayList<>();
            for(ServiceHealthCheckDocument rs: dbResult) {
                operationList.add(ServiceUtil.convertServiceDocToModel(rs));
            }
            output.setIsOperationList(operationList);
            output.setResult(true);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (Exception e) {
            output.setResult(false);
            output.setErrorCode(103L);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        }

        return result;
    }

    @Override
    public ResponseEntity<GenericResultOutput> checkServiceResponse(HealthCheckServiceModel serviceModel) {

        ResponseEntity<GenericResultOutput> result;

        GenericResultOutput output = new GenericResultOutput();

        HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();


        GenericResultOutput authNullOutput = checkServiceAuthNullControl(serviceModel, output);
        if(authNullOutput != null)  {
            return ResponseEntity.status(HttpStatus.OK).body(authNullOutput);
        }

        GenericResultOutput checkNullOutput = checkNullableControl(serviceModel, output);
        if(checkNullOutput != null) {
            return ResponseEntity.status(HttpStatus.OK).body(checkNullOutput);
        }



        try {
              connection = ServiceUtil.getConnection(serviceModel);

                /*
                Paste Response
                 */
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            is.close();

            output.setResult(true);
            output.setMessage(response.toString());

            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (MalformedURLException e) {
            output.setResult(false);
            output.setErrorCode(101L);
            output.setMessage(INVALID_URL_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (ProtocolException e) {
            output.setResult(false);
            output.setErrorCode(102L);
            output.setMessage(INVALID_METHOD_TYPE_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (IOException e) {
            output.setResult(false);
            output.setErrorCode(103L);
            output.setMessage(SERVER_CONNECTION_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (Exception e) {
            output.setResult(false);
            output.setErrorCode(100L);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


        return result;
    }

    @Override
    public ResponseEntity<HealthCheckServiceBindingModel> getOperationsFromWsdl(GetBindingListInputModel serviceModel) {

        ResponseEntity<HealthCheckServiceBindingModel> result;
        HealthCheckServiceBindingModel output = new HealthCheckServiceBindingModel();



        try {
            Document wsdlDocument = getWSDLDocument(serviceModel.getServiceWsdlURL(),
                    serviceModel.isHasAuth(),
                    serviceModel.getUserName(),
                    serviceModel.getPassword());

            List<HealthCheckServiceBinding> bindingList = new ArrayList<>();

            Element service = null;

            for(Node n = wsdlDocument.getDocumentElement().getFirstChild(); n != null; n = n.getNextSibling() ) {
                if (n.getNodeType() == Node.ELEMENT_NODE
                        && StringUtils.contains(n.getNodeName(), "service")) {
                    service = (Element)n;
                    break;
                }
            }



            for(Node n = wsdlDocument.getDocumentElement().getFirstChild(); n != null ; n = n.getNextSibling()) {
                if(n.getNodeType() == Node.ELEMENT_NODE
                    && StringUtils.contains(n.getNodeName(), "binding")) {
                    Element elBinding = (Element)n;

                    HealthCheckServiceBinding bindingModel = new HealthCheckServiceBinding();
                    String bindingName = elBinding.getAttributes().getNamedItem("name").getNodeValue();
                    String endPointAddress = endPointAddressAsBinding(service, bindingName);

                    List<HealthCheckServiceOperation> operationList = new ArrayList<>();
                    for(Node i = elBinding.getFirstChild(); i != null; i = i.getNextSibling()) {
                        if(i.getNodeType() == Node.ELEMENT_NODE
                                && StringUtils.contains(i.getNodeName(), "operation")) {
                            Element elOperation = (Element) i;

                            HealthCheckServiceOperation operation = new HealthCheckServiceOperation();
                            String operationName = elOperation.getAttributes().getNamedItem("name").getNodeValue();

                            for(Node z = elOperation.getFirstChild(); z != null; z = z.getNextSibling()) {
                                if(z.getNodeType() == Node.ELEMENT_NODE
                                    && StringUtils.equalsIgnoreCase(z.getNodeName(), "soap:operation")) {

                                    Element elSoapOperation = (Element)z;
                                    String soapAction = elSoapOperation.getAttributes().getNamedItem("soapAction") == null
                                            ? ""
                                            : elSoapOperation.getAttributes().getNamedItem("soapAction").getNodeValue() ;
                                    operation.setSoapAction(soapAction);
                                    break;
                                }
                            }
                            operation.setOperationName(operationName);
                            operationList.add(operation);
                        }
                    }


                    bindingModel.setBindingName(bindingName);
                    bindingModel.setEndpointAddres(endPointAddress);
                    bindingModel.setOperationList(operationList);
                    bindingList.add(bindingModel);

                }
            }

            output.setResult(true);
            output.setBindingList(bindingList);


            result =  ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (MalformedURLException e) {
            output.setResult(false);
            output.setMessage(INVALID_URL_FAULT_MESSAGE);
            output.setErrorCode(200L);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (ParserConfigurationException e) {
            output.setResult(false);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            output.setErrorCode(201L);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        } catch (IOException e) {
            output.setResult(false);
            output.setMessage(SERVER_CONNECTION_FAULT_MESSAGE);
            output.setErrorCode(202L);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (SAXException e) {
            output.setResult(false);
            output.setMessage(WSDL_PARSE_FAULT_MESSAGE);
            output.setErrorCode(203L);
            result = ResponseEntity.status(HttpStatus.OK).body(output);
        }  catch (Exception e) {
            output.setResult(false);
            output.setMessage(SYSTEM_FAULT_MESSAGE);
            output.setErrorCode(100L);
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        }


        return result;
    }

    @Override
    public ServiceHealthCheckDocument getDocument(String serviceName, String serviceURL) {
        return serviceHealthCheckDao.getServiceIds(serviceName, serviceURL);
    }




    private String endPointAddressAsBinding(Element service, String bindName) {
        String result = "";


        for(Node z = service.getFirstChild(); z != null ; z = z.getNextSibling()) {
            if(z.getNodeType() == Node.ELEMENT_NODE
                    && StringUtils.contains(z.getNodeName(), "port")
                    && z.getAttributes().getNamedItem("binding") != null
                    && StringUtils.contains(z.getAttributes().getNamedItem("binding").getNodeValue(), bindName)) {

                Element elPort = (Element)z;

                for(Node x = elPort.getFirstChild(); x != null ; x = x.getNextSibling()) {
                    if(x.getNodeType() == Node.ELEMENT_NODE
                            && StringUtils.contains(x.getNodeName(), "address")) {
                        Element elAddress = (Element)x;

                        result = elAddress.getAttributes().getNamedItem("location") != null ? elAddress.getAttributes().getNamedItem("location").getNodeValue() : "";
                        break;
                    }
                }
                break;
            }
        }

        return result;
    }

    private GenericResultOutput checkServiceAuthNullControl(HealthCheckServiceModel input, GenericResultOutput output) {

        if(input.isHasAuth()
                && (StringUtils.isEmpty(input.getPassword())
                || StringUtils.isEmpty(input.getUserName())))  {
            output.setErrorCode(-1L);
            output.setMessage(NULL_USERNAME_PASSWORD_FAULT_MESSAGE);
            output.setResult(false);
            return output;
        }

        return null;
    }


    private GenericResultOutput checkNullableControl(HealthCheckServiceModel input, GenericResultOutput output) {

        if(StringUtils.isBlank(input.getRequest())
                || StringUtils.isBlank(input.getServiceName())
                || input.getServiceType() == null
                || StringUtils.isBlank(input.getServiceURL())
                || StringUtils.isBlank(input.getSegment())
                || StringUtils.isBlank(input.getServiceWsdlURL())
                || StringUtils.isBlank(input.getOperationName())
                || StringUtils.isBlank(input.getBindingName())
                || StringUtils.isBlank(input.getSourceSystem())
                || CollectionUtils.isEmpty(input.getTargetSystem())
                || StringUtils.isBlank(input.getMethodType())
                || input.getSlaTime() == null
                || StringUtils.isBlank(input.getEnv())) {
            output.setErrorCode(-2L);
            output.setMessage(NULL_REQUIRED_FIELDS_FAULT_MESSAGE);
            output.setResult(false);
            return output;
        }

      return null;

    }


    private ObjectId getExistId(String serviceName, String serviceURL) throws Exception {
        ObjectId isExistId;

        try {
            isExistId = serviceHealthCheckDao.getServiceId(serviceName, serviceURL);
        } catch (NullPointerException e) {
            isExistId = null;
        }

        return isExistId;
    }

    private ObjectId getSameServiceOperation(String serviceURL, String bindingName, String operationName) {
        ObjectId isExistId;

        try {
            isExistId = serviceHealthCheckDao.getSameServiceOperation(serviceURL, bindingName, operationName);
        } catch (Exception e) {
            isExistId = null;
        }

        return isExistId;
    }


    private Document getWSDLDocument(String wsdlURL, boolean isAuth, String userName, String password) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;



        URL url = new URL(wsdlURL);
        URLConnection connection = url.openConnection();
        if (isAuth) {
            String authInfo = userName + ":" + password;
            connection.setRequestProperty("Authorization", "Basic " + Base64.encode(authInfo.getBytes()));
        }

        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            output.write(dataBuffer, 0, bytesRead);
        }

        document = dBuilder.parse(new ByteArrayInputStream(output.toByteArray()));



        return document;
    }


    private ServiceHealthCheckDocument prepareDocument(HealthCheckServiceModel serviceModel) {
        ServiceHealthCheckDocument document = new ServiceHealthCheckDocument();

        document.setHasAuth(serviceModel.isHasAuth());
        document.setUserName(serviceModel.getUserName());
        document.setPassword(serviceModel.getPassword());
        document.setRequest(serviceModel.getRequest());
        document.setResponse(serviceModel.getResponse());
        document.setServiceName(serviceModel.getServiceName());
        document.setServiceType(serviceModel.getServiceType());
        document.setServiceURL(serviceModel.getServiceURL());
        document.setServiceWsdlURL(serviceModel.getServiceWsdlURL());
        document.setOperationName(serviceModel.getOperationName());
        document.setBindingName(serviceModel.getBindingName());
        document.setSoapAction(serviceModel.getSoapAction());
        document.setSourceSystem(serviceModel.getSourceSystem());
        document.setTargetSystem(serviceModel.getTargetSystem());
        document.setMethodType(serviceModel.getMethodType());
        document.setSegment(serviceModel.getSegment());

        return document;
    }



}
