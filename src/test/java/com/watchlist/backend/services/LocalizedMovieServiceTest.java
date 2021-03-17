package com.watchlist.backend.services;

import com.watchlist.backend.dao.MovieCastDao;
import com.watchlist.backend.dao.MovieCrewDao;
import com.watchlist.backend.dao.MovieDao;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.LocalizedMovie;
import com.watchlist.backend.entities.db.LocalizedMovieGenre;
import com.watchlist.backend.entities.db.Movie;
import com.watchlist.backend.entities.db.MovieCast;
import com.watchlist.backend.entities.db.MovieCrew;
import com.watchlist.backend.entities.db.MovieGenre;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(LocalizedMovieService.class)
public class LocalizedMovieServiceTest {

    @Mock
    private MovieDao movieDao;

    @Mock
    private MovieCrewDao movieCrewDao;

    @Mock
    private MovieCastDao movieCastDao;

    @InjectMocks
    private LocalizedMovieService locMovieSvc;

    private final Language eng = new Language();
    private final Language esp = new Language();

    @Before
    public void setup() {
        eng.setId(1);
        eng.setIso639(Language.ISO_EN_US);

        esp.setId(2);
        eng.setIso639(Language.ISO_ES_MX);
    }

    @Test
    public void testMergeLocalizationsAddLoc() throws Exception {
        LocalizedMovie loc1 = makeLocMovie("Fight Club", eng);
        LocalizedMovie loc2 = makeLocMovie("El Club de la Pelea", esp);

        Movie dbMovie = new Movie();
        Movie movie = new Movie();

        dbMovie.getLocalizedMovies().add(loc1);
        movie.getLocalizedMovies().addAll(Arrays.asList(
                loc1,
                loc2
        ));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeLocalizations",
                movie,
                dbMovie
        );

        assertEquals(
                "Movie localizations count expected to be eq 2",
                2,
                dbMovie.getLocalizedMovies().size()
        );

