package com.watchlist.backend.clients;

import com.watchlist.backend.entities.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FBClient {
    private static final Logger log = LoggerFactory.getLogger(FBClient.class);
    private static final String BASE_URL = "https://graph.facebook.com/";
    private static final String PATH_ME = "me";

    private final RestTemplate fbRestTemplate;

    public FBClient(RestTemplate fbRestTemplate) {
        this.fbRestTemplate = fbRestTemplate;
    }

    public boolean verifyToken(String email, String token) {
        String url = BASE_URL + PATH_ME;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("fields", "id,name,email")
                .queryParam("access_token", token);

        try {
            ResponseEntity<FBUser> responseEntity = fbRestTemplate
                    .getForEntity(uriBuilder.toUriString(), FBUser.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() &&
                    responseEntity.getBody() != null) {
                return responseEntity.getBody().getEmail().equals(email);
            }
        } catch (HttpClientErrorException | HttpServerErrorException |
                    UnknownHttpStatusCodeException e) {
            log.error("Facebook token verification failed: {}", e.getMessage());
            return false;
        }

        return false;
    }
}
