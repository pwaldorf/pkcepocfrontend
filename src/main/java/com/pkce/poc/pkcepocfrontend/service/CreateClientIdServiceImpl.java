package com.pkce.poc.pkcepocfrontend.service;

import com.oint.poc.auth.OnyxIntegratorAuthClient;
import com.oint.poc.constants.OnyxIntegratorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    public String createClientId(String inputPayload) {

        //Get Token move to UI
        String token = authClient.getToken();

        return createClient(token, inputPayload);
    }

    private String createClient(String token, String inputPayload){
        logger.info("New Client Creation");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);
        headers.add("User-Agent", "Spring's RestTemplate" );

        HttpEntity<String> entity = new HttpEntity<>(inputPayload, headers);

        ResponseEntity<Object> response =null;

        try {
            response = restTemplate.postForEntity(clientCreateURL, entity, Object.class);
        }catch (Exception e) {
            logger.error("Exception occured while creating the client :{}", e.getMessage());
            throw new RuntimeException("Exception occured creating the client ", e);
        }

        String newClient = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTID);
        if (Objects.isNull(newClient)) {
            logger.error("Unable to create client");
            throw new RuntimeException("Unable to create client");
        }

        String newClientSecret = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.BODY_CLIENTSECRET);
        logger.info("New Client " + newClient + " Secret " + newClientSecret);
        return newClient;
    }
}
