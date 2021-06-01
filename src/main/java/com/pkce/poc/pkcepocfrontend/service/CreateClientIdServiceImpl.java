package com.pkce.poc.pkcepocfrontend.service;

import com.pkce.poc.pkcepocfrontend.constants.OnyxIntegratorConstants;
import com.pkce.poc.pkcepocfrontend.model.Client;
import com.pkce.poc.pkcepocfrontend.model.NewClient;
import com.pkce.poc.pkcepocfrontend.utils.PkceUtil;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CreateClientIdServiceImpl implements CreateClientIdService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PkceUtil pkceUtil;

    @Value("${keycloak.client.create.url}")
    private String clientCreateURL;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.redirect.url}")
    private String redirectUri;

    @Value("${keycloak.token.url}")
    private String tokenURL;

    @Value("${keycloak.auth.url}")
    private String authURL;

    @Override
    public String getAuthenticationUrl(Client client){
        logger.info("Get Authentication URL");

        String codeVerifier = null;
        try {
            codeVerifier = pkceUtil.generateCodeVerifier();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.setCodeVerifier(codeVerifier);

        String codeChallenge = null;
        try {
            codeChallenge = pkceUtil.generateCodeChallange(codeVerifier);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        logger.info("Code challenge = " + codeChallenge);
        logger.info("Code verifier = " + codeVerifier);

        Map<String, String> params = new HashMap<>();
        params.put(OnyxIntegratorConstants.AUTH_RESPONSE_TYPE,OnyxIntegratorConstants.AUTH_CODE);
        params.put(OnyxIntegratorConstants.AUTH_CLIENT_ID, clientId);
        params.put(OnyxIntegratorConstants.AUTH_REDIRECT_URI, redirectUri);
        params.put(OnyxIntegratorConstants.AUTH_SCOPE, OnyxIntegratorConstants.AUTH_PROFILE);
        params.put(OnyxIntegratorConstants.AUTH_STATE, "123456");
        params.put(OnyxIntegratorConstants.AUTH_CODE_CHALLENGE, codeChallenge);
        params.put(OnyxIntegratorConstants.AUTH_CODE_CHALLENGE_METHOD, "S256");

        String encodedURL = params.entrySet().stream()
                .map(entry -> {
                    try {
                        return entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return "";
                })
                .collect(Collectors.joining("&", authURL + "?", ""));
        logger.info("Auth URL: " + encodedURL);


        return encodedURL;
    }

    @Override
    public String getTokenFromAuthCode(String authCode, String codeVerifier){
        logger.info("Get Token From Auth Code");

        logger.info("Auth Code: " + authCode);
        logger.info("Code Verifier: " + codeVerifier);
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add(OnyxIntegratorConstants.AUTH_GRANT_TYPE, OnyxIntegratorConstants.AUTH_AUTHORIZATION_CODE);
        request.add(OnyxIntegratorConstants.AUTH_CLIENT_ID, clientId);
        request.add(OnyxIntegratorConstants.AUTH_CODE, authCode);
        request.add(OnyxIntegratorConstants.AUTH_REDIRECT_URI, redirectUri);
        request.add(OnyxIntegratorConstants.AUTH_CODE_VERIFIER, codeVerifier);
        request.add(OnyxIntegratorConstants.AUTH_SCOPE, OnyxIntegratorConstants.AUTH_PROFILE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Object> response =null;

        try {
            response = restTemplate.postForEntity(tokenURL, entity, Object.class);
        }catch (Exception e) {
            logger.error("Exception occurred while retrieving Auth token :{}", e.getMessage());
            throw new RuntimeException("Exception occurred retrieving Auth token ", e);
        }

        return ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.AUTH_ACCESS_TOKEN);
    }

    @Override
    public Object createClientId(String token, NewClient newClient){
        logger.info("New Client Creation");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);
        headers.add("User-Agent", "Spring's RestTemplate" );

        HttpEntity<NewClient> entity = new HttpEntity<>(newClient, headers);

        ResponseEntity<Object> response;

        try {
            response = restTemplate.postForEntity(clientCreateURL, entity, Object.class);
        }catch (Exception e) {
            logger.error("Exception occured while creating the client :{}", e.getMessage());
            throw new RuntimeException("Exception occured creating the client ", e);
        }

        String returnClient = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTID);
        if (Objects.isNull(returnClient)) {
            logger.error("Unable to create client");
            throw new RuntimeException("Unable to create client");
        }

        String returnClientSecret = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTSECRET);
        logger.info("New Client " + returnClient + " Secret " + returnClientSecret);
        return response.getBody();
    }
}
