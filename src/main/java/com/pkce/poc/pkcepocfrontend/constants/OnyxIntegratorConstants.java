package com.pkce.poc.pkcepocfrontend.constants;

public class OnyxIntegratorConstants {

    private OnyxIntegratorConstants() {

    }

    /* Spring security constants */
    public static final String ROLE_PREFIX = "ROLE_";

    /* Constants for resource permissions. */
    public static final String LIST_VIEW = "ROLE_USER:MY_RESOURCE:TEST:VIEW";

    /* constants for token */
    public static final String TOKEN_STARTS_WITH_BEARER = "Bearer ";
    public static final String AUTH_GRANT_TYPE = "grant_type";
    public static final String AUTH_CLIENT_ID = "client_id";
    public static final String AUTH_CLIENT_SECRET = "client_secret";
    public static final String AUTH_SCOPE = "scope";
    public static final String AUTH_CLIENT_CREDENTIALS = "client_credentials";
    public static final String AUTH_OPEN_ID = "openid";
    public static final String AUTH_PROFILE = "profile";
    public static final String AUTH_ID_TOKEN = "id_token";
    public static final String AUTH_ACCESS_TOKEN = "access_token";

    /* For enriching the header*/
    public static final String BODY_CLIENTID = "clientId";
    public static final String BODY_CLIENTSECRET = "secret";
}