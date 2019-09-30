
package com.turkcell.blockmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.blockmail.util.mail.service.BlockSendMailService;


//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.xml.bind.annotation.XmlAccessOrder;
//import javax.xml.namespace.QName;
//
//import org.apache.commons.io.IOUtils;
//import org.bson.Document;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ibm.wsdl.BindingImpl;
//import com.ibm.wsdl.DefinitionImpl;
//import com.ibm.wsdl.ImportImpl;
//import com.ibm.wsdl.MessageImpl;
//import com.ibm.wsdl.PortImpl;
//import com.ibm.wsdl.PortTypeImpl;
//import com.ibm.wsdl.ServiceImpl;
//import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
//import com.ibm.wsdl.extensions.soap12.SOAP12BodyImpl;
//import com.ibm.wsdl.xml.WSDLReaderImpl;
//import com.ibm.wsdl.xml.WSDLWriterImpl;
//import com.turkcell.blockmail.daterange.service.BlockDateRangeService;
//import com.turkcell.blockmail.document.BlockInfoDocumentInput;
//import com.turkcell.blockmail.save.service.BlockSaveService;
//import com.turkcell.blockmail.util.mail.service.BlockSendMailService;
//import com.turkcell.blockmail.util.schedular.service.SchedularReportMailService;
//
@RestController
public class Deneme {
//	
	@Autowired
	private BlockSendMailService blockMailService;
//	
//	@Autowired
//	private SchedularReportMailService reportService;
//	
//	@Autowired
//	private BlockDateRangeService blockDateRangeService;
//	
//	@Autowired
//	private BlockSaveService blockSaveService;
//	
	@RequestMapping(value ="/sendEndmail")
	public void sendMail() {
//		blockMailService.sendEndDayReportMail();
		blockMailService.sendExampleMail();
//		List<BlockInfoDocumentInput> activeList = blockSaveService.getActiveBlockList();
//		List<Document> closedList = new ArrayList<>();
//		
//		if(!activeList.isEmpty()) {
//			blockMailService.senEndDayWarningMail(activeList);
//		} else {
//			closedList = blockDateRangeService.getBlockOfParameter(1546608600000l, 1546608600000l);
//			if(closedList.isEmpty()) {
//				blockMailService.sendEndDayNoBlockMail();
//			}
//		}
	}
//	
//	@RequestMapping(value ="/wsdl" , produces = {MediaType.APPLICATION_XML_VALUE})
//	public @ResponseBody ResponseEntity<byte[]> getWsdl(HttpServletRequest request) throws Exception { 
//		File dene = new File("D:\\contApiWS.wsdl");
//
//		ResponseEntity<byte[]> respEntity = null;
//		if(dene.exists()) {
//			InputStream inputStream = new FileInputStream(dene);
//			
//			byte[] out = IOUtils.toByteArray(inputStream);
//			
//			HttpHeaders responseHeaders = new HttpHeaders();
//			
//	        responseHeaders.add("content-disposition", "inline");
//	        
//	        respEntity = new ResponseEntity<byte[]>(out, responseHeaders, HttpStatus.OK);
//	        
//	        return respEntity;
//		} else {
//			return new ResponseEntity<byte[]> ("File Not Found".getBytes(), HttpStatus.OK);
//		}
//	}
//	
////	@RequestMapping(value = "{path}", produces = {MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE })
////	public String getXSD() throws Exception {
////		return "OK - PATH";
////	}
////	
////	@RequestMapping(value = "{path}" , params= {"wsdl", "interface"}, produces = {MediaType.APPLICATION_XML_VALUE} )
////	public String getXSD2() throws Exception {
////		return "OK - WSDL - INTERFACE";
////	}
////	
//	@RequestMapping(value = "{path}" , params= {"wsdl"}, produces = {MediaType.APPLICATION_XML_VALUE} ) 
//	public ResponseEntity<byte[]> getTarih() throws Exception {
//		
//		
////		WSDLReaderImpl impl = new WSDLReaderImpl();
////		DefinitionImpl def = (DefinitionImpl) impl.readWSDL("D:\\contApiWS.wsdl");
////		DefinitionImpl defWrite = new DefinitionImpl();
////		
////		for(Object entry : def.getBindings().keySet()) {
////			BindingImpl binding = (BindingImpl) def.getBinding((QName)entry);
////			defWrite.addBinding(binding);
////		}
////		
////		for(Object entry : def.getMessages().keySet()) {
////			MessageImpl message = (MessageImpl) def.getMessage((QName)entry);
////			defWrite.addMessage(message);
////		}
////		
////		for(Object entry : def.getPortTypes().keySet()) {
////			PortTypeImpl portType = (PortTypeImpl) def.getPortType((QName)entry);
////			defWrite.addPortType(portType);
////		}
////		
////		for(Object entry: def.getServices().keySet()) {
////			ServiceImpl service = (ServiceImpl) def.getService((QName)entry);
////			for(Object portEntry:service.getPorts().keySet()) {
////				PortImpl pImpl = (PortImpl)service.getPort((String)portEntry);
////				List<Object> soapAddressList = (List<Object>)pImpl.getExtensibilityElements();
////				for(Object rs:soapAddressList) {
////					SOAPAddressImpl soapAddress = (SOAPAddressImpl)rs;
////					soapAddress.setLocationURI("http://localhost:8080/getWsdl");
////				}
////			}
////			defWrite.addService(service);
////		}
////		
////		defWrite.setQName(def.getQName());
////		
////		for(Object entry: def.getNamespaces().keySet()) {
////			defWrite.addNamespace((String)entry, (String)def.getNamespace((String)entry));
////		}
////		
////		defWrite.setTargetNamespace(def.getTargetNamespace());
////		defWrite.setExtensionRegistry(def.getExtensionRegistry());
////		
////		defWrite.setTypes(def.getTypes());
////		
////		for(Object entry: def.getImports().keySet()) {
////			Vector<Object> dVector = (Vector<Object>)def.getImports((String) entry);
////			for(Object rs : dVector) {
////				ImportImpl dImport  = (ImportImpl) rs;
////				defWrite.addImport(dImport);
////			}
////		}
////	
////		ByteArrayOutputStream out = new ByteArrayOutputStream();
////		WSDLWriterImpl write =  new WSDLWriterImpl();
////		write.writeWSDL(defWrite, out);
////		
////		HttpHeaders responseHeaders = new HttpHeaders();
////	    responseHeaders.add("Content-Type", 
////			        MediaType.APPLICATION_XML_VALUE);
////	    
////		return new ResponseEntity<byte[]>(out.toByteArray(), HttpStatus.OK);
//		
//		WSDLReaderImpl impl = new WSDLReaderImpl();
//		DefinitionImpl def = (DefinitionImpl) impl.readWSDL("D:\\contApiWS.wsdl");
//		DefinitionImpl defWrite = new DefinitionImpl();
//		
//		String bindingName = "";
//		for(Object entry : def.getBindings().keySet()) {
//			BindingImpl binding = (BindingImpl) def.getBinding((QName)entry);
//			bindingName = binding.getQName().getLocalPart();
//			defWrite.addBinding(binding);
//		}
//		
//		for(Object entry : def.getMessages().keySet()) {
//			MessageImpl message = (MessageImpl) def.getMessage((QName)entry);
//			defWrite.addMessage(message);
//		}
//		
//		for(Object entry : def.getPortTypes().keySet()) {
//			PortTypeImpl portType = (PortTypeImpl) def.getPortType((QName)entry);
//			defWrite.addPortType(portType);
//		}
//		
//		for(Object entry: def.getServices().keySet()) {
//			ServiceImpl service = (ServiceImpl) def.getService((QName)entry);
//			for(Object portEntry:service.getPorts().keySet()) {
//				PortImpl pImpl = (PortImpl)service.getPort((String)portEntry);
//				List<Object> soapAddressList = (List<Object>)pImpl.getExtensibilityElements();
//				for(Object rs:soapAddressList) {
//					SOAPAddressImpl soapAddress = (SOAPAddressImpl)rs;
//					soapAddress.setLocationURI("http://localhost:9090/");
//				}
//			}
//			defWrite.addService(service);
//		}
//		
//		defWrite.setQName(def.getQName());
//		
//		for(Object entry: def.getNamespaces().keySet()) {
//			defWrite.addNamespace((String)entry, (String)def.getNamespace((String)entry));
//		}
//		
//		defWrite.setTargetNamespace(def.getTargetNamespace());
//		defWrite.setExtensionRegistry(def.getExtensionRegistry());
//		
//		defWrite.setTypes(def.getTypes());
//		
//		String localationUrl = "?WSDL&interface="+bindingName;
//		for(Object entry: recursiveAddImport(def, localationUrl, 1).getImports().keySet()) {
//			Vector<Object> dVector = (Vector<Object>)def.getImports((String) entry);
//			for(Object rs : dVector) {
//				defWrite.addImport((ImportImpl)rs);
//			}
//		}
//
//	
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		WSDLWriterImpl write =  new WSDLWriterImpl();
//		
//		write.writeWSDL(defWrite, out);
//		
//		HttpHeaders responseHeaders = new HttpHeaders();
//	    responseHeaders.add("Content-Type", 
//			        MediaType.APPLICATION_XML_VALUE);
//	    
//		return new ResponseEntity<byte[]>(out.toByteArray(), HttpStatus.OK);
//	}
//	
//	private DefinitionImpl recursiveAddImport(DefinitionImpl defs, String locationUrl,int part) {
//		
//		if(defs.getImports().isEmpty()) {
//			return defs;
//		}
//		
//		for(Object entry:defs.getImports().keySet()) {
//			Vector<Object> dVector = (Vector<Object>)defs.getImports((String)entry);
//			for(Object rs: dVector) {
//				ImportImpl dImport = (ImportImpl)rs;
//				System.out.println(dImport.getLocationURI());
//				dImport.setLocationURI(locationUrl+"&part=deneme_"+String.valueOf(part));
//				DefinitionImpl innerDefinition = recursiveAddImport((DefinitionImpl)dImport.getDefinition(),locationUrl,part+1);
//				dImport.setDefinition(innerDefinition);
//			}
//		}
//		
//		return defs;
//		
//	}
//	
//	@RequestMapping(value ="/getWsdl" , produces = {MediaType.APPLICATION_XML_VALUE})
//	public ResponseEntity<byte[]> getWsdlDefinition(@RequestBody String request) throws Exception { 
//		HttpURLConnection connection = null;
//		StringBuilder response = new StringBuilder();
//		
//		HttpHeaders responseHeaders = new HttpHeaders();
//	    responseHeaders.add("Content-Type", 
//			        MediaType.APPLICATION_XML_VALUE);
//	    
//		  try {
//		    //Bağlantıyı aç
//		    URL url = new URL("http://contapiservicetest.turkcell.com.tr:8890/contapiservice/ws/contApiWS");
//		    connection = (HttpURLConnection) url.openConnection();
//		    connection.setRequestMethod("POST");
//		    connection.setRequestProperty("Content-Type", 
//		        MediaType.APPLICATION_XML_VALUE); 
//
//		    connection.setUseCaches(false);
//		    connection.setDoOutput(true);
//		    
//	
//		    WSDLReaderImpl reader = new WSDLReaderImpl();
//		    DefinitionImpl def = (DefinitionImpl)reader.readWSDL(request);
//		    
//		    //Requesti yaz
//		    DataOutputStream wr = new DataOutputStream (
//		        connection.getOutputStream());
//		    wr.writeBytes(request);
//		    wr.close();
//
//		    //Dönen cevabı yapıştır.  
//		    InputStream is = connection.getInputStream();
//		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//		    String line;
//		    while ((line = rd.readLine()) != null) {
//		      response.append(line);
//		      response.append('\r');
//		    }
//		    rd.close();
//		  } catch (Exception e) {
//		    return new ResponseEntity<byte[]>("Deneme".getBytes(), responseHeaders, HttpStatus.OK);
//		  } finally {
//		    if (connection != null) {
//		      connection.disconnect();
//		    }
//		  }
//		  
//		  return new ResponseEntity<byte[]>(response.toString().getBytes(), responseHeaders, HttpStatus.OK);
//	}
//	
//
}
