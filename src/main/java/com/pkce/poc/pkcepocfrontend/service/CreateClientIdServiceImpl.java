package com.pkce.poc.pkcepocfrontend.service;

import com.pkce.poc.pkcepocfrontend.auth.OnyxIntegratorAuthClient;
import com.pkce.poc.pkcepocfrontend.constants.OnyxIntegratorConstants;
import com.pkce.poc.pkcepocfrontend.model.Client;
import com.pkce.poc.pkcepocfrontend.model.NewClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
public class CreateClientIdServiceImpl implements CreateClientIdService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OnyxIntegratorAuthClient authClient;

    @Value("${keycloak.client.create.url}")
    private String clientCreateURL;

    @Override
    public Object createClientId(String token, NewClient newClient) {

        //String authCode = authClient.getAuthCode();

        return createClient(token, newClient);
    }

    private Object createClient(String token, NewClient newClient){
        logger.info("New Client Creation");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);
        headers.add("User-Agent", "Spring's RestTemplate" );

        HttpEntity<NewClient> entity = new HttpEntity<>(newClient, headers);

        ResponseEntity<Object> response =null;

        try {
            response = restTemplate.postForEntity(clientCreateURL, entity, Object.class);
            logger.info("New Client: " + response);
        }catch (Exception e) {
            logger.error("Exception occurred while creating the client :{}", e.getMessage());
            throw new RuntimeException("Exception occurred creating the client ", e);
        }

        String returnClient = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTID);
        if (Objects.isNull(newClient)) {
            logger.error("Unable to create client");
            throw new RuntimeException("Unable to create client");
        }

        String newClientSecret = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTSECRET);
        logger.info("Return Client " + returnClient + " Secret " + newClientSecret);
        return response.getBody();
    }
/*
    private Object getAuthCode(){
        logger.info("Get Auth Code");

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add(OnyxIntegratorConstants.AUTH_GRANT_TYPE, OnyxIntegratorConstants.AUTH_CLIENT_CREDENTIALS);
        request.add(OnyxIntegratorConstants.AUTH_CLIENT_ID, clientId);
        request.add(OnyxIntegratorConstants.AUTH_CLIENT_SECRET, clientSecret);
        request.add(OnyxIntegratorConstants.AUTH_SCOPE, OnyxIntegratorConstants.AUTH_PROFILE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(request, headers);
        ResponseEntity<Object> response =null;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Object> response =null;

        try {
            response = restTemplate.postForEntity(clientCreateURL, entity, Object.class);
            logger.info("New Client: " + response);
        }catch (Exception e) {
            logger.error("Exception occurred while creating the client :{}", e.getMessage());
            throw new RuntimeException("Exception occurred creating the client ", e);
        }

        String returnClient = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTID);
        if (Objects.isNull(newClient)) {
            logger.error("Unable to create client");
            throw new RuntimeException("Unable to create client");
        }

        String newClientSecret = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTSECRET);
        logger.info("Return Client " + returnClient + " Secret " + newClientSecret);
        return response.getBody();
    }
    */

}
