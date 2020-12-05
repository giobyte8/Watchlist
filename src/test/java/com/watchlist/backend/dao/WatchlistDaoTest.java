package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.User;
import com.watchlist.backend.entities.db.UserHasWatchlist;
import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.entities.db.WatchlistPermission;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@DataJpaTest
@MockBean({
        RestTemplate.class
})
public class WatchlistDaoTest {

    @Autowired
    private WatchlistDao watchlistDao;

    @Autowired
    private UserHasWatchlistDao hasWatchlistDao;

    @Autowired
    private EntityManager entityManager;

    private final long testUserId = 101L;

    @Test
    public void testExistsByNameAndOwner() {
        String watchlistName = "Western";

        Watchlist watchlist = new Watchlist();
        watchlist.setName(watchlistName);
        watchlistDao.save(watchlist);

        WatchlistPermission ownerPermission = entityManager.getReference(
                WatchlistPermission.class,
                WatchlistPermission.OWNER_ID
        );
        User user = entityManager.getReference(User.class, testUserId);

        UserHasWatchlist hasWatchlist = new UserHasWatchlist();
        hasWatchlist.setWatchlist(watchlist);
        hasWatchlist.setUser(user);
        hasWatchlist.setPermission(ownerPermission);
        hasWatchlistDao.save(hasWatchlist);

        Assert.assertTrue(watchlistDao.existsByNameAndOwner(
                watchlistName,
                testUserId
        ));
    }

    @Test
    public void testExistsByNameAndOwnerForNonExistentList() {
        String watchlistName = "Western";
        Assert.assertFalse(watchlistDao.existsByNameAndOwner(
                watchlistName,
                testUserId
        ));
    }
}
