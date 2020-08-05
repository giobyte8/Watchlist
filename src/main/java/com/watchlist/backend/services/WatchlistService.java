package com.watchlist.backend.services;

import com.watchlist.backend.dao.UserHasWatchlistDao;
import com.watchlist.backend.dao.WatchlistDao;
import com.watchlist.backend.model.User;
import com.watchlist.backend.model.UserHasWatchlist;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.model.WatchlistPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
public class WatchlistService {

    private final WatchlistDao watchlistDao;
    private final UserHasWatchlistDao userHasWatchlistDao;

    private final EntityManager entityManager;

    public WatchlistService(WatchlistDao watchlistDao,
                            UserHasWatchlistDao userHasWatchlistDao,
                            EntityManager entityManager) {
        this.watchlistDao = watchlistDao;
        this.userHasWatchlistDao = userHasWatchlistDao;
        this.entityManager = entityManager;
    }

    @Transactional
    public void createDefaultList(User user) {
        Watchlist defaultList = new Watchlist();
        defaultList.setName(Watchlist.DEFAULT_LIST_NAME);
        defaultList.setDefault(true);
        watchlistDao.save(defaultList);

        WatchlistPermission ownerPermission = entityManager.getReference(
                WatchlistPermission.class,
                WatchlistPermission.OWNER_ID
        );

        UserHasWatchlist hasWatchlist = new UserHasWatchlist();
        hasWatchlist.setUser(user);
        hasWatchlist.setWatchlist(defaultList);
        hasWatchlist.setPermission(ownerPermission);
        userHasWatchlistDao.save(hasWatchlist);
    }

    /**
     * Persists watchlist and sets provided user
     * as the owner of list
     * @param watchlist List to save/create
     * @param userId Owner of list
     */
    @Transactional
    public void create(Watchlist watchlist, long userId) {
        watchlistDao.save(watchlist);

        WatchlistPermission ownerPermission = entityManager.getReference(
                WatchlistPermission.class,
                WatchlistPermission.OWNER_ID
        );

        User owner = entityManager.getReference(
                User.class,
                userId
        );

        UserHasWatchlist hasWatchlist = new UserHasWatchlist();
        hasWatchlist.setUser(owner);
        hasWatchlist.setWatchlist(watchlist);
        hasWatchlist.setPermission(ownerPermission);
        userHasWatchlistDao.save(hasWatchlist);
    }

    /**
     * Verifies if a watchlist with given name an under
     * ownership of indicated user already exists
     *
     * @param name Watchlist name
     * @param ownerId Id of list owner
     * @return true if a list already exists
     */
    public boolean existsByNameAndOwner(String name, long ownerId) {
        return watchlistDao.existsByNameAndOwner(name, ownerId);
    }
}
