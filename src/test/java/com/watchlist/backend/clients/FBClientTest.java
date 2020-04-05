package com.watchlist.backend.clients;

import com.watchlist.backend.entities.FBUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
public class FBClientTest {

    private FBClient fbClient;

    @MockBean(name = "fbRestTemplate")
    private RestTemplate fbRestTemplate;

    @Before
    public void setup() {
        fbClient = new FBClient(fbRestTemplate);
    }

    @Test
    public void verifyValidToken() {
        // Given:
        String email = "test@example.com";
        String token = "valid_token";

        String expectedUrl = "https://graph.facebook.com/me" +
                "?fields=id,name,email" +
                "&access_token=" +
                token;

        FBUser fbUser = new FBUser();
        fbUser.setId("123456");
        fbUser.setEmail(email);

        ResponseEntity<FBUser> responseEntity = new ResponseEntity<>(
                fbUser,
                HttpStatus.OK);
        Mockito.when(fbRestTemplate.getForEntity(expectedUrl, FBUser.class))
                .thenReturn(responseEntity);

        // When:
        boolean verifyResult = fbClient.verifyToken(email, token);

        // Then:
        Assert.assertTrue("Token should be valid", verifyResult);
    }

    @Test
    public void verifyInvalidToken() {
        // Given:
        String email = "test@example.com";
        String token = "invalid_token";

        String expectedUrl = "https://graph.facebook.com/me" +
                "?fields=id,name,email" +
                "&access_token=" +
                token;

        FBUser fbUser = new FBUser();
        fbUser.setId("123456");
        fbUser.setEmail(email);

        ResponseEntity<FBUser> responseEntity = new ResponseEntity<>(
                fbUser,
                HttpStatus.BAD_REQUEST);
        Mockito.when(fbRestTemplate.getForEntity(expectedUrl, FBUser.class))
                .thenReturn(responseEntity);

        // When:
        boolean verifyResult = fbClient.verifyToken(email, token);

        // Then:
        Assert.assertFalse("Token should be invalid", verifyResult);
    }
}
