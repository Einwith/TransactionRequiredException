package com.lixar.apba.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Properties specific to bridging community site.
 * <p>
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "bridgeresource", ignoreUnknownFields = false)
@Configuration
public class BridgeResourceProperties {

	private final CommunityCredentials communityCredentials = new CommunityCredentials();
	
	private final Cleanup cleanup = new Cleanup();

	private String version;
	
	private int timeoutMinutes;

	@Bean
	public CommunityCredentials getCommunityCredentials() {
		return this.communityCredentials;
	}

	public Cleanup getCleanup() {
		return cleanup;
	}
	
	public static class CommunityCredentials {
		String publicUrl;
		String url;
		String username;
		String password;

		public String getPublicUrl() {
			return publicUrl;
		}

		public void setPublicUrl(String publicUrl) {
			this.publicUrl = publicUrl;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getTimeoutMinutes() {
		return timeoutMinutes;
	}

	public void setTimeoutMinutes(int timeoutMinutes) {
		this.timeoutMinutes = timeoutMinutes;
	}

	public static class Cleanup {
		int expirationTimeout;
		String cron;
		public int getExpirationTimeout() {
			return expirationTimeout;
		}
		public void setExpirationTimeout(int expirationTimeout) {
			this.expirationTimeout = expirationTimeout;
		}
		public String getCron() {
			return cron;
		}
		public void setCron(String cron) {
			this.cron = cron;
		}
	}
}
