package com.watchlist.backend.services;

import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.dao.WatchlistHasTVShowDao;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.entities.db.WatchlistHasMovie;
import com.watchlist.backend.entities.db.WatchlistHasTVShow;
import com.watchlist.backend.entities.json.WatchlistItem;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
public class ListContentService {

    private final EntityManager entityManager;
    private final WatchlistHasMovieDao hasMovieDao;
    private final WatchlistHasTVShowDao hasTVShowDao;
    private final LanguageService languageService;
    private final LocalizedMediaService localizedMediaService;

    public ListContentService(EntityManager entityManager,
                              WatchlistHasMovieDao hasMovieDao,
                              WatchlistHasTVShowDao hasTVShowDao,
                              LanguageService languageService,
                              LocalizedMediaService localizedMediaService) {
        this.entityManager = entityManager;
        this.hasMovieDao = hasMovieDao;
        this.hasTVShowDao = hasTVShowDao;
        this.languageService = languageService;
        this.localizedMediaService = localizedMediaService;
    }

    public List<WatchlistItem> getListContent(long listId, String iso639) {
        Watchlist list = entityManager.getReference(
                Watchlist.class,
                listId
        );
        Language lang = languageService.parseISO639(iso639);

        Queue<WatchlistHasMovie> hasMovies = hasMovieDao
                .findByWatchlistByOrderByAddedAtDesc(list);
        Queue<WatchlistHasTVShow> hasTVShows = hasTVShowDao
                .findByWatchlistByOrderByAddedAtDesc(list);
        List<WatchlistItem> listContent =
                new ArrayList<>(hasMovies.size() + hasTVShows.size());

        WatchlistHasMovie hasMovie = hasMovies.poll();
        WatchlistHasTVShow hasTVShow = hasTVShows.poll();
        while (hasMovie != null || hasTVShow != null) {

            // Only movie has a value
            if (hasMovie != null && hasTVShow == null) {
                listContent.add(localizedMediaService.toWatchlistItem(
                        hasMovie,
                        lang
                ));

                hasMovie = hasMovies.poll();
            }

            // Only tv show has a value
            else if (hasMovie == null) {
                listContent.add(localizedMediaService.toWatchlistItem(
                        hasTVShow,
                        lang
                ));

                hasTVShow = hasTVShows.poll();
            }

            // Both have non null value
            else {
                if (hasMovie.getAddedAt().after(hasTVShow.getAddedAt())) {
                    listContent.add(localizedMediaService.toWatchlistItem(
                            hasMovie,
                            lang
                    ));
                } else {
                    listContent.add(localizedMediaService.toWatchlistItem(
                            hasTVShow,
                            lang
                    ));
                }

                hasMovie = hasMovies.poll();
                hasTVShow = hasTVShows.poll();
            }
        }

        return listContent;
    }

}
