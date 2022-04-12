package com.lixar.apba.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.core.HazelcastInstance;
import com.lixar.apba.web.filter.CachingHttpHeadersFilter;
import com.lixar.apba.web.filter.StaticResourcesProductionFilter;
import org.apache.catalina.util.SessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.inject.Inject;
import javax.servlet.*;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@EnableHazelcastHttpSession
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    @Inject
    private Environment env;

    @Inject
    private JHipsterProperties props;

	@Inject
	private FirebaseConfiguration firebaseConfig;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    // Hazelcast instance is injected to force its initialization before the Servlet filter uses it.
    @SuppressWarnings("unused")
	@Inject
    private HazelcastInstance hazelcastInstance;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        if (!env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_FAST))) {
            initMetrics(servletContext, disps);
        }
        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_PRODUCTION))) {
            initCachingHttpHeadersFilter(servletContext, disps);
            initStaticResourcesProductionFilter(servletContext, disps);
        }
        log.info("Web application fully configured");
    }

    /**
     * Set up Mime types.
     */
    @Override
    public void customize(ConfigurableServletWebServerFactory container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
        mappings.add("html", "text/html;charset=utf-8");
        // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
        mappings.add("json", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
    }

    /**
     * Initializes the static resources production Filter.
     */
    private void initStaticResourcesProductionFilter(ServletContext servletContext,
                                                     EnumSet<DispatcherType> disps) {

        log.debug("Registering static resources production Filter");
        FilterRegistration.Dynamic staticResourcesProductionFilter =
            servletContext.addFilter("staticResourcesProductionFilter",
                new StaticResourcesProductionFilter());

        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/dist");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/dist/index.html");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/dist/assets/**");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/dist/scripts/**");
        staticResourcesProductionFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext,
                                              EnumSet<DispatcherType> disps) {
        log.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter =
            servletContext.addFilter("cachingHttpHeadersFilter",
                new CachingHttpHeadersFilter(env));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/dist/assets/**");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/dist/scripts/**");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE,
            metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY,
            metricRegistry);

        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
            new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = props.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
            source.registerCorsConfiguration("/oauth/**", config);
        }
        return new CorsFilter(source);
    }

	@Bean(name = "firebaseAppInit")
	public FirebaseApp firebaseAppInit() throws IOException {
		InputStream serviceAccount = new ClassPathResource(firebaseConfig.getKeyFileName()).getInputStream();

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl(firebaseConfig.getUrl())
				.build();

		FirebaseApp.initializeApp(options);
		return null;
	}

	@Bean
	@DependsOn("firebaseAppInit")
	public FirebaseDatabase firebaseDatabase() {
		return FirebaseDatabase.getInstance();
	}

	@Bean
	@DependsOn("firebaseAppInit")
	public FirebaseAuth firebaseAuth() {
		return FirebaseAuth.getInstance();
	}

    public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
        public SecurityInitializer() {
            super(SecurityConfig.class, SessionConfig.class);
        }
    }

    public class Initializer extends AbstractHttpSessionApplicationInitializer {
    }
}
