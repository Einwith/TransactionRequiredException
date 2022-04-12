package com.lixar.apba.config;

import java.util.Collections;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.lixar.apba.web.dialect.LixarDialect;

@Configuration
public class ThymeleafConfiguration {

	private final Logger log = LoggerFactory.getLogger(ThymeleafConfiguration.class);

	@Bean
	@Description("Thymeleaf template resolver serving HTML emails")
	public ClassLoaderTemplateResolver emailTemplateResolver() {
		ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
		emailTemplateResolver.setPrefix("mails/");
		emailTemplateResolver.setSuffix(".html");
		emailTemplateResolver.setForceTemplateMode(true);
		emailTemplateResolver.setResolvablePatterns(Collections.singleton("html/*"));
		emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
		emailTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
		emailTemplateResolver.setOrder(1);
		emailTemplateResolver.setCacheable(true);
		return emailTemplateResolver;
	}

	@Bean
	@Description("Spring mail message resolver")
	public MessageSource emailMessageSource() {
		log.info("loading non-reloadable mail messages resources");
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/mails/messages/messages");
		messageSource.setDefaultEncoding(CharEncoding.UTF_8);
		return messageSource;
	}

	@Bean
	public LixarDialect lixarDialect() {
		return new LixarDialect("LixarDialect", "lixar", 1);
	}
}
