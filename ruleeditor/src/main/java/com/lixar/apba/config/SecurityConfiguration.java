package com.lixar.apba.config;

import javax.inject.Inject;

import com.lixar.apba.security.AuthoritiesConstants;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

	@Inject
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Inject
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}

	@Configuration
	@Order(1)
	public static class OAuthAndNonSpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring()
					.antMatchers("/scripts/**/*.{js,html}")
					.antMatchers("/bower_components/**")
					.antMatchers("/i18n/**")
					.antMatchers("/assets/**")
					.antMatchers("/swagger-ui/index.html")
					.antMatchers("/api/register")
					.antMatchers("/api/activate")
					.antMatchers("/api/account/reset_password/init")
					.antMatchers("/api/account/reset_password/finish")
					.antMatchers("/test/**")
					// Legacy API
					.antMatchers("/bridge/**")
					.antMatchers("/create_bridge/**")
					.antMatchers("/join/**")
					.antMatchers("/games/**")
					.antMatchers("/chat/**");
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http
					.httpBasic().realmName("ruleeditor")
					.and()
					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
					.requestMatchers().antMatchers("/oauth/authorize")
					.and()
					.authorizeRequests()
					.antMatchers("/oauth/authorize").authenticated();
		}

		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
	}

	@Configuration
	public static class HttpBasicSecurityConfiguration extends WebSecurityConfigurerAdapter {

		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().headers().addHeaderWriter(
					new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));

			http
					.antMatcher("/game/**")
					.authorizeRequests()
						.anyRequest().hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.COMMUNITY_SERVER)
						.and()
					.httpBasic();
		}
	}
}
