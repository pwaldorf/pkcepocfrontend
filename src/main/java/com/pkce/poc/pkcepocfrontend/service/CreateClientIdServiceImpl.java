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

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    @Value("${keycloak.client.redirect-uri}")
    private String redirectUri;

    @Value("${keycloak.token.url}")
    private String tokenURL;

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
        if (Objects.isNull(returnClient)) {
            logger.error("Unable to create client");
            throw new RuntimeException("Unable to create client");
        }

        String newClientSecret = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTSECRET);
        logger.info("Return Client " + returnClient + " Secret " + newClientSecret);
        return response.getBody();
    }

    public String getTokenFromAuthCode(String authCode){
        logger.info("Get Token From Auth Code");

        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
        request.add(OnyxIntegratorConstants.AUTH_GRANT_TYPE, OnyxIntegratorConstants.AUTH_AUTHORIZATION_CODE);
        request.add(OnyxIntegratorConstants.AUTH_CLIENT_ID, clientId);
        //request.add(OnyxIntegratorConstants.AUTH_CLIENT_SECRET, clientSecret);
        request.add(OnyxIntegratorConstants.AUTH_CODE, authCode);
        request.add(OnyxIntegratorConstants.AUTH_REDIRECT_URI, "http://192.168.4.64:20001/callback");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Object> response =null;

        try {
            response = restTemplate.postForEntity(tokenURL, entity, Object.class);
            logger.info("Auth Token: " + response);
        }catch (Exception e) {
            logger.error("Exception occurred while retrieving Auth token :{}", e.getMessage());
            throw new RuntimeException("Exception occurred retrieving Auth token ", e);
        }

       // String returnClient = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTID);
       // if (Objects.isNull(returnClient)) {
       //     logger.error("Unable to create client");
       //     throw new RuntimeException("Unable to create client");
       // }

        String accessToken = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.AUTH_ACCESS_TOKEN);
        return accessToken;
    }

}
