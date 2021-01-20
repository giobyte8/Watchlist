package com.watchlist.backend.services;

import com.watchlist.backend.entities.db.*;
import com.watchlist.backend.entities.json.JGenre;
import com.watchlist.backend.entities.json.MediaType;
import com.watchlist.backend.entities.json.WatchlistItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalizedMediaService {

    public WatchlistItem toWatchlistItem(WatchlistHasMovie hasMovie,
                                          Language lang) {
        WatchlistItem watchlistItem = new WatchlistItem();
        LocalizedMovie localizedMovie = findMovieForLang(
                hasMovie.getMovie().getLocalizedMovies(),
                lang
        );

        watchlistItem.setTmdbId(hasMovie.getMovie().getTmdbId());
        watchlistItem.setReleaseDate(hasMovie.getMovie().getReleaseDate());
        watchlistItem.setTitle(localizedMovie.getTitle());
        watchlistItem.setSynopsis(localizedMovie.getSynopsis());
        watchlistItem.setPosterPath(localizedMovie.getPosterPath());
        watchlistItem.setBackdropPath(localizedMovie.getBackdropPath());
        watchlistItem.setMediaType(MediaType.MOVIE);
        watchlistItem.setGenres(getLocalizedMovieGenres(
                hasMovie.getMovie().getGenres(),
                lang
        ));

        return watchlistItem;
    }

    public WatchlistItem toWatchlistItem(WatchlistHasTVShow hasTVShow,
                                         Language lang) {
        WatchlistItem watchlistItem = new WatchlistItem();
        LocalizedTVShow localizedTVShow = findTVForLang(
                hasTVShow.getTvShow().getLocalizedTVShows(),
                lang
        );

        watchlistItem.setTmdbId(hasTVShow.getTvShow().getTmdbId());
        watchlistItem.setReleaseDate(hasTVShow.getTvShow().getFirstAirDate());
        watchlistItem.setTitle(localizedTVShow.getTitle());
        watchlistItem.setSynopsis(localizedTVShow.getSynopsis());
        watchlistItem.setPosterPath(localizedTVShow.getPosterPath());
        watchlistItem.setBackdropPath(localizedTVShow.getBackdropPath());
        watchlistItem.setMediaType(MediaType.TV);
        watchlistItem.setGenres(getLocalizedTVShowGenres(
                hasTVShow.getTvShow().getGenres(),
                lang
        ));

        return watchlistItem;
    }

    /**
     * Find localized movie for provided language or defaults
     * to "en-US"
     *
     * @param localizedMovies List of localized movies
     * @param lang Language to search for
     * @return Localized movie or english movie
     */
    private LocalizedMovie findMovieForLang(List<LocalizedMovie> localizedMovies,
                                            Language lang) {
        LocalizedMovie localizedMovie = localizedMovies
                .stream()
                .filter(lMovie -> lMovie.getLanguage().equals(lang))
                .findAny()
                .orElse(null);

        if (localizedMovie == null) {
            localizedMovie = localizedMovies
                    .stream()
                    .filter(lMovie1 -> lMovie1
                            .getLanguage()
                            .getIso639()
                            .equals(Language.ISO_EN_US)
                    )
                    .findFirst()
                    .orElse(null);
        }

        return localizedMovie;
    }

    /**
     * Find localized TV Show for provided language or defaults
     * to "en-US"
     *
     * @param localizedTVShows List of localized TV Shows
     * @param lang Language to search for
     * @return Localized TV Show or english version
     */
    private LocalizedTVShow findTVForLang(List<LocalizedTVShow> localizedTVShows,
                                        Language lang) {
        Optional<LocalizedTVShow> lTVShowOpt = localizedTVShows
                .stream()
                .filter(lTVShow -> lTVShow.getLanguage().equals(lang))
                .findFirst();

        if (!lTVShowOpt.isPresent()) {
            lTVShowOpt = localizedTVShows
                    .stream()
                    .filter(lTVShow1 -> lTVShow1
                            .getLanguage()
                            .getIso639()
                            .equals(Language.ISO_EN_US)
                    )
                    .findFirst();
        }

        return lTVShowOpt.orElse(null);
    }

    /**
     * Will retrieve each genre localized for provided language
     * or default to english
     *
     * @param genres Movie genres list
     * @param lang Localization language
     * @return Localized genres
     */
    @SuppressWarnings("DuplicatedCode")
    private List<JGenre> getLocalizedMovieGenres(List<MovieGenre> genres,
                                                 Language lang) {
        List<JGenre> jGenres = new ArrayList<>(genres.size());

        for (MovieGenre genre : genres) {
            Optional<LocalizedMovieGenre> localizedGenreOpt = genre
                    .getLocalizedGenres()
                    .stream()
                    .filter(lGenre -> lGenre.getLanguage().equals(lang))
                    .findFirst();

            if (!localizedGenreOpt.isPresent()) {
                localizedGenreOpt = genre
                        .getLocalizedGenres()
                        .stream()
                        .filter(lGenre1 -> lGenre1
                                .getLanguage()
                                .getIso639()
                                .equals(Language.ISO_EN_US)
                        )
                        .findFirst();
            }

            if (localizedGenreOpt.isPresent()) {
                JGenre jGenre = new JGenre();
                jGenre.setId(genre.getId());
                jGenre.setName(localizedGenreOpt.get().getName());

                jGenres.add(jGenre);
            }
        }

        return jGenres;
    }

    /**
     * Will retrieve each genre localized for provided language
     * or default to english
     *
     * @param genres TV Show genres list
     * @param lang Localization language
     * @return Localized genres
     */
    @SuppressWarnings("DuplicatedCode")
    private List<JGenre> getLocalizedTVShowGenres(List<TVShowGenre> genres,
                                                  Language lang) {
        List<JGenre> jGenres = new ArrayList<>(genres.size());

        for (TVShowGenre genre : genres) {
            Optional<LocalizedTVShowGenre> localizedGenreOpt = genre
                    .getLocalizedGenres()
                    .stream()
                    .filter(lGenre -> lGenre.getLanguage().equals(lang))
                    .findFirst();

            if (!localizedGenreOpt.isPresent()) {
                localizedGenreOpt = genre
                        .getLocalizedGenres()
                        .stream()
                        .filter(lGenre1 -> lGenre1
                                .getLanguage()
                                .getIso639()
                                .equals(Language.ISO_EN_US)
                        )
                        .findFirst();
            }

            if (localizedGenreOpt.isPresent()) {
                JGenre jGenre = new JGenre();
                jGenre.setId(genre.getId());
                jGenre.setName(localizedGenreOpt.get().getName());

                jGenres.add(jGenre);
            }
        }

        return jGenres;
    }
}
