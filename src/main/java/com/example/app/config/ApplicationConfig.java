package com.example.app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.app.filter.AuthFilterAdmin;
import com.example.app.filter.AuthFilterAssistant;
import com.example.app.filter.AuthFilterCustomer;
import com.example.app.filter.AuthFilterOperator;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {
/* 
	public class WebMvcConfig implements WebMvcConfigurer {
		  @Override
		  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		    configurer.enable(); // デフォルトサーブレットへの転送機能を有効化
		  }
	}
*/	
	// バリデーションメッセージのカスタマイズ	
	@Override
	 public Validator getValidator() {
		 var validator = new LocalValidatorFactoryBean();
		 validator.setValidationMessageSource(messageSource());
		 return validator;
	 }
	 @Bean
	 public ResourceBundleMessageSource messageSource() {
		 var messageSource = new ResourceBundleMessageSource();
		 messageSource.setBasename("validation");
		 return messageSource;
	 }
	 @Bean
	 public FilterRegistrationBean<AuthFilterAdmin> authFilterAdmin() {
		 var bean = new FilterRegistrationBean<AuthFilterAdmin>(new AuthFilterAdmin());
		 bean.addUrlPatterns("/admin/*");
		 return bean;
	 } 
	 @Bean
	 public FilterRegistrationBean<AuthFilterAssistant> authFilterAssistant() {
		 var bean = new FilterRegistrationBean<AuthFilterAssistant>(new AuthFilterAssistant());
		 bean.addUrlPatterns("/assistant/*");
		 return bean;
	 } 
	 
	 @Bean
	 public FilterRegistrationBean<AuthFilterCustomer> authFilterCustomer() {
		 var bean = new FilterRegistrationBean<AuthFilterCustomer>(new AuthFilterCustomer());
		 bean.addUrlPatterns("/customer/*");
		 return bean;
	 } 
	 
	 @Bean
	 public FilterRegistrationBean<AuthFilterOperator> authFilterOperator() {
		 var bean = new FilterRegistrationBean<AuthFilterOperator>(new AuthFilterOperator());
		 bean.addUrlPatterns("/operation/*");
		 return bean;
	 }
	  
} 