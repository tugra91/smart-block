package com.turkcell.blockmail.util.thread;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.soap.SOAPBinding;

import org.springframework.stereotype.Service;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Port;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;

@Service
public class SoapServiceCallThread {
	
	
//	
//	public static void main (String[] args) throws IOException {
//		soapCall();
//	}
	
	public static void soapCall() {
		String wsdlURL = "http://contapiservicetest.turkcell.com.tr:8890/contapiservice/ws/contApiWS?wsdl";
//		String wsdlPassUrl = "file:///C:/Users/TCMUER/Desktop/wsdl.wsdl";
		String wsdlPassUrl = "http://posa3.turkcell.tgc:50000/dir/wsdl?p=ic/d7d03c3f95aa3671b631dddb81a370cd";
		String endpointAddress = "http://posa3.turkcell.tgc:50000/XISOAPAdapter/MessageServlet?channel=:BC_MAYA:CC_GetList_SOAP_SND";
//		String endpointAddress = "http://contapiservicetest.turkcell.com.tr:8890/contapiservice/ws/contApiWS";
		QName serviceName = null;
		QName portName = null;
		StringBuilder builder = new StringBuilder();
		
		builder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.controller.contapiservice.marsalman.turkcell.com/\"> ");
		builder.append("<soapenv:Header/> ");
		builder.append("<soapenv:Body> ");
		builder.append("<web:callService> ");
		builder.append(" <serviceRequest> ");
		builder.append("         { ");
		builder.append("         \"header\": { ");
		builder.append("         \"transactionId\": 1, ");
		builder.append("         \"applicationId\": 999000, ");
		builder.append("         \"accessKey\": \"f61830d688b168e4d649da646d0d8617045d9ebfea7f43aa8d30920578648957\", ");
		builder.append("         \"userId\": \"PSAONDB\", ");
		builder.append("         \"operationId\": \"paymentQuery\", ");
		builder.append("         \"transactionDate\": \"2017-12-12T06:25:34\", ");
		builder.append("         \"language\": \"TR\" ");
		builder.append("         }, ");
		builder.append("         \"serviceHolderName\": \"paymentQuery\", ");
		builder.append("         \"serviceName\": \"getTfsConcernSwitchValue\", ");
		builder.append("          \"serviceInput\": { }} ");
		builder.append("         </serviceRequest> ");
		builder.append("     </web:callService> ");
		builder.append("   </soapenv:Body> ");
		builder.append("</soapenv:Envelope> ");
		
		StringBuilder builderPass = new StringBuilder();
		builderPass.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\"> ");
		builderPass.append("  <soapenv:Header/> ");
		builderPass.append("   <soapenv:Body> ");
		builderPass.append("      <urn:ZRT_STOK_KONTROL_SORGUSU_V2> ");
		builderPass.append("         <IT_SERI_NO> ");
		builderPass.append("            <item>301000000000016</item> ");
		builderPass.append("         </IT_SERI_NO> ");
		builderPass.append("         <IV_BYS>70031.42001</IV_BYS> ");
		builderPass.append("         <IV_DISTR_CHAN>11</IV_DISTR_CHAN> ");
		builderPass.append("      </urn:ZRT_STOK_KONTROL_SORGUSU_V2> ");
		builderPass.append("   </soapenv:Body> ");
		builderPass.append(" </soapenv:Envelope> ");
		
		
		
		
		String request = builder.toString();
		
		
		URL url;
		URLConnection uc;
		WSDLParser wsdlParser = new WSDLParser();
		Definitions defs = null;
		try {
			url = new URL(wsdlURL);
			uc = url.openConnection();
//			String userPass = "ENTUSR_MAYA:6wh4#18ghV!Jg";
//			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userPass.getBytes()));
//			uc.setRequestProperty("Authorization", basicAuth);
			defs = wsdlParser.parse(uc.getInputStream());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		String targetNameSpace = defs.getTargetNamespace();
		
		for(com.predic8.wsdl.Service service: defs.getServices()) {
			serviceName = new QName(targetNameSpace, service.getName());
			for (Port port: service.getPorts()) {
				portName = new QName(targetNameSpace, port.getName());
			}
		}
	
	
		SOAPMessage response = soapInvoke(serviceName, portName, endpointAddress, request);
		try {
			SOAPBody body = response.getSOAPBody();
			if(body.hasFault()) {
				System.out.println("Hata Aldık Kardeş");
			} else {
				BufferedOutputStream out = new BufferedOutputStream(System.out);
				response.writeTo(out);
				out.flush();
				System.out.println();
			}
		} catch (SOAPException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static SOAPMessage soapInvoke(QName serviceName, QName portName, String endpointAddress, String request) {
		MessageFactory messageFactory;
		SOAPMessage message;
		SOAPMessage response = null;
		SOAPPart soapPart;
		SOAPEnvelope soapEnvelope;
		SOAPBody soapBody;
		javax.xml.ws.Service service = javax.xml.ws.Service.create(serviceName);
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
		
		Dispatch dispatch = service.createDispatch(portName, SOAPMessage.class, javax.xml.ws.Service.Mode.MESSAGE);
		
		((BindingProvider) dispatch).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "ENTUSR_MAYA");
		((BindingProvider) dispatch).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "6wh4#18ghV!Jg");
		
		try {
			messageFactory = MessageFactory.newInstance();
			message = messageFactory.createMessage();
			soapPart = message.getSOAPPart();
			soapEnvelope = soapPart.getEnvelope();
			soapBody = soapEnvelope.getBody();
			StreamSource prepareRequest = new StreamSource(new StringReader(request));
			soapPart.setContent(prepareRequest);
			message.saveChanges();
			response = (SOAPMessage) dispatch.invoke(message);
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
		
	}

}
