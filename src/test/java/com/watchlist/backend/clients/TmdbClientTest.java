package com.watchlist.backend.clients;

import com.watchlist.backend.model.Movie;
import com.watchlist.backend.security.JWTUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
@MockBean({
        JWTUtils.class,
        RestTemplate.class
})
public class TmdbClientTest {

    @Autowired
    private TmdbClient tmdbClient;

    @Test
    public void testGetDetails() throws IOException {
        Movie movie = tmdbClient.getMovie(550);
        Assert.assertEquals("Fight Club", movie.getTitle());
    }
}
