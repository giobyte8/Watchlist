package com.watchlist.backend.controllers;

import com.watchlist.backend.exceptions.ProvidedUserIdNotFoundException;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("user")
public class ListController {

    private final UserService userService;

    public ListController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{userId}/lists")
    public Collection<Watchlist> getAll(@PathVariable long userId) {
        Collection<Watchlist> watchlists = userService.getLists(userId);

        if (watchlists == null) {
            throw new ProvidedUserIdNotFoundException();
        }

        return watchlists;
    }
}
