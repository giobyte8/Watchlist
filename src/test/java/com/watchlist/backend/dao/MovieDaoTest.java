package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Cast;
import com.watchlist.backend.entities.db.Crew;
import com.watchlist.backend.entities.db.MovieGenre;
import com.watchlist.backend.entities.db.Movie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@MockBean({
        RestTemplate.class
})
public class MovieDaoTest {

    @Autowired
    private MovieDao movieDao;

    @Test
    public void testSaveMoviePersistsRelationships() {
        String movieTitle = "Dummy title";
        int tmdbId = 101;

        MovieGenre movieGenre1 = new MovieGenre();
        movieGenre1.setId(5001);

        MovieGenre movieGenre2 = new MovieGenre();
        movieGenre2.setId(5002);

        Crew crew1 = new Crew();
        crew1.setName("John Doe");
        crew1.setJob("Something");
        crew1.setDepartment("Production");
        crew1.setPictureUrl("sample.com/profile.png");

        Crew crew2 = new Crew();
        crew2.setName("Elon Doe");
        crew2.setJob("Something");
        crew2.setDepartment("Production");

        Cast cast1 = new Cast();
        cast1.setName("Some legal name");
        cast1.setCharacter("Some uncommon name");
        cast1.setPictureUrl("sample.com/profile.png");

        Cast cast2 = new Cast();
        cast2.setName("Some legal name");
        cast2.setCharacter("Some uncommon name");

        Movie movie = new Movie();
        movie.setTmdbId(tmdbId);
        movie.setOriginalTitle(movieTitle);
        movie.setReleaseDate(new Date());
        movie.setRuntime(101);
        movie.setRating(5.5);

        movie.getGenres().add(movieGenre1);
        movie.getGenres().add(movieGenre2);

        movie.getCast().add(cast1);
        movie.getCast().add(cast2);

        movie.getCrew().add(crew1);
        movie.getCrew().add(crew2);

        movieDao.save(movie);

        Movie dbMovie = movieDao.findByTmdbId(tmdbId);
        assertNotNull(dbMovie);
        assertEquals(tmdbId, dbMovie.getTmdbId());
        assertEquals(movieTitle, dbMovie.getOriginalTitle());
        assertEquals(2, dbMovie.getGenres().size());
        assertEquals(2, dbMovie.getCast().size());
        assertEquals(2, dbMovie.getCrew().size());
    }
}
