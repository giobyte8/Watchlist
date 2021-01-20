package com.watchlist.backend.services;

import com.watchlist.backend.entities.db.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LocalizedMediaServiceTest {
    private int GENRES_COUNT = 3;

    private int TMDB_ID = 12345;
    private String MOVIE_TITLE_ES = "MOVIE_ES";
    private String MOVIE_TITLE_EN = "MOVIE_EN";

    private Language langEs;
    private Language langEn;

    @Before
    public void setUp() {
        langEn = new Language();
        langEn.setId(1);
        langEn.setIso639(Language.ISO_EN_US);

        langEs = new Language();
        langEs.setId(2);
        langEs.setIso639(Language.ISO_ES_MX);
    }

    @Test
    public void testMovieToWatchlistItem() {

    }

    private WatchlistHasMovie makeWatchlistHasMovie() {
        Movie movie = new Movie();
        movie.setTmdbId(TMDB_ID);

        for (int i = 1; i <= GENRES_COUNT; i++) {
            MovieGenre genre = new MovieGenre();
            genre.setId(i);

            LocalizedMovieGenre genreEs = new LocalizedMovieGenre();
            genreEs.setName(String.format("Genre %d", i));
            genreEs.setLanguage(langEs);
            genre.getLocalizedGenres().add(genreEs);

            LocalizedMovieGenre genreEn = new LocalizedMovieGenre();
            genreEn.setName(String.format("Genre %d", i));
            genreEn.setLanguage(langEn);
            genre.getLocalizedGenres().add(genreEn);

            movie.getGenres().add(genre);
        }

        LocalizedMovie movieEs = new LocalizedMovie();
        movieEs.setTitle(MOVIE_TITLE_ES);
        movie.getLocalizedMovies().add(movieEs);

        LocalizedMovie movieEn = new LocalizedMovie();
        movieEn.setTitle(MOVIE_TITLE_EN);
        movie.getLocalizedMovies().add(movieEn);

        return null;
    }
}
