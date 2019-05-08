package com.turkcell.blockmail.admin.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.turkcell.blockmail.admin.dao.ServiceRegisterDao;
import com.turkcell.blockmail.admin.service.ServiceRegisterService;
import com.turkcell.blockmail.document.ServiceRegisterDocument;
import com.turkcell.blockmail.model.ServiceAdminUserInputModel;
import com.turkcell.blockmail.model.ServiceRegisterDetailInput;
import com.turkcell.blockmail.model.ServiceRegisterDetailOutput;
import com.turkcell.blockmail.model.ServiceRegisterListOutput;
import com.turkcell.blockmail.model.ServiceRegisterTestOutput;
import com.turkcell.blockmail.util.ExceptionUtil;

@Service
public class ServiceRegisterServiceImpl implements ServiceRegisterService {

	@Autowired
	private ServiceRegisterDao serviceRegisterDao;

	private static final String BIND_PROTOCOL_11 = "SOAP11";
	private static final String BIND_PROTOCOL_12 = "SOAP12";

	@Override
	public void saveOrUpdate(ServiceRegisterDocument input) {
		serviceRegisterDao.saveOrUpdate(input);
	}

	@Override
	public ServiceRegisterDocument getOneService(BigInteger id) {
		return serviceRegisterDao.getOneService(id);
	}

	@Override
	public ServiceRegisterListOutput getAllServices() {
		ServiceRegisterListOutput output = new ServiceRegisterListOutput();
		output.setServiceList(serviceRegisterDao.getAllService());
		return output;
	}

	@Override
	public ServiceRegisterTestOutput serviceTest(ServiceRegisterDocument input) {

		ServiceRegisterTestOutput output = new ServiceRegisterTestOutput();
		String response = "";
		String targetNamespace = input.getTargetNameSpace();
		QName serviceName = new QName(targetNamespace, input.getServiceName());
		QName portName = new QName(targetNamespace, input.getPortName());
		try {
			SOAPMessage soapResponse = soapInvoke(serviceName, portName, input.getEndpointAddress(), input.getRequest(),
					input.getBindProtocol(), input.isAuth(), input.getUserName(), input.getPassword());
			SOAPBody body = soapResponse.getSOAPBody();
			if(body.hasFault()) {
				output.setMessage("Servis Hata Aldı, Hata Ayrıntısı : " + body.getFault().getFaultString());
				output.setTestResult(false);
			} else { 
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				soapResponse.writeTo(out);
				Charset utf8Chartset = Charset.forName("UTF-8");
				response = StringUtils.toEncodedString(out.toByteArray(), utf8Chartset);
//				if(StringUtils.equalsIgnoreCase(this.getSOAPBody(response), this.getSOAPBody(input.getResponse()))) {
//					output.setMessage("");
//					output.setTestResult(true);
//					output.setResponse(response);
//				} else {
//					output.setMessage("Girdiğiniz SOAP response ile test sırasında dönen SOAP response eşleşmiyor.");
//					output.setTestResult(false);
//				}
				
				output.setMessage("");
				output.setResponse(response);
				output.setTestResult(true);
			}
		} catch (SOAPException | IOException e) {
			output.setMessage("Servis çağırımı sırasında hata alındı Hata Ayrıntısı: " + ExceptionUtil.convertExceptionToString(e));
			output.setTestResult(false);
		} catch (StringIndexOutOfBoundsException e) {
			output.setMessage("Lütfen geçerli bir response yazın.");
			output.setTestResult(false);
		}

		return output;
	}
	
