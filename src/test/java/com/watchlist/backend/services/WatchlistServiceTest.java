package com.watchlist.backend.services;

import com.watchlist.backend.dao.UserHasWatchlistDao;
import com.watchlist.backend.dao.WatchlistDao;
import com.watchlist.backend.model.User;
import com.watchlist.backend.model.UserHasWatchlist;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.model.WatchlistPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class WatchlistServiceTest {

    @Mock
    private WatchlistDao watchlistDao;

    @Mock
    private UserHasWatchlistDao hasWatchlistDao;

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

    @Test
    public void testCreate() {

        // Given

        long userId = 150001L;
        Watchlist watchlist = new Watchlist();
        watchlist.setName("Test watchlist for creation method");

        WatchlistPermission ownerPermission = new WatchlistPermission();
        ownerPermission.setId(WatchlistPermission.OWNER_ID);
        Mockito
                .when(entityManager.getReference(
                        WatchlistPermission.class,
                        WatchlistPermission.OWNER_ID)
                )
                .thenReturn(ownerPermission);

        User owner = new User();
        owner.setId(userId);
        Mockito
                .when(entityManager.getReference(
                        User.class,
                        userId
                ))
                .thenReturn(owner);


        // When

        watchlistService.create(watchlist, userId);


        // Then

        Mockito
                .verify(watchlistDao, times(1))
                .save(eq(watchlist));

        ArgumentCaptor<UserHasWatchlist> hasWatchlistCaptor = ArgumentCaptor
                .forClass(UserHasWatchlist.class);
        Mockito
                .verify(hasWatchlistDao, times(1))
                .save(hasWatchlistCaptor.capture());

        UserHasWatchlist capturedHasWatchlist = hasWatchlistCaptor.getValue();
        assertEquals(capturedHasWatchlist.getUser().getId(), userId);
        assertEquals(capturedHasWatchlist.getWatchlist(), watchlist);
        assertEquals(
                capturedHasWatchlist.getPermission().getId(),
                WatchlistPermission.OWNER_ID
        );
    }
}
