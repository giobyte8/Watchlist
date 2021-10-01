package com.watchlist.backend.services;

import com.watchlist.backend.entities.db.*;
import com.watchlist.backend.entities.json.JGenre;
import com.watchlist.backend.entities.json.MediaType;
import com.watchlist.backend.entities.json.LocalizedListItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalizedMediaService {

    public LocalizedListItem toWatchlistItem(WatchlistHasMovie hasMovie,
                                             Language lang) {
        LocalizedListItem localizedListItem = new LocalizedListItem();
        LocalizedMovie localizedMovie = findMovieForLang(
                hasMovie.getMovie().getLocalizedMovies(),
                lang
        );

        localizedListItem.setLang(lang.getIso639());
        localizedListItem.setTmdbId(hasMovie.getMovie().getTmdbId());
        localizedListItem.setReleaseDate(hasMovie.getMovie().getReleaseDate());
        localizedListItem.setTitle(localizedMovie.getTitle());
        localizedListItem.setSynopsis(localizedMovie.getSynopsis());
        localizedListItem.setPosterPath(localizedMovie.getPosterPath());
        localizedListItem.setBackdropPath(localizedMovie.getBackdropPath());
        localizedListItem.setMediaType(MediaType.MOVIE);
        localizedListItem.setGenres(getLocalizedMovieGenres(
                hasMovie.getMovie().getGenres(),
                lang
        ));

        return localizedListItem;
    }

    public LocalizedListItem toWatchlistItem(WatchlistHasTVShow hasTVShow,
                                             Language lang) {
        LocalizedListItem localizedListItem = new LocalizedListItem();
        LocalizedTVShow localizedTVShow = findTVForLang(
                hasTVShow.getTvShow().getLocalizedTVShows(),
                lang
        );

        localizedListItem.setLang(lang.getIso639());
        localizedListItem.setTmdbId(hasTVShow.getTvShow().getTmdbId());
        localizedListItem.setReleaseDate(hasTVShow.getTvShow().getFirstAirDate());
        localizedListItem.setTitle(localizedTVShow.getTitle());
        localizedListItem.setSynopsis(localizedTVShow.getSynopsis());
        localizedListItem.setPosterPath(localizedTVShow.getPosterPath());
        localizedListItem.setBackdropPath(localizedTVShow.getBackdropPath());
        localizedListItem.setMediaType(MediaType.TV);
        localizedListItem.setGenres(getLocalizedTVShowGenres(
                hasTVShow.getTvShow().getGenres(),
                lang
        ));

        return localizedListItem;
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
