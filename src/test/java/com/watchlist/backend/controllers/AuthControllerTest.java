package com.watchlist.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.security.JWTUtils;
import com.watchlist.backend.services.FBAuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AuthController.class)
@MockBean({ RestTemplate.class, JWTUtils.class })
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FBAuthService fbAuthService;

    private String jCredentials;

    @Before
    public void setup() {
        jCredentials = "{" +
                "\"name\": \"John Doe\"," +
                "\"email\": \"giovanni_fi05@live.com\"," +
                "\"picture\": \"https://example.com/1.png\"," +
                "\"token\": \"12345\"," +
                "\"auth_provider_id\": 1" +
                "}";
    }

    @Test
    public void loginSuccessful() throws Exception {

        //
        // Given
        LoginResponse loginSuccessful = new LoginResponse();
        loginSuccessful.setSuccess(true);


        //
        // When
        when(fbAuthService.login(argThat(userCredentials ->
                        userCredentials.getName().equals("John Doe") &&
                                userCredentials.getAuthProviderId() == 1L &&
                                userCredentials.getToken().equals("12345")
                )))
                .thenReturn(loginSuccessful);

        //
        // Then:
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jCredentials))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        LoginResponse receivedResponse = objectMapper.readValue(
                result,
                LoginResponse.class);

        assertTrue(
                "Login should be successful",
                receivedResponse.isSuccess());
    }

    @Test
    public void loginUnsuccessful() throws Exception {

        //
        // Given
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setSuccess(false);

        //
        // When
        when(fbAuthService.login(
                argThat(credentials -> credentials.getToken().equals("12345"))
            ))
            .thenReturn(loginResponse);

        //
        // Then:
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jCredentials))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        LoginResponse receivedResponse = objectMapper.readValue(
                result,
                LoginResponse.class);

        assertFalse(
                "Login should be unsuccessful",
                receivedResponse.isSuccess());
    }
}
