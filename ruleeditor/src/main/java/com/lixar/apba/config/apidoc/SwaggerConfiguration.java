package com.lixar.apba.config.apidoc;

import com.lixar.apba.config.Constants;
import com.lixar.apba.config.JHipsterProperties;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Springfox Swagger configuration.
 *
 * Warning! When having a lot of REST endpoints, Springfox can become a performance issue. In that
 * case, you can use a specific Spring profile for this class, so that only front-end developers
 * have access to the Swagger view.
 */
@Configuration
@EnableSwagger2
@Profile({Constants.SPRING_PROFILE_STAGING, Constants.SPRING_PROFILE_DEVELOPMENT})
public class SwaggerConfiguration {

    private final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

    /**
     * Swagger Springfox configuration.
     */
    @Bean
    @Profile("!" + Constants.SPRING_PROFILE_FAST)
    public Docket swaggerSpringfoxDocket(JHipsterProperties jHipsterProperties) {
        log.debug("Starting Swagger");
        StopWatch watch = new StopWatch();
        watch.start();
        ApiInfo apiInfo = new ApiInfo(
            jHipsterProperties.getSwagger().getTitle(),
            jHipsterProperties.getSwagger().getDescription(),
            jHipsterProperties.getSwagger().getVersion(),
            jHipsterProperties.getSwagger().getTermsOfServiceUrl(),
            jHipsterProperties.getSwagger().getContact(),
            jHipsterProperties.getSwagger().getLicense(),
            jHipsterProperties.getSwagger().getLicenseUrl());

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
        	.useDefaultResponseMessages(false)
            .apiInfo(apiInfo)
            .genericModelSubstitutes(ResponseEntity.class)
            .forCodeGeneration(true)
            .genericModelSubstitutes(ResponseEntity.class)
            .ignoredParameterTypes(Pageable.class, HttpSession.class, Model.class)
            .directModelSubstitute(java.time.LocalDate.class, String.class)
            .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
            .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.lixar.apba"))
            .build();
        watch.stop();
        log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        return docket;
    }
}
