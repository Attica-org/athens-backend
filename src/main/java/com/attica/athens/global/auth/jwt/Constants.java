package com.attica.athens.global.auth.jwt;

public abstract class Constants {

    public static final String ACCESS_TOKEN = "access-token";

    public static final String REFRESH_TOKEN = "refresh-token";

    public static final String AUTHORITY_KEY = "id";

    public static final String AUTHORITY_ROLE = "role";

    public static final String COOKIE_NAME = "Refresh-Token";

    public static final int COOKIE_EXPIRATION_TIME = 24 * 60 * 60;

    public static final String AUTHORIZATION = "Authorization";

    public static final String BEARER_ = "Bearer ";

    public static final String REQUEST_ATTRIBUTE_NAME = "jwt exception";

}
