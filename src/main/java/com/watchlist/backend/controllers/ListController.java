package com.watchlist.backend.controllers;

import com.watchlist.backend.exceptions.ProvidedUserIdNotFoundException;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.services.UserService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("user")
public class ListController {

    private final UserService userService;
    private final WatchlistService watchlistService;

    public ListController(UserService userService,
                          WatchlistService watchlistService) {
        this.userService = userService;
        this.watchlistService = watchlistService;
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
        watchlistService.create(watchlist, userId);
        return watchlist;
    }
}
