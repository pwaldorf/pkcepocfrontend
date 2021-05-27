package com.pkce.poc.pkcepocfrontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequestScope
//@RequiredArgsConstructor
public class KeycloakLinkGenerator {

    private static final String CLIENT_LINK_TEMPLATE = "%s/realms/%s/clients/%s/redirect";

    @Value("${server.base.url}")
    private String issuer;

    public String createAccountLinkWithBacklink(String backlinkUri) {

        UriComponentsBuilder accountUri = UriComponentsBuilder
                .fromHttpUrl(issuer).path("/" + backlinkUri);
//		UriComponentsBuilder accountUri = UriComponentsBuilder
//				.fromHttpUrl(keycloakSecurityContext.getToken().getIssuer()).path("/account")
//				.queryParam("referrer", keycloakProperties.getResource()).queryParam("referrer_uri", backlinkUri);

        return accountUri.toUriString();
    }
}
