package com.watchlist.backend.services;

import com.watchlist.backend.dao.UserHasWatchlistDao;
import com.watchlist.backend.dao.WatchlistDao;
import com.watchlist.backend.model.User;
import com.watchlist.backend.model.Watchlist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class WatchlistServiceTest {

    @Mock
    private WatchlistDao watchlistDao;

    @Mock
    private UserHasWatchlistDao hasWatchlistDao;

    @SuppressWarnings("unused")
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WatchlistService watchlistService;

    @Test
    public void testCreateDefaultList() {

        // Given
        User user = new User();

        // When
        watchlistService.createDefaultList(user);

        // Then
        Mockito
                .verify(watchlistDao, times(1))
                .save(argThat(watchlist -> watchlist.isDefault() &&
                        watchlist.getName().equals(Watchlist.DEFAULT_LIST_NAME)));
        Mockito
                .verify(hasWatchlistDao, times(1))
                .save(argThat(hasWatchlist ->
                        hasWatchlist.getUser().equals(user)
                ));
    }
}
