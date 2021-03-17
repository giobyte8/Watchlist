package com.watchlist.backend.services;

import com.watchlist.backend.dao.TVShowCastDao;
import com.watchlist.backend.dao.TVShowCrewDao;
import com.watchlist.backend.dao.TVShowDao;
import com.watchlist.backend.entities.db.LocalizedTVShow;
import com.watchlist.backend.entities.db.LocalizedTVShowGenre;
import com.watchlist.backend.entities.db.TVShow;
import com.watchlist.backend.entities.db.TVShowCast;
import com.watchlist.backend.entities.db.TVShowCrew;
import com.watchlist.backend.entities.db.TVShowGenre;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalizedTVShowService {

    private final TVShowDao tvShowDao;
    private final TVShowCrewDao tvShowCrewDao;
    private final TVShowCastDao tvShowCastDao;

    public LocalizedTVShowService(TVShowDao tvShowDao,
                                  TVShowCrewDao tvShowCrewDao,
                                  TVShowCastDao tvShowCastDao) {
        this.tvShowDao = tvShowDao;
        this.tvShowCrewDao = tvShowCrewDao;
        this.tvShowCastDao = tvShowCastDao;
    }

    /**
     * Update or inserts provided tv show along with its
     * crew, cast, localizations and localized genres
     *
     * @param tvShow Tmdb tv show to upsert
     * @return Upserted tv show
     */
    public TVShow upsert(TVShow tvShow) {
        TVShow dbTvShow = tvShowDao.findByTmdbId(tvShow.getTmdbId());
        if (dbTvShow == null) {
            tvShowDao.save(tvShow);
            return tvShow;
        }

        dbTvShow.setOriginalTitle(tvShow.getOriginalTitle());
        dbTvShow.setFirstAirDate(tvShow.getFirstAirDate());
        dbTvShow.setRating(tvShow.getRating());

        mergeLocalizations(tvShow, dbTvShow);
        mergeCrew(tvShow, dbTvShow);
        mergeCast(tvShow, dbTvShow);
        mergeGenres(tvShow, dbTvShow);

        tvShowDao.save(dbTvShow);
        return dbTvShow;
    }

    /**
     * Updates tv show localizations in {@code dbTvShow}
     * with localizations from {@code tvShow}
     *
     * @param tvShow Source tv show object
     * @param dbTvShow Target tv show object
     */
    private void mergeLocalizations(TVShow tvShow, TVShow dbTvShow) {
        for (LocalizedTVShow locTvShow : tvShow.getLocalizedTVShows()) {
            Optional<LocalizedTVShow> dbLocTVShowOpt = dbTvShow
                    .getLocalizedTVShows()
                    .stream()
                    .filter(locTvShow1 -> locTvShow1
                            .getLanguage()
                            .equals(locTvShow.getLanguage())
                    )
                    .findFirst();

            // Localized tv show already in db, update it
            if (dbLocTVShowOpt.isPresent()) {
                LocalizedTVShow dbLocTvShow = dbLocTVShowOpt.get();
                dbLocTvShow.setTitle(locTvShow.getTitle());
                dbLocTvShow.setSynopsis(locTvShow.getSynopsis());
                dbLocTvShow.setPosterPath(locTvShow.getPosterPath());
                dbLocTvShow.setBackdropPath(locTvShow.getBackdropPath());
            }

            // Loc tv show not in database, create it
            else {
                dbTvShow.getLocalizedTVShows().add(locTvShow);
            }
        }
    }

    /**
     * Updates crew members in {@code dbTvShow} with
     * crew from {@code tvShow}
     *
     * @param tvShow Source tvShow object
     * @param dbTvShow Target tvShow object
     */
    private void mergeCrew(TVShow tvShow, TVShow dbTvShow) {
        List<TVShowCrew> removedCrew = new ArrayList<>();

        for (TVShowCrew dbCrew : dbTvShow.getCrew()) {
            Optional<TVShowCrew> crewOpt = tvShow
                    .getCrew()
                    .stream()
                    .filter(crew1 -> crew1.getName().equals(dbCrew.getName()))
                    .findFirst();

            if (crewOpt.isPresent()) {

                // Update db crew in case some
                // values has changed in tmdb
                TVShowCrew crew = crewOpt.get();
                dbCrew.setName(crew.getName());
                dbCrew.setDepartment(crew.getDepartment());
                dbCrew.setJob(crew.getJob());
                dbCrew.setPictureUrl(crew.getPictureUrl());

                // Remove from non db crew
                // since it's already in database
                tvShow.getCrew().remove(crew);
            } else {

                // Crew is not part of tv show anymore
                // prepare it for be removed
                removedCrew.add(dbCrew);
                tvShowCrewDao.delete(dbCrew);
            }
        }

        // Remove all crew members that does not
        // belong to tvShow anymore
        dbTvShow.getCrew().removeAll(removedCrew);

        // Add all crew members not stored previously in database
        dbTvShow.getCrew().addAll(tvShow.getCrew());
    }

    /**
     * Updates cast members in {@code dbTvShow} with
     * cast from {@code tvShow}
     *
     * @param tvShow Source tv show object
     * @param dbTvShow Target tv show object
     */
    private void mergeCast(TVShow tvShow, TVShow dbTvShow) {
        List<TVShowCast> removedCast = new ArrayList<>();

        for (TVShowCast dbCast : dbTvShow.getCast()) {
            Optional<TVShowCast> castOpt = tvShow
                    .getCast()
                    .stream()
                    .filter(cast1 -> cast1.getName().equals(dbCast.getName()))
                    .findFirst();

            if (castOpt.isPresent()) {

                // Update db cast in case some
                // values has changed in tmdb
                TVShowCast cast = castOpt.get();
                dbCast.setName(cast.getName());
                dbCast.setCharacter(cast.getCharacter());
                dbCast.setPictureUrl(cast.getPictureUrl());

                // Remove from non db cast
                // since it's already in database
                tvShow.getCast().remove(cast);
            } else {

                // Cast is not part of tv show anymore
                // remove from db
                removedCast.add(dbCast);
                tvShowCastDao.delete(dbCast);
            }
        }

        // Remove all cast members that does not
        // belong to tv show anymore
        dbTvShow.getCast().removeAll(removedCast);

        // Add all cast members not stored previously in database
        dbTvShow.getCast().addAll(tvShow.getCast());
    }

    /**
     * Updates genres in {@code dbTvShow} with
     * genres from {@code tvShow}
     *
     * @param tvShow Source tv show object
     * @param dbTvShow Target tv show object
     */
    private void mergeGenres(TVShow tvShow, TVShow dbTvShow) {
        List<TVShowGenre> removedGenres = new ArrayList<>();

        for (TVShowGenre dbGenre : dbTvShow.getGenres()) {
            Optional<TVShowGenre> genreOpt = tvShow.getGenres()
                    .stream()
                    .filter(genre1 -> genre1.getId() == dbGenre.getId())
                    .findFirst();

            // DB Genre still belongs to tv show
            if (genreOpt.isPresent()) {
                TVShowGenre genre = genreOpt.get();

                // Update genre localizations
                for (LocalizedTVShowGenre locGenre : genre.getLocalizedGenres()) {
                    Optional<LocalizedTVShowGenre> dbLocGenreOpt = dbGenre
                            .getLocalizedGenres()
                            .stream()
                            .filter(locGenre1 -> locGenre1
                                    .getLanguage()
                                    .equals(locGenre.getLanguage())
                            )
                            .findFirst();

                    // Localization found in db, hence update it
                    if (dbLocGenreOpt.isPresent()) {
                        LocalizedTVShowGenre dbLocGenre = dbLocGenreOpt.get();
                        dbLocGenre.setName(locGenre.getName());
                    }

                    // Localization not found in db, hence add it
                    else {
                        dbGenre.getLocalizedGenres().add(locGenre);
                    }
                }

                // Genre is already in db, hence remove it
                // from in memory list
                tvShow.getGenres().remove(genre);
            }

            // Genre don't belongs to tv show anymore, prepare it
            // for remove
            else {
                removedGenres.add(dbGenre);
            }
        }

        // Remove genres that doesn't belongs to tv show anymore
        dbTvShow.getGenres().removeAll(removedGenres);

        // Insert all genres not found in db
        dbTvShow.getGenres().addAll(tvShow.getGenres());
    }
}
