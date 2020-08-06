package com.watchlist.backend;

import com.watchlist.backend.security.JWTUtils;
import com.watchlist.backend.services.WatchlistService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@MockBean({
        JWTUtils.class,
        RestTemplate.class
})
class BackendApplicationTests {

  @Test
  void contextLoads() {}
}
