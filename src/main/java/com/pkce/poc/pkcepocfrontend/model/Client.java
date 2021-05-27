package com.pkce.poc.pkcepocfrontend.model;

import java.io.Serializable;

public class Client implements Serializable {

    private String clientId;
    private String initialAccessToken;
    private String clientSecret;

    public Client() {
    }

    public Client(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getInitialAccessToken() {
        return initialAccessToken;
    }

    public void setInitialAccessToken(String initialAccessToken) {
        this.initialAccessToken = initialAccessToken;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
