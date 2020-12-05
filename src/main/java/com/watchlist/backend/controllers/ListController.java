package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.exceptions.NonUniqueWatchlistNameForUserException;
import com.watchlist.backend.exceptions.ProvidedUserIdNotFoundException;
import com.watchlist.backend.services.UserService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("user")
public class ListController {

    private final UserService userService;
    private final WatchlistService watchlistSvc;

    public ListController(UserService userService,
                          WatchlistService watchlistSvc) {
        this.userService = userService;
        this.watchlistSvc = watchlistSvc;
    }

    @GetMapping("{userId}/lists")
    public Collection<Watchlist> getAll(@PathVariable long userId) {
        Collection<Watchlist> watchlists = userService.getLists(userId);

        if (watchlists == null) {
            throw new ProvidedUserIdNotFoundException();
        }

        return watchlists;
    }

    @PostMapping("{userId}/lists")
    public Watchlist create(@PathVariable long userId,
                            @Valid @RequestBody Watchlist watchlist) {
        if (!userService.userExists(userId)) {
            throw new ProvidedUserIdNotFoundException();
        }

        if (watchlistSvc.existsByNameAndOwner(watchlist.getName(), userId)) {
            throw new NonUniqueWatchlistNameForUserException();
        }

        watchlistSvc.create(watchlist, userId);
        return watchlist;
    }
}
