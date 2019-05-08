package com.turkcell.blockmail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BlockMailRestTemplateConfiguration {
	
	@Bean("restTemplateBean")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}