	@Override
	public ServiceRegisterDetailOutput getServiceDetail(ServiceRegisterDetailInput input) {
		ServiceRegisterDetailOutput output = new ServiceRegisterDetailOutput();
		List<String> operations = new ArrayList<>();
		URL url;
		URLConnection uc;
		WSDLParser wsdlParser = new WSDLParser();
		Definitions defs = null;
		try {
			url = new URL(input.getWsdlURL());
			uc = url.openConnection();
			if(input.isAuth()) {
				String userPass = input.getUsername()+":"+ input.getPassword();
				String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userPass.getBytes()));
				uc.setRequestProperty("Authorization", basicAuth);
			}
			defs = wsdlParser.parse(uc.getInputStream());
			output.setSuccess(true);
			output.setTargetNamespace(defs.getTargetNamespace());
			output.setServiceName(defs.getServices().get(0).getName());
			output.setPortName(defs.getServices().get(0).getPorts().get(0).getName());
			output.setBindingProtocol(defs.getBindings().get(0).getBinding().getProtocol());
			for(PortType pt : defs.getPortTypes()) {
				for(Operation op: pt.getOperations()) {
					operations.add(op.getName());
				}
			}
			output.setOperationList(operations);
			
		} catch (MalformedURLException e) {
			output.setMessage("WSDL URL Adresi Hatalı. "+ "\r WSDL Adresi: "+ input.getWsdlURL() +"\n Hata Detayı: " + ExceptionUtil.convertExceptionToString(e));
			output.setSuccess(false);
		} catch (IOException e) {
			output.setMessage("Verilen Adrese Bağlanırken Hata Oluştu. \n" + "WSDL Adresi: "+ input.getWsdlURL() +"\n Hata Detayı : " + ExceptionUtil.convertExceptionToString(e));
			output.setSuccess(false);
		} catch (Exception e) {
			output.setMessage("Genel bir hata oluştu. \n" + "WSDL Adresi: "+ input.getWsdlURL() +"\n Hata Detayı : " + ExceptionUtil.convertExceptionToString(e));
			output.setSuccess(false);
		}
		
		
		return output;
	}

	private SOAPMessage soapInvoke(QName serviceName, QName portName, String endpointAddress, 
			String request, String bindProtocol, boolean isAuth, String username, String password) throws SOAPException {
		MessageFactory messageFactory;
		SOAPMessage message;
		SOAPMessage response = null;
		SOAPPart soapPart;
		javax.xml.ws.Service service = javax.xml.ws.Service.create(serviceName);
		if(StringUtils.equalsIgnoreCase(bindProtocol, BIND_PROTOCOL_11 )) {
			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
		} else if(StringUtils.equalsIgnoreCase(bindProtocol, BIND_PROTOCOL_12)) {
			service.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, endpointAddress);
		}

		Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, javax.xml.ws.Service.Mode.MESSAGE);

		if(isAuth) {
			((BindingProvider) dispatch).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			((BindingProvider) dispatch).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}

		messageFactory = MessageFactory.newInstance();
		message = messageFactory.createMessage();
		soapPart = message.getSOAPPart();
		StreamSource prepareRequest = new StreamSource(new StringReader(request));
		soapPart.setContent(prepareRequest);
		message.saveChanges();
		response = dispatch.invoke(message);


		return response;

	}
	
	private String getSOAPBody(String soapResponse ) {
		String searchText = "XMLNS:";
		String endText = "=\"";
		String nsName = "";
		String bodyString = "";
		soapResponse = StringUtils.deleteWhitespace(soapResponse);
		soapResponse = soapResponse.toUpperCase();
		
		int beginIndex = soapResponse.indexOf(searchText);
		nsName = soapResponse.substring(beginIndex + searchText.length(), soapResponse.indexOf(endText));
		int startBodyIndex = soapResponse.indexOf("<"+nsName+":BODY>");
		int endBodyIndex = soapResponse.indexOf("</"+nsName+":BODY>");
		bodyString = soapResponse.substring(startBodyIndex, endBodyIndex);
		
		return StringUtils.deleteWhitespace(bodyString);	
	}

	@Override
	public void saveUser(ServiceAdminUserInputModel userModel) {
		String username = userModel.getUsername();
		String password = userModel.getPassword();
		Set<? extends GrantedAuthority> authoritys = new HashSet<>();
		for(String rs : userModel.getAuthorities()) {
			
		}
	}
	


}