        assertTrue(
                "Movie expected to contain all localizations",
                dbMovie.getLocalizedMovies().containsAll(Arrays.asList(
                        loc1,
                        loc2
                ))
        );
    }

    @Test
    public void testMergeLocalizationsUpdateLoc() throws Exception {
        LocalizedMovie loc1 = makeLocMovie("Fight Club", eng);
        LocalizedMovie loc2 = makeLocMovie("Localized title", esp);

        Movie dbMovie = new Movie();
        dbMovie.getLocalizedMovies().addAll(Arrays.asList(
                loc1,
                loc2
        ));

        LocalizedMovie loc2Updated = makeLocMovie("Updated title", esp);
        Movie movie = new Movie();
        movie.getLocalizedMovies().addAll(Arrays.asList(
                loc1,
                loc2Updated
        ));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeLocalizations",
                movie,
                dbMovie
        );

        assertEquals(
                "Movie localizations count expected to be eq 2",
                2,
                dbMovie.getLocalizedMovies().size()
        );

        assertEquals(
                "First movie localization matches eng lang title",
                dbMovie
                        .getLocalizedMovies()
                        .get(0)
                        .getTitle(),
                loc1.getTitle()
        );

        assertEquals(
                "Second movie localization matches spanish lang title",
                dbMovie
                        .getLocalizedMovies()
                        .get(1)
                        .getTitle(),
                loc2Updated.getTitle()
        );
    }

    @Test
    public void testMergeCrewAddAll() throws Exception {
        MovieCrew crew1 = makeCrew(1, "Crew 1");
        MovieCrew crew2 = makeCrew(2, "Crew 2");
        MovieCrew crew3 = makeCrew(3, "Crew 3");

        Movie dbMovie = new Movie();
        Movie movie = new Movie();
        movie.getCrew().addAll(Arrays.asList(crew1, crew2, crew3));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCrew",
                movie,
                dbMovie
        );

        assertEquals(
                "Crew list expected to have 3 elements",
                3,
                dbMovie.getCrew().size()
        );

        assertTrue(
                "Crew list expected to include all crew members",
                dbMovie.getCrew().containsAll(Arrays.asList(
                        crew1,
                        crew2,
                        crew3
                ))
        );
    }

    @Test
    public void testMergeCrewAddSome() throws Exception {
        MovieCrew crew1 = makeCrew(1, "Crew 1");
        MovieCrew crew2 = makeCrew(2, "Crew 2");
        MovieCrew crew3 = makeCrew(3, "Crew 3");

        Movie dbMovie = new Movie();
        dbMovie.getCrew().addAll(Arrays.asList(crew1, crew2));

        Movie movie = new Movie();
        movie.getCrew().addAll(Arrays.asList(crew1, crew2, crew3));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCrew",
                movie,
                dbMovie
        );

        assertEquals(
                "Crew list expected to have 3 elements",
                3,
                dbMovie.getCrew().size()
        );

        assertTrue(
                "Crew list expected to include all crew members",
                dbMovie.getCrew().containsAll(Arrays.asList(
                        crew1,
                        crew2,
                        crew3
                ))
        );
    }

    @Test
    public void testMergeCrewRemoveSome() throws Exception {
        MovieCrew crew1 = makeCrew(1, "Crew 1");
        MovieCrew crew2 = makeCrew(2, "Crew 2");
        MovieCrew crew3 = makeCrew(3, "Crew 3");

        Movie dbMovie = new Movie();
        dbMovie.getCrew().addAll(Arrays.asList(crew1, crew2, crew3));

        Movie movie = new Movie();
        movie.getCrew().addAll(Collections.singletonList(crew1));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCrew",
                movie,
                dbMovie
        );

        assertEquals(
                "Crew list expected to have 1 element",
                1,
                dbMovie.getCrew().size()
        );

        assertTrue(
                "Crew list expected to include crew members",
                dbMovie.getCrew().contains(crew1)
        );
    }

    @Test
    public void testMergeCrewUpdateSome() throws Exception {
        String crew1Name = "Crew 1";

        MovieCrew crew1 = makeCrew(1, crew1Name);
        MovieCrew crew2 = makeCrew(2, "Crew 2");
        MovieCrew crew3 = makeCrew(3, "Crew 3");

        Movie dbMovie = new Movie();
        dbMovie.getCrew().addAll(Arrays.asList(crew1, crew2));

        MovieCrew crew1Updated = makeCrew(1, crew1Name);
        crew1Updated.setPictureUrl("https://example.com/profile.jpg");

        Movie movie = new Movie();
        movie.getCrew().addAll(Arrays.asList(
                crew1Updated,
                crew2,
                crew3
        ));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCrew",
                movie,
                dbMovie
        );

        assertEquals(
                "Crew list expected to have 3 elements",
                3,
                dbMovie.getCrew().size()
        );


        // Verify crew1 was updated

        Optional<MovieCrew> crew1Opt = dbMovie.getCrew()
                .stream()
                .filter(crew -> crew.getName().equals(crew1Updated.getName()))
                .findFirst();
        assertTrue(
                "Crew1 expected to be present",
                crew1Opt.isPresent()
        );

        assertEquals(
                "Crew 1 values expected to match",
                crew1Updated.getId(),
                crew1Opt.get().getId()
        );
        assertEquals(
                "Crew 1 values expected to match",
                crew1Updated.getName(),
                crew1Opt.get().getName()
        );
        assertEquals(
                "Crew 1 values expected to match",
                crew1Updated.getPictureUrl(),
                crew1Opt.get().getPictureUrl()
        );
    }

    @Test
    public void testMergeCastAddAll() throws Exception {
        MovieCast cast1 = makeCast(1, "Cast 1");
        MovieCast cast2 = makeCast(2, "Cast 2");
        MovieCast cast3 = makeCast(3, "Cast 3");

        Movie dbMovie = new Movie();
        Movie movie = new Movie();
        movie.getCast().addAll(Arrays.asList(cast1, cast2, cast3));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCast",
                movie,
                dbMovie
        );

        assertEquals(
                "Cast list expected to have 3 elements",
                3,
                dbMovie.getCast().size()
        );

        assertTrue(
                "Cast list expected to include all cast members",
                dbMovie.getCast().containsAll(Arrays.asList(
                        cast1,
                        cast2,
                        cast3
                ))
        );
    }

    @Test
    public void testMergeCastAddSome() throws Exception {
        MovieCast cast1 = makeCast(1, "Cast 1");
        MovieCast cast2 = makeCast(2, "Cast 2");
        MovieCast cast3 = makeCast(3, "Cast 3");

        Movie dbMovie = new Movie();
        dbMovie.getCast().addAll(Arrays.asList(cast1, cast2));

        Movie movie = new Movie();
        movie.getCast().addAll(Arrays.asList(cast1, cast2, cast3));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCast",
                movie,
                dbMovie
        );

        assertEquals(
                "Cast list expected to have 3 elements",
                3,
                dbMovie.getCast().size()
        );

        assertTrue(
                "Cast list expected to include all cast members",
                dbMovie.getCast().containsAll(Arrays.asList(
                        cast1,
                        cast2,
                        cast3
                ))
        );
    }

    @Test
    public void testMergeCastRemoveSome() throws Exception {
        MovieCast cast1 = makeCast(1, "Cast 1");
        MovieCast cast2 = makeCast(2, "Cast 2");
        MovieCast cast3 = makeCast(3, "Cast 3");

        Movie dbMovie = new Movie();
        dbMovie.getCast().addAll(Arrays.asList(cast1, cast2, cast3));

        Movie movie = new Movie();
        movie.getCast().addAll(Collections.singletonList(cast1));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCast",
                movie,
                dbMovie
        );

        assertEquals(
                "Cast list expected to have 1 element",
                1,
                dbMovie.getCast().size()
        );

        assertTrue(
                "Cast list expected to include cast members",
                dbMovie.getCast().contains(cast1)
        );
    }

    @Test
    public void testMergeCastUpdateSome() throws Exception {
        String cast1Name = "Cast 1";

        MovieCast cast1 = makeCast(1, cast1Name);
        MovieCast cast2 = makeCast(2, "Cast 2");
        MovieCast cast3 = makeCast(3, "Cast 3");

        Movie dbMovie = new Movie();
        dbMovie.getCast().addAll(Arrays.asList(cast1, cast2));

        MovieCast cast1Updated = makeCast(1, cast1Name);
        cast1Updated.setCharacter("Some char");
        cast1Updated.setPictureUrl("https://example.com/profile.jpg");

        Movie movie = new Movie();
        movie.getCast().addAll(Arrays.asList(
                cast1Updated,
                cast2,
                cast3
        ));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeCast",
                movie,
                dbMovie
        );

        assertEquals(
                "Cast list expected to have 3 elements",
                3,
                dbMovie.getCast().size()
        );


        // Verify cast1 was updated

        Optional<MovieCast> cast1Opt = dbMovie.getCast()
                .stream()
                .filter(cast -> cast.getName().equals(cast1Updated.getName()))
                .findFirst();
        assertTrue(
                "Cast1 expected to be present",
                cast1Opt.isPresent()
        );

        assertEquals(
                "Cast 1 values expected to match",
                cast1Updated.getId(),
                cast1Opt.get().getId()
        );
        assertEquals(
                "Cast 1 values expected to match",
                cast1Updated.getName(),
                cast1Opt.get().getName()
        );
        assertEquals(
                "Cast 1 values expected to match",
                cast1Updated.getCharacter(),
                cast1Opt.get().getCharacter()
        );
        assertEquals(
                "Cast 1 values expected to match",
                cast1Updated.getPictureUrl(),
                cast1Opt.get().getPictureUrl()
        );
    }

    @Test
    public void testMergeGenresAddAllGenres() throws Exception {
        Movie movie = new Movie();
        Movie dbMovie = new Movie();

        MovieGenre action = makeGenre(1, "Action", eng);
        MovieGenre comedy = makeGenre(2, "Comedy", eng);
        MovieGenre sciFi = makeGenre(3, "Sci-Fi", eng);
        movie.getGenres().addAll(Arrays.asList(action, comedy, sciFi));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeGenres",
                movie,
                dbMovie
        );

        assertEquals(
                "All genres expected to be added to db movie",
                3,
                dbMovie.getGenres().size()
        );
        assertTrue(
                "'Action' genre expected to be added to db movie",
                dbMovie.getGenres().contains(action)
        );
        assertTrue(
                "'Comedy' genre expected to be added to db movie",
                dbMovie.getGenres().contains(comedy)
        );
        assertTrue(
                "'Sci-Fi' genre expected to be added to db movie",
                dbMovie.getGenres().contains(sciFi)
        );
    }

    @Test
    public void testMergeGenresAddSomeGenres() throws Exception {
        Movie movie = new Movie();
        Movie dbMovie = new Movie();

        MovieGenre action = makeGenre(1, "Action", eng);
        MovieGenre comedy = makeGenre(2, "Comedy", eng);
        MovieGenre sciFi = makeGenre(3, "Sci-Fi", eng);

        dbMovie.getGenres().addAll(Arrays.asList(action, comedy));
        movie.getGenres().addAll(Arrays.asList(action, comedy, sciFi));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeGenres",
                movie,
                dbMovie
        );

        assertEquals(
                "New genre expected to be added to db movie",
                3,
                dbMovie.getGenres().size()
        );
        assertTrue(
                "'Action' genre expected to be added to db movie",
                dbMovie.getGenres().contains(action)
        );
        assertTrue(
                "'Comedy' genre expected to be added to db movie",
                dbMovie.getGenres().contains(comedy)
        );
        assertTrue(
                "'Sci-Fi' genre expected to be added to db movie",
                dbMovie.getGenres().contains(sciFi)
        );
    }

    @Test
    public void testMergeGenresRemoveGenre() throws Exception {
        Movie movie = new Movie();
        Movie dbMovie = new Movie();

        MovieGenre action = makeGenre(1, "Action", eng);
        MovieGenre comedy = makeGenre(2, "Comedy", eng);
        MovieGenre sciFi = makeGenre(3, "Sci-Fi", eng);
        dbMovie.getGenres().addAll(Arrays.asList(action, comedy, sciFi));
        movie.getGenres().add(action);

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeGenres",
                movie,
                dbMovie
        );

        assertEquals(
                "Wrong genres count",
                1,
                dbMovie.getGenres().size()
        );

        assertFalse(
                "'Comedy' expected to be removed",
                dbMovie.getGenres().contains(comedy)
        );
        assertFalse(
                "'Sci-Fi' expected to be removed",
                dbMovie.getGenres().contains(sciFi)
        );
        assertTrue(
                "'Action' expected to be in movie",
                dbMovie.getGenres().contains(action)
        );
    }

    @Test
    public void testMergeGenresAddNewLocalizations() throws Exception {
        Movie movie = new Movie();
        Movie dbMovie = new Movie();

        MovieGenre action = makeGenre(1, "Action", eng);
        MovieGenre comedy = makeGenre(2, "Comedy", eng);
        MovieGenre sciFi = makeGenre(3, "Sci-Fi", eng);
        dbMovie.getGenres().addAll(Arrays.asList(action, comedy, sciFi));

        MovieGenre actionEsp = makeGenre(1, "Acción", esp);
        MovieGenre comedyEsp = makeGenre(2, "Comedia", esp);
        MovieGenre sciFiEsp = makeGenre(3, "Sci-Fi", esp);
        movie.getGenres().addAll(Arrays.asList(actionEsp, comedyEsp, sciFiEsp));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeGenres",
                movie,
                dbMovie
        );

        assertEquals(
                "Genres count expected to be the same",
                3,
                dbMovie.getGenres().size()
        );


        // Verify action genre and its localizations

        Optional<MovieGenre> actionOpt = dbMovie.getGenres()
                .stream()
                .filter(genre -> genre.getId() == 1)
                .findAny();
        assertTrue(
                "Action genre expected to be present",
                actionOpt.isPresent()
        );
        assertEquals(
                "Action genre expected to have 2 localizations",
                2,
                actionOpt.get().getLocalizedGenres().size()
        );
        assertTrue(
                "Action expected to have 'esp' and 'eng' localizations",
                actionOpt.get().getLocalizedGenres().containsAll(Arrays.asList(
                        action.getLocalizedGenres().get(0),
                        actionEsp.getLocalizedGenres().get(0)
                ))
        );


        // Verify comedy genre and its localizations

        Optional<MovieGenre> comedyOpt = dbMovie.getGenres()
                .stream()
                .filter(genre -> genre.getId() == 2)
                .findAny();
        assertTrue(
                "Comedy genre expected to be present",
                comedyOpt.isPresent()
        );
        assertEquals(
                "Comedy genre expected to have 2 localizations",
                2,
                comedyOpt.get().getLocalizedGenres().size()
        );
        assertTrue(
                "Comedy expected to have 'esp' and 'eng' localizations",
                comedyOpt.get().getLocalizedGenres().containsAll(Arrays.asList(
                        comedy.getLocalizedGenres().get(0),
                        comedyEsp.getLocalizedGenres().get(0)
                ))
        );


        // Verify sci-Fi genre and its localizations

        Optional<MovieGenre> sciFiOpt = dbMovie.getGenres()
                .stream()
                .filter(genre -> genre.getId() == 3)
                .findAny();
        assertTrue(
                "Sci-Fi genre expected to be present",
                sciFiOpt.isPresent()
        );
        assertEquals(
                "Sci-Fi genre expected to have 2 localizations",
                2,
                sciFiOpt.get().getLocalizedGenres().size()
        );
        assertTrue(
                "Sci-Fi expected to have 'esp' and 'eng' localizations",
                sciFiOpt.get().getLocalizedGenres().containsAll(Arrays.asList(
                        sciFi.getLocalizedGenres().get(0),
                        sciFiEsp.getLocalizedGenres().get(0)
                ))
        );
    }

    @Test
    public void testMergeGenresUpdateLocalizations() throws Exception {
        Movie movie = new Movie();
        Movie dbMovie = new Movie();

        MovieGenre actionTypo = makeGenre(1, "Action Typo", eng);
        MovieGenre comedyTypo = makeGenre(2, "Comedy Typo", eng);
        dbMovie.getGenres().addAll(Arrays.asList(actionTypo, comedyTypo));

        String actionName = "Action";
        String comedyName = "Comedy";
        MovieGenre action = makeGenre(1, actionName, eng);
        MovieGenre comedy = makeGenre(2, comedyName, eng);
        movie.getGenres().addAll(Arrays.asList(action, comedy));

        WhiteboxImpl.invokeMethod(
                locMovieSvc,
                "mergeGenres",
                movie,
                dbMovie
        );

        assertEquals(
                "Genres count expected to be the same",
                2,
                dbMovie.getGenres().size()
        );


        // Verify 'action' genre has been updated

        Optional<MovieGenre> actionOpt = dbMovie.getGenres()
                .stream()
                .filter(genre -> genre.getId() == 1)
                .findFirst();
        assertTrue(
                "Action genre expected to be present",
                actionOpt.isPresent()
        );

        assertEquals(
                "Action genre loc expected to be updated",
                actionName,
                actionOpt.get().getLocalizedGenres().get(0).getName()
        );


        // Verify 'comedy' genre has been updated

        Optional<MovieGenre> comedyOpt = dbMovie.getGenres()
                .stream()
                .filter(genre -> genre.getId() == 2)
                .findFirst();

        assertTrue(
                "Comedy genre expected to be present",
                comedyOpt.isPresent()
        );

        assertEquals(
                "Comedy genre loc expected to be updated",
                comedyName,
                comedyOpt.get().getLocalizedGenres().get(0).getName()
        );
    }

    private LocalizedMovie makeLocMovie(String title, Language lang) {
        LocalizedMovie locMovie = new LocalizedMovie();
        locMovie.setTitle(title);
        locMovie.setLanguage(lang);

        return locMovie;
    }

    private MovieCrew makeCrew(long id, String name) {
        MovieCrew crewMember = new MovieCrew();
        crewMember.setId(id);
        crewMember.setName(name);

        return crewMember;
    }

    private MovieCast makeCast(long id, String name) {
        MovieCast cast = new MovieCast();
        cast.setId(id);
        cast.setName(name);

        return cast;
    }

    private MovieGenre makeGenre(long id, String name, Language lang) {
        LocalizedMovieGenre locGenre = new LocalizedMovieGenre();
        locGenre.setName(name);
        locGenre.setLanguage(lang);

        MovieGenre genre = new MovieGenre();
        genre.setId(id);
        genre.getLocalizedGenres().add(locGenre);
        return genre;
    }
}