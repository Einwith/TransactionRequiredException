package com.lixar.apba.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "firebase", ignoreUnknownFields = false)
@Configuration
public class FirebaseConfiguration {
	private int secondsUntilTokenExpired;
	private String keyFileName;
	private String url;
	private String apiKey;
	private String projectId;

	public int getSecondsUntilTokenExpired() {
		return secondsUntilTokenExpired;
	}

	public void setSecondsUntilTokenExpired(int secondsUntilTokenExpired) {
		this.secondsUntilTokenExpired = secondsUntilTokenExpired;
	}

	public String getKeyFileName() {
		return keyFileName;
	}

	public void setKeyFileName(String keyFileName) {
		this.keyFileName = keyFileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
