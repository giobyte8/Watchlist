package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.json.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
public class ConfigController {

    private final String tmdbApiKey;
    private final String ytApiKey;


    public ConfigController(@Value("${watchlist.tmdb-api-key}") String tmdbApiKey,
                            @Value("${watchlist.yt-api-key}") String ytApiKey) {
        this.tmdbApiKey = tmdbApiKey;
        this.ytApiKey = ytApiKey;
    }

    @GetMapping
    public Config getConfig() {
        Config config = new Config();
        config.setTmdbApiKey(tmdbApiKey);
        config.setYtApiKey(ytApiKey);

        return config;
    }
}
