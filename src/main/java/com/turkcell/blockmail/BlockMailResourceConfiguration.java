package com.turkcell.blockmail;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class BlockMailResourceConfiguration implements WebMvcConfigurer {

	
	@Override
	public void addResourceHandlers (ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index.html");
		registry.addViewController("/blockSave").setViewName("forward:/index.html");
		registry.addViewController("/searchBlock").setViewName("forward:/index.html");
		registry.addViewController("/todayBlock").setViewName("forward:/index.html");
		registry.addViewController("/weekBlock").setViewName("forward:/index.html");
		registry.addViewController("/monthBlock").setViewName("forward:/index.html");
		registry.addViewController("/uptimeService").setViewName("forward:/index.html");
		registry.addViewController("/blockUpdate/{id}").setViewName("forward:/index.html");
		registry.addViewController("/admin/addService").setViewName("forward:/index.html");
		registry.addViewController("/login").setViewName("forward:/index.html");
		registry.addViewController("/serviceHealth").setViewName("forward:/index.html");
	}
	

}
