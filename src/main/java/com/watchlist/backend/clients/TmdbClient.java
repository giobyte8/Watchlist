package com.watchlist.backend.clients;

import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.AppendToResponse;
import com.uwetrottmann.tmdb2.entities.CastMember;
import com.uwetrottmann.tmdb2.entities.CrewMember;
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem;
import com.uwetrottmann.tmdb2.services.MoviesService;
import com.watchlist.backend.model.Cast;
import com.watchlist.backend.model.Crew;
import com.watchlist.backend.model.Genre;
import com.watchlist.backend.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
public class TmdbClient {
    private static final String DEFAULT_LANG = "en";
    private final MoviesService moviesService;

    public TmdbClient(@Value("${watchlist.tmdb-api-key}") String tmdbApiKey) {
        Tmdb tmdb = new Tmdb(tmdbApiKey);
        moviesService = tmdb.moviesService();
    }

    public Movie getMovie(int tmdbId)
            throws IOException {
        Response<com.uwetrottmann.tmdb2.entities.Movie> movieResponse = moviesService
                .summary(
                        tmdbId,
                        DEFAULT_LANG,
                        new AppendToResponse(
                                AppendToResponseItem.CREDITS
                        )
                )
                .execute();

        if (movieResponse.isSuccessful() && movieResponse.body() != null) {
            return toWatchlistMovie(movieResponse.body());
        }

        throw new IOException("Movie could not be retrieved");
    }

    @SuppressWarnings("ConstantConditions")
    private Movie toWatchlistMovie(
            com.uwetrottmann.tmdb2.entities.Movie tmdbMovie) {
        Movie movie = new Movie();
        movie.setTmdbId(tmdbMovie.id);
        movie.setTitle(tmdbMovie.title);
        movie.setOriginalTitle(tmdbMovie.original_title);
        movie.setReleaseDate(tmdbMovie.release_date);
        movie.setRuntime(tmdbMovie.runtime);
        movie.setSynopsis(tmdbMovie.overview);
        movie.setRating(tmdbMovie.vote_average);
        movie.setPosterPath(tmdbMovie.poster_path);
        movie.setBackdropPath(tmdbMovie.backdrop_path);

        for (CrewMember tmdbCrew : tmdbMovie.credits.crew) {
            Crew crew = new Crew();
            crew.setName(tmdbCrew.name);
            crew.setJob(tmdbCrew.job);
            crew.setDepartment(tmdbCrew.department);
            crew.setPictureUrl(tmdbCrew.profile_path);

            movie.getCrew().add(crew);
        }

        for (CastMember tmdbCast : tmdbMovie.credits.cast) {
            Cast cast = new Cast();
            cast.setName(tmdbCast.name);
            cast.setCharacter(tmdbCast.character);
            cast.setPictureUrl(tmdbCast.profile_path);

            movie.getCast().add(cast);
        }

        for (com.uwetrottmann.tmdb2.entities.Genre tmdbGenre :
                tmdbMovie.genres) {
            Genre genre = new Genre();
            genre.setId(tmdbGenre.id);
            genre.setName(tmdbGenre.name);

            movie.getGenres().add(genre);
        }

        return movie;
    }
}
