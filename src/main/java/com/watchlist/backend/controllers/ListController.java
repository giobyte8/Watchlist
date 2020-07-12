package com.watchlist.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class ListController {

    @GetMapping("{userId}/lists")
    public String getAll(@PathVariable long userId) {
        System.out.println("Recovering lists of user");
        return "Watchlists";
    }
}
