package com.turkcell.blockmail;

import java.util.Arrays;

import com.turkcell.blockmail.threadService.AutoBlockStartThreadService;
import com.turkcell.blockmail.threadService.document.ServiceHealthCheckDocument;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.turkcell.blockmail.threadService.AutoBlockControlThreadService;

@Configuration
public class BlockMailThreadConfiguration {
	
	@Autowired
	private AutoBlockStartThreadService autoBlockThreadService;
	
	@Bean
	public String startAutoBlockThreadService() {
		Thread autoBlockThread = new Thread(autoBlockThreadService);
		System.out.println(Thread.currentThread().getName() + " THREADLERRR BAŞLASINNNNN");
		autoBlockThread.start();
		return "OK";
	}

}
