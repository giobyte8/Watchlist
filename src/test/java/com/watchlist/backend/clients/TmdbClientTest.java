package com.watchlist.backend.clients;

import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.Movie;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
public class TmdbClientTest {
    private final int fightClubTmdbId = 550;
    private final String fightClubOriginalTitle = "Fight Club";
    private TmdbClient tmdbClient;

    @Value("${watchlist.tmdb-api-key}")
    private String tmdbApiKey;

    @Before
    public void setup() {
        tmdbClient = new TmdbClient(tmdbApiKey);
    }

    @Test
    public void testGetDetails() throws IOException {
        Language langEnglish = new Language();
        langEnglish.setIso639(Language.ISO_EN_US);

        Movie movie = tmdbClient.getMovie(fightClubTmdbId, langEnglish);
        Assert.assertEquals(fightClubOriginalTitle, movie.getOriginalTitle());
        Assert.assertEquals(
                fightClubOriginalTitle,
                movie.getLocalizedMovies().get(0).getTitle()
        );
    }

    @Test
    public void testGetDetailsSpanish() throws IOException {
        String fightClubSpanishTitle = "El club de la pelea";

        Language langSpanish = new Language();
        langSpanish.setIso639(Language.ISO_ES_MX);

        Movie movie = tmdbClient.getMovie(fightClubTmdbId, langSpanish);
        Assert.assertEquals(fightClubOriginalTitle, movie.getOriginalTitle());
        Assert.assertEquals(
                fightClubSpanishTitle,
                movie.getLocalizedMovies().get(0).getTitle()
        );
    }
}
