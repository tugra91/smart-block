package com.turkcell.blockmail.util;

import org.apache.commons.lang3.StringUtils;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdImport;
import org.xmlet.xsdparser.xsdelements.XsdSchema;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.stream.Stream;

public class XSDParseUtil {



    public static void deneme() {

//        URL url = new URL("http://inka-om-stb.turkcell.com.tr/ProductIdentifierQuery/ProductIdentifierQuery_v1.0?xsd=1");
//        URLConnection connection = url.openConnection();
//
//        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
//
//        ByteArrayOutputStream output2 = new ByteArrayOutputStream();
//
//        byte dataBuffer[] = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//            output2.write(dataBuffer, 0, bytesRead);
//        }

//            File file = new File("ds.xsd");
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(output2.toByteArray());


        String messageName = "getProductTypeByMsisdnRequest";

        XsdParser parser = new XsdParser("ds.xsd");
        Stream<XsdElement> elementStream = parser.getResultXsdElements();

        Stream<XsdSchema> xsdStream = parser.getResultXsdSchemas();




        Optional<XsdElement> oElement = elementStream
                .filter(s -> StringUtils.equalsIgnoreCase(s.getName(), messageName))
                .findFirst();

        XsdElement element = null;
        if(oElement.isPresent()) {
            element = oElement.get();
        }

        getXsdComplexType(xsdStream.findFirst().get().getChildrenComplexTypes(), "getProductTypeByMsisdnRequest");

        if(element.getXsdSimpleType() == null && element.getXsdComplexType() == null) {

        }


    }

    public static String getElement() {
        return "OK";
    }

    public static String getComplexType() {
        return "OK";
    }

    public static String getSimpleType() {
        return "OK";
    }

    private static String getImportLocationFromChildImports(Stream<XsdImport> xsdImports, String namespace) {
        String xsdLocation = null;

        Optional<XsdImport> oXsdImport = xsdImports
                .filter(s -> StringUtils.equalsIgnoreCase(s.getNamespace(), namespace))
                .findFirst();


        if(oXsdImport.isPresent()) {
            xsdLocation = oXsdImport.get().getSchemaLocation();
        }

        return xsdLocation;
    }

    private static XsdComplexType getXsdComplexType(Stream<XsdComplexType> xsdComplexTypes, String complexName) {
        XsdComplexType xsdComplexType = null;


        Optional<XsdComplexType> complexType = xsdComplexTypes
                .filter(s -> StringUtils.equalsIgnoreCase(s.getName(), complexName))
                .findFirst();

        if(complexType.isPresent()) {
            xsdComplexType =complexType.get();
        }

        xsdComplexType.getElements();

        return xsdComplexType;
    }

}
