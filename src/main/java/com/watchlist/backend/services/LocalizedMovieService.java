package com.watchlist.backend.services;

import com.watchlist.backend.dao.MovieCastDao;
import com.watchlist.backend.dao.MovieCrewDao;
import com.watchlist.backend.dao.MovieDao;
import com.watchlist.backend.entities.db.LocalizedMovie;
import com.watchlist.backend.entities.db.LocalizedMovieGenre;
import com.watchlist.backend.entities.db.Movie;
import com.watchlist.backend.entities.db.MovieCast;
import com.watchlist.backend.entities.db.MovieCrew;
import com.watchlist.backend.entities.db.MovieGenre;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalizedMovieService {

    private final MovieDao movieDao;
    private final MovieCrewDao movieCrewDao;
    private final MovieCastDao movieCastDao;

    public LocalizedMovieService(MovieCrewDao movieCrewDao,
                                 MovieCastDao movieCastDao,
                                 MovieDao movieDao) {
        this.movieCrewDao = movieCrewDao;
        this.movieCastDao = movieCastDao;
        this.movieDao = movieDao;
    }

    /**
     * Update or inserts provided movie along with its
     * crew, cast, localized movies and localized genres
     *
     * @param movie Tmdb movie to upsert
     * @return Upserted movie
     */
    public Movie upsert(Movie movie) {
        Movie dbMovie = movieDao.findByTmdbId(movie.getTmdbId());
        if (dbMovie == null) {
            movieDao.save(movie);
            return movie;
        }

        dbMovie.setOriginalTitle(movie.getOriginalTitle());
        dbMovie.setReleaseDate(movie.getReleaseDate());
        dbMovie.setRuntime(movie.getRuntime());
        dbMovie.setRating(movie.getRating());

        mergeLocalizations(movie, dbMovie);
        mergeCrew(movie, dbMovie);
        mergeCast(movie, dbMovie);
        mergeGenres(movie, dbMovie);

        movieDao.save(dbMovie);
        return dbMovie;
    }

    /**
     * Updates movie localizations in {@code dbMovie} with
     * localizations from {@code movie}
     *
     * @param movie Source movie object
     * @param dbMovie Target movie object
     */
    private void mergeLocalizations(Movie movie, Movie dbMovie) {
        for (LocalizedMovie locMovie : movie.getLocalizedMovies()) {
            Optional<LocalizedMovie> dbLocMovieOpt = dbMovie
                    .getLocalizedMovies()
                    .stream()
                    .filter(locMovie1 -> locMovie1
                            .getLanguage()
                            .equals(locMovie.getLanguage())
                    )
                    .findFirst();

            // Localized movie already in db, update it
            if (dbLocMovieOpt.isPresent()) {
                LocalizedMovie dbLocMovie = dbLocMovieOpt.get();
                dbLocMovie.setTitle(locMovie.getTitle());
                dbLocMovie.setSynopsis(locMovie.getSynopsis());
                dbLocMovie.setPosterPath(locMovie.getPosterPath());
                dbLocMovie.setBackdropPath(locMovie.getBackdropPath());
            }

            // Loc movie not in database, create it
            else {
                dbMovie.getLocalizedMovies().add(locMovie);
            }
        }
    }

    /**
     * Updates crew members in {@code dbMovie} with
     * crew from {@code movie}
     *
     * @param movie Source movie object
     * @param dbMovie Target movie object
     */
    private void mergeCrew(Movie movie, Movie dbMovie) {
        List<MovieCrew> removedCrew = new ArrayList<>();

        for (MovieCrew dbCrew : dbMovie.getCrew()) {
            Optional<MovieCrew> crewOpt = movie
                    .getCrew()
                    .stream()
                    .filter(crew1 -> crew1.getName().equals(dbCrew.getName()))
                    .findFirst();

            if (crewOpt.isPresent()) {

                // Update db crew in case some
                // values has changed in tmdb
                MovieCrew crew = crewOpt.get();
                dbCrew.setName(crew.getName());
                dbCrew.setDepartment(crew.getDepartment());
                dbCrew.setJob(crew.getJob());
                dbCrew.setPictureUrl(crew.getPictureUrl());

                // Remove from non db movie crew
                // since it's already into database
                movie.getCrew().remove(crew);
            } else {

                // Crew is not part of movie anymore
                // prepare it for be removed
                removedCrew.add(dbCrew);
                movieCrewDao.delete(dbCrew);
            }
        }

        // Remove all crew members that does not
        // belong to movie anymore
        dbMovie.getCrew().removeAll(removedCrew);

        // Add all crew members not stored previously in database
        dbMovie.getCrew().addAll(movie.getCrew());
    }

    /**
     * Updates cast members in {@code dbMovie} with
     * cast from {@code movie}
     *
     * @param movie Source movie object
     * @param dbMovie Target movie object
     */
    private void mergeCast(Movie movie, Movie dbMovie) {
        List<MovieCast> removedCast = new ArrayList<>();

        for (MovieCast dbCast : dbMovie.getCast()) {
            Optional<MovieCast> castOpt = movie.getCast()
                    .stream()
                    .filter(cast1 -> cast1.getName().equals(dbCast.getName()))
                    .findFirst();

            if (castOpt.isPresent()) {

                // Update db cast in case some
                // values has changed in tmdb
                MovieCast cast = castOpt.get();
                dbCast.setName(cast.getName());
                dbCast.setCharacter(cast.getCharacter());
                dbCast.setPictureUrl(cast.getPictureUrl());

                // Remove from non db movie cast
                // since it's already in database
                movie.getCast().remove(cast);
            } else {

                // Cast is not part of movie anymore
                // remove from db stored movie
                removedCast.add(dbCast);
                movieCastDao.delete(dbCast);
            }
        }

        // Remove all cast members that does not
        // belong to movie anymore
        dbMovie.getCast().removeAll(removedCast);

        // Add all cast members not stored previously in database
        dbMovie.getCast().addAll(movie.getCast());
    }

    /**
     * Updates genres in {@code dbMovie} with
     * genres from {@code movie}
     *
     * @param movie Source movie object
     * @param dbMovie Target movie object
     */
    private void mergeGenres(Movie movie, Movie dbMovie) {
        List<MovieGenre> removedGenres = new ArrayList<>();

        for (MovieGenre dbGenre : dbMovie.getGenres()) {
            Optional<MovieGenre> genreOpt = movie.getGenres()
                    .stream()
                    .filter(genre1 -> genre1.getId() == dbGenre.getId())
                    .findFirst();

            // DB Genre still belongs to movie
            if (genreOpt.isPresent()) {
                MovieGenre genre = genreOpt.get();

                // Update genre localizations
                for (LocalizedMovieGenre locGenre : genre.getLocalizedGenres()) {
                    Optional<LocalizedMovieGenre> dbLocGenreOpt = dbGenre
                            .getLocalizedGenres()
                            .stream()
                            .filter(locGenre1 -> locGenre1
                                    .getLanguage()
                                    .equals(locGenre.getLanguage())
                            )
                            .findFirst();

                    // Localization found in db, hence update it
                    if (dbLocGenreOpt.isPresent()) {
                        LocalizedMovieGenre dbLocGenre = dbLocGenreOpt.get();
                        dbLocGenre.setName(locGenre.getName());
                    }

                    // Localization not found in db, hence add it
                    else {
                        dbGenre.getLocalizedGenres().add(locGenre);
                    }
                }

                // Genre is already in db movie, hence remove it
                // from in memory movie
                movie.getGenres().remove(genre);
            }

            // Genre don't belongs to movie anymore, prepare it
            // for remove
            else {
                removedGenres.add(dbGenre);
            }
        }

        // Remove genres that doesn't belongs to movie anymore
        dbMovie.getGenres().removeAll(removedGenres);

        // Insert all genres not found in db
        dbMovie.getGenres().addAll(movie.getGenres());
    }
}
