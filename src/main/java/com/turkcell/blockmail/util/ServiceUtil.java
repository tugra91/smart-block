package com.turkcell.blockmail.util;


import com.google.gson.Gson;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import com.turkcell.blockmail.threadService.model.HealthCheckServiceModel;
import org.bson.internal.Base64;
import org.springframework.http.MediaType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServiceUtil {

    public static HttpURLConnection getConnection(HealthCheckServiceModel serviceModel) throws IOException {
        HttpURLConnection connection;
        try {
            URL url = new URL(serviceModel.getServiceURL());
            connection = (HttpURLConnection) url.openConnection();


            if (serviceModel.isHasAuth()) {
                String authInfo = serviceModel.getUserName() + ":" + serviceModel.getPassword();
                connection.setRequestProperty("Authorization", "Basic " + Base64.encode(authInfo.getBytes()));
            }

            if (serviceModel.getServiceType().intValue() == 0) {

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        MediaType.TEXT_XML_VALUE);
                connection.setConnectTimeout(serviceModel.getSlaTime().intValue());
                connection.setRequestProperty("SOAPAction", serviceModel.getSoapAction());
                connection.setRequestProperty("Content-Length", String.valueOf(serviceModel.getRequest().length()));
                connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setUseCaches(false);
                connection.setDoOutput(true);


            } else if (serviceModel.getServiceType().intValue() == 1) {
                connection.setRequestMethod(serviceModel.getMethodType());
                connection.setRequestProperty("Content-Type",
                        MediaType.APPLICATION_JSON_VALUE);
            }

            /*
                Create Input
                 */
            OutputStream os = connection.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);
            wr.write(serviceModel.getRequest().getBytes());
            wr.flush();
            wr.close();
            os.close();

        } catch (MalformedURLException e) {
            throw(e);
        } catch (ProtocolException e) {
            throw(e);
        } catch (IOException e) {
            throw(e);
        } catch (Exception e) {
            throw(e);
        }

        return connection;
    }


    public static HealthCheckServiceModel convertServiceDocToModel(ServiceHealthCheckDocument serviceDoc) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(serviceDoc), HealthCheckServiceModel.class);
    }

    public static ServiceHealthCheckDocument convertServiceModelToDoc (HealthCheckServiceModel serviceModel) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(serviceModel), ServiceHealthCheckDocument.class);
    }
}
