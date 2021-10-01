package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.json.LocalizedListItem;
import com.watchlist.backend.exceptions.WatchlistNotFoundException;
import com.watchlist.backend.services.ListContentService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("list")
public class ListContentController {

    private final WatchlistService watchlistService;
    private final ListContentService listContentService;

    public ListContentController(WatchlistService watchlistService,
                                 ListContentService listContentService) {
        this.watchlistService = watchlistService;
        this.listContentService = listContentService;
    }

    @GetMapping("{listId}")
    public List<LocalizedListItem> getContents(@PathVariable long listId,
                                               @RequestParam(
                                                   required = false,
                                                   name = "lang",
                                                   defaultValue = Language.ISO_EN_US
                                           ) String lang) {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        return listContentService.getListContent(
                listId,
                lang
        );
    }
}
