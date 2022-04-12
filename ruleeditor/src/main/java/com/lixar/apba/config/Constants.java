package com.lixar.apba.config;

/**
 * Application constants.
 */
public final class Constants {

    // Spring profile for development, production and "fast", see http://jhipster.github.io/profiles.html
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "dockerprod";
    public static final String SPRING_PROFILE_STAGING = "dockerstaging";
    public static final String SPRING_PROFILE_FAST = "fast";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    public static final String SPRING_PROFILE_CLOUD = "cloud";

    public static final String SYSTEM_ACCOUNT = "system";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private Constants() {
    }
}
