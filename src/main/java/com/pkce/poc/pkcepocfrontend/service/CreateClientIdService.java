package com.pkce.poc.pkcepocfrontend.service;

import com.pkce.poc.pkcepocfrontend.model.NewClient;

public interface CreateClientIdService {
    Object createClientId(String token, NewClient newClient);
    String getTokenFromAuthCode(String authCode);
}
