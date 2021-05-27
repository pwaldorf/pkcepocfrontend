package com.pkce.poc.pkcepocfrontend.auth;

import com.pkce.poc.pkcepocfrontend.constants.OnyxIntegratorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
public class OnyxIntegratorAuthClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${keycloak.token.url}")
    private String tokenURL;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    //@Cacheable(value = "bearerTokenMap", key="#userId.concat('_').concat(#appAction)")
    public String getToken() {
        return generateToken();
    }

    @SuppressWarnings("unchecked")
    private String generateToken(){
        logger.info("Generating the token for client id {}", clientId);

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
        try {
            response = restTemplate.exchange(tokenURL, HttpMethod.POST, entity, Object.class);
        }catch (Exception e) {
            logger.error("Exception occured while generating the token :{}", e.getMessage());
            throw new RuntimeException("Exception occured while generating the token ", e);
        }

        String idToken = ((Map<String, String>)response.getBody()).get(OnyxIntegratorConstants.AUTH_ACCESS_TOKEN);
        if (Objects.isNull(idToken)) {
            logger.error("Unable to generate the token for client {}",  clientId);
            throw new RuntimeException("Unble to generate the token for client "+clientId);
        }

        logger.info("bearertoken " + idToken);
        return idToken;
    }
}
