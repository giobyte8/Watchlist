package com.watchlist.backend.dao;

import com.watchlist.backend.model.Cast;
import com.watchlist.backend.model.Crew;
import com.watchlist.backend.model.Genre;
import com.watchlist.backend.model.Movie;
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

        Genre genre1 = new Genre();
        genre1.setId(5001);
        genre1.setName("Test genre 1");

        Genre genre2 = new Genre();
        genre2.setId(5002);
        genre2.setName("Test genre 2");

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
        movie.setTitle(movieTitle);
        movie.setOriginalTitle(movieTitle);
        movie.setReleaseDate(new Date());
        movie.setRuntime(101);
        movie.setSynopsis("Something");
        movie.setRating(5.5);
        movie.setPosterPath("something/poster.png");

        movie.getGenres().add(genre1);
        movie.getGenres().add(genre2);

        movie.getCast().add(cast1);
        movie.getCast().add(cast2);

        movie.getCrew().add(crew1);
        movie.getCrew().add(crew2);

        movieDao.save(movie);

        Movie dbMovie = movieDao.findByTmdbId(tmdbId);
        assertNotNull(dbMovie);
        assertEquals(tmdbId, dbMovie.getTmdbId());
        assertEquals(movieTitle, dbMovie.getTitle());
        assertEquals(2, dbMovie.getGenres().size());
        assertEquals(2, dbMovie.getCast().size());
        assertEquals(2, dbMovie.getCrew().size());
    }
}
