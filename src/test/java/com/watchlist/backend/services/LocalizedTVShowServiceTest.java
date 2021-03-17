package com.watchlist.backend.services;

import com.watchlist.backend.dao.TVShowCastDao;
import com.watchlist.backend.dao.TVShowCrewDao;
import com.watchlist.backend.dao.TVShowDao;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.LocalizedTVShow;
import com.watchlist.backend.entities.db.LocalizedTVShowGenre;
import com.watchlist.backend.entities.db.TVShow;
import com.watchlist.backend.entities.db.TVShowCast;
import com.watchlist.backend.entities.db.TVShowCrew;
import com.watchlist.backend.entities.db.TVShowGenre;
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
@PrepareForTest(LocalizedTVShowService.class)
public class LocalizedTVShowServiceTest {

    @Mock
    private TVShowDao tvShowDao;

    @Mock
    private TVShowCrewDao tvShowCrewDao;

    @Mock
    private TVShowCastDao tvShowCastDao;

    @InjectMocks
    private LocalizedTVShowService locTvShowService;

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
        LocalizedTVShow loc1 = makeLocTvShow("Fight Club", eng);
        LocalizedTVShow loc2 = makeLocTvShow("El Club de la Pelea", esp);

        TVShow dbTvShow = new TVShow();
        TVShow tvShow = new TVShow();

        dbTvShow.getLocalizedTVShows().add(loc1);
        tvShow.getLocalizedTVShows().addAll(Arrays.asList(
                loc1,
                loc2
        ));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeLocalizations",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "TVShow localizations count expected to be eq 2",
                2,
                dbTvShow.getLocalizedTVShows().size()
        );

        assertTrue(
                "TVShow expected to contain all localizations",
                dbTvShow.getLocalizedTVShows().containsAll(Arrays.asList(
                        loc1,
                        loc2
                ))
        );
    }

    @Test
    public void testMergeLocalizationsUpdateLoc() throws Exception {
        LocalizedTVShow loc1 = makeLocTvShow("Gome of Thrones", eng);
        LocalizedTVShow loc2 = makeLocTvShow("Localized title", esp);

        TVShow dbTvShow = new TVShow();
        dbTvShow.getLocalizedTVShows().addAll(Arrays.asList(
                loc1,
                loc2
        ));

        LocalizedTVShow loc2Updated = makeLocTvShow("Updated title", esp);
        TVShow tvShow = new TVShow();
        tvShow.getLocalizedTVShows().addAll(Arrays.asList(
                loc1,
                loc2Updated
        ));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeLocalizations",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "TV Show localizations count expected to be eq 2",
                2,
                dbTvShow.getLocalizedTVShows().size()
        );

        assertEquals(
                "First tv show localization matches eng lang title",
                dbTvShow
                        .getLocalizedTVShows()
                        .get(0)
                        .getTitle(),
                loc1.getTitle()
        );

        assertEquals(
                "Second tv show localization matches spanish lang title",
                dbTvShow
                        .getLocalizedTVShows()
                        .get(1)
                        .getTitle(),
                loc2Updated.getTitle()
        );
    }

    @Test
    public void testMergeCrewAddAll() throws Exception {
        TVShowCrew crew1 = makeCrew(1, "Crew 1");
        TVShowCrew crew2 = makeCrew(2, "Crew 2");
        TVShowCrew crew3 = makeCrew(3, "Crew 3");

        TVShow dbTvShow = new TVShow();
        TVShow tvShow = new TVShow();
        tvShow.getCrew().addAll(Arrays.asList(crew1, crew2, crew3));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCrew",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Crew list expected to have 3 elements",
                3,
                dbTvShow.getCrew().size()
        );

        assertTrue(
                "Crew list expected to include all crew members",
                dbTvShow.getCrew().containsAll(Arrays.asList(
                        crew1,
                        crew2,
                        crew3
                ))
        );
    }

    @Test
    public void testMergeCrewAddSome() throws Exception {
        TVShowCrew crew1 = makeCrew(1, "Crew 1");
        TVShowCrew crew2 = makeCrew(2, "Crew 2");
        TVShowCrew crew3 = makeCrew(3, "Crew 3");

        TVShow dbTvShow = new TVShow();
        dbTvShow.getCrew().addAll(Arrays.asList(crew1, crew2));

        TVShow tvShow = new TVShow();
        tvShow.getCrew().addAll(Arrays.asList(crew1, crew2, crew3));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCrew",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Crew list expected to have 3 elements",
                3,
                dbTvShow.getCrew().size()
        );

        assertTrue(
                "Crew list expected to include all crew members",
                dbTvShow.getCrew().containsAll(Arrays.asList(
                        crew1,
                        crew2,
                        crew3
                ))
        );
    }

    @Test
    public void testMergeCrewRemoveSome() throws Exception {
        TVShowCrew crew1 = makeCrew(1, "Crew 1");
        TVShowCrew crew2 = makeCrew(2, "Crew 2");
        TVShowCrew crew3 = makeCrew(3, "Crew 3");

        TVShow dbTvShow = new TVShow();
        dbTvShow.getCrew().addAll(Arrays.asList(crew1, crew2, crew3));

        TVShow tvShow = new TVShow();
        tvShow.getCrew().addAll(Collections.singletonList(crew1));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCrew",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Crew list expected to have 1 element",
                1,
                dbTvShow.getCrew().size()
        );

        assertTrue(
                "Crew list expected to include crew members",
                dbTvShow.getCrew().contains(crew1)
        );
    }

    @Test
    public void testMergeCrewUpdateSome() throws Exception {
        String crew1Name = "Crew 1";

        TVShowCrew crew1 = makeCrew(1, crew1Name);
        TVShowCrew crew2 = makeCrew(2, "Crew 2");
        TVShowCrew crew3 = makeCrew(3, "Crew 3");

        TVShow dbTvShow = new TVShow();
        dbTvShow.getCrew().addAll(Arrays.asList(crew1, crew2));

        TVShowCrew crew1Updated = makeCrew(1, crew1Name);
        crew1Updated.setPictureUrl("https://example.com/profile.jpg");

        TVShow tvShow = new TVShow();
        tvShow.getCrew().addAll(Arrays.asList(
                crew1Updated,
                crew2,
                crew3
        ));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCrew",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Crew list expected to have 3 elements",
                3,
                dbTvShow.getCrew().size()
        );


        // Verify crew1 was updated

        Optional<TVShowCrew> crew1Opt = dbTvShow
                .getCrew()
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
        TVShowCast cast1 = makeCast(1, "Cast 1");
        TVShowCast cast2 = makeCast(2, "Cast 2");
        TVShowCast cast3 = makeCast(3, "Cast 3");

        TVShow dbTvShow = new TVShow();
        TVShow tvShow = new TVShow();
        tvShow.getCast().addAll(Arrays.asList(cast1, cast2, cast3));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCast",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Cast list expected to have 3 elements",
                3,
                dbTvShow.getCast().size()
        );

        assertTrue(
                "Cast list expected to include all cast members",
                dbTvShow.getCast().containsAll(Arrays.asList(
                        cast1,
                        cast2,
                        cast3
                ))
        );
    }

    @Test
    public void testMergeCastAddSome() throws Exception {
        TVShowCast cast1 = makeCast(1, "Cast 1");
        TVShowCast cast2 = makeCast(2, "Cast 2");
        TVShowCast cast3 = makeCast(3, "Cast 3");

        TVShow dbTvShow = new TVShow();
        dbTvShow.getCast().addAll(Arrays.asList(cast1, cast2));

        TVShow tvShow = new TVShow();
        tvShow.getCast().addAll(Arrays.asList(cast1, cast2, cast3));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCast",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Cast list expected to have 3 elements",
                3,
                dbTvShow.getCast().size()
        );

        assertTrue(
                "Cast list expected to include all cast members",
                dbTvShow.getCast().containsAll(Arrays.asList(
                        cast1,
                        cast2,
                        cast3
                ))
        );
    }

    @Test
    public void testMergeCastRemoveSome() throws Exception {
        TVShowCast cast1 = makeCast(1, "Cast 1");
        TVShowCast cast2 = makeCast(2, "Cast 2");
        TVShowCast cast3 = makeCast(3, "Cast 3");

        TVShow dbTvShow = new TVShow();
        dbTvShow.getCast().addAll(Arrays.asList(cast1, cast2, cast3));

        TVShow tvShow = new TVShow();
        tvShow.getCast().addAll(Collections.singletonList(cast1));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCast",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Cast list expected to have 1 element",
                1,
                dbTvShow.getCast().size()
        );

        assertTrue(
                "Cast list expected to include cast members",
                dbTvShow.getCast().contains(cast1)
        );
    }

    @Test
    public void testMergeCastUpdateSome() throws Exception {
        String cast1Name = "Cast 1";

        TVShowCast cast1 = makeCast(1, cast1Name);
        TVShowCast cast2 = makeCast(2, "Cast 2");
        TVShowCast cast3 = makeCast(3, "Cast 3");

        TVShow dbTvShow = new TVShow();
        dbTvShow.getCast().addAll(Arrays.asList(cast1, cast2));

        TVShowCast cast1Updated = makeCast(1, cast1Name);
        cast1Updated.setCharacter("Some char");
        cast1Updated.setPictureUrl("https://example.com/profile.jpg");

        TVShow tvShow = new TVShow();
        tvShow.getCast().addAll(Arrays.asList(
                cast1Updated,
                cast2,
                cast3
        ));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeCast",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Cast list expected to have 3 elements",
                3,
                dbTvShow.getCast().size()
        );


        // Verify cast1 was updated

        Optional<TVShowCast> cast1Opt = dbTvShow
                .getCast()
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
        TVShow tvShow = new TVShow();
        TVShow dbTvShow = new TVShow();

        TVShowGenre action = makeGenre(1, "Action", eng);
        TVShowGenre comedy = makeGenre(2, "Comedy", eng);
        TVShowGenre sciFi = makeGenre(3, "Sci-Fi", eng);
        tvShow.getGenres().addAll(Arrays.asList(action, comedy, sciFi));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeGenres",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "All genres expected to be added to db tv show",
                3,
                dbTvShow.getGenres().size()
        );
        assertTrue(
                "'Action' genre expected to be added to db tv show",
                dbTvShow.getGenres().contains(action)
        );
        assertTrue(
                "'Comedy' genre expected to be added to db tv show",
                dbTvShow.getGenres().contains(comedy)
        );
        assertTrue(
                "'Sci-Fi' genre expected to be added to db tv show",
                dbTvShow.getGenres().contains(sciFi)
        );
    }

    @Test
    public void testMergeGenresAddSomeGenres() throws Exception {
        TVShow tvShow = new TVShow();
        TVShow dbTvShow = new TVShow();

        TVShowGenre action = makeGenre(1, "Action", eng);
        TVShowGenre comedy = makeGenre(2, "Comedy", eng);
        TVShowGenre sciFi = makeGenre(3, "Sci-Fi", eng);

        dbTvShow.getGenres().addAll(Arrays.asList(action, comedy));
        tvShow.getGenres().addAll(Arrays.asList(action, comedy, sciFi));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeGenres",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "New genre expected to be added to db tv show",
                3,
                dbTvShow.getGenres().size()
        );
        assertTrue(
                "'Action' genre expected to be added to db tv show",
                dbTvShow.getGenres().contains(action)
        );
        assertTrue(
                "'Comedy' genre expected to be added to db tv show",
                dbTvShow.getGenres().contains(comedy)
        );
        assertTrue(
                "'Sci-Fi' genre expected to be added to db tv show",
                dbTvShow.getGenres().contains(sciFi)
        );
    }

    @Test
    public void testMergeGenresRemoveGenre() throws Exception {
        TVShow tvShow = new TVShow();
        TVShow dbTvShow = new TVShow();

        TVShowGenre action = makeGenre(1, "Action", eng);
        TVShowGenre comedy = makeGenre(2, "Comedy", eng);
        TVShowGenre sciFi = makeGenre(3, "Sci-Fi", eng);
        dbTvShow.getGenres().addAll(Arrays.asList(action, comedy, sciFi));
        tvShow.getGenres().add(action);

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeGenres",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Wrong genres count",
                1,
                dbTvShow.getGenres().size()
        );

        assertFalse(
                "'Comedy' expected to be removed",
                dbTvShow.getGenres().contains(comedy)
        );
        assertFalse(
                "'Sci-Fi' expected to be removed",
                dbTvShow.getGenres().contains(sciFi)
        );
        assertTrue(
                "'Action' expected to be in tv show",
                dbTvShow.getGenres().contains(action)
        );
    }

    @Test
    public void testMergeGenresAddNewLocalizations() throws Exception {
        TVShow tvShow = new TVShow();
        TVShow dbTvShow = new TVShow();

        TVShowGenre action = makeGenre(1, "Action", eng);
        TVShowGenre comedy = makeGenre(2, "Comedy", eng);
        TVShowGenre sciFi = makeGenre(3, "Sci-Fi", eng);
        dbTvShow.getGenres().addAll(Arrays.asList(action, comedy, sciFi));

        TVShowGenre actionEsp = makeGenre(1, "Acción", esp);
        TVShowGenre comedyEsp = makeGenre(2, "Comedia", esp);
        TVShowGenre sciFiEsp = makeGenre(3, "Sci-Fi", esp);
        tvShow.getGenres().addAll(Arrays.asList(actionEsp, comedyEsp, sciFiEsp));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeGenres",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Genres count expected to be the same",
                3,
                dbTvShow.getGenres().size()
        );


        // Verify action genre and its localizations

        Optional<TVShowGenre> actionOpt = dbTvShow
                .getGenres()
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

        Optional<TVShowGenre> comedyOpt = dbTvShow.getGenres()
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

        Optional<TVShowGenre> sciFiOpt = dbTvShow
                .getGenres()
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
        TVShow tvShow = new TVShow();
        TVShow dbTvShow = new TVShow();

        TVShowGenre actionTypo = makeGenre(1, "Action Typo", eng);
        TVShowGenre comedyTypo = makeGenre(2, "Comedy Typo", eng);
        dbTvShow.getGenres().addAll(Arrays.asList(actionTypo, comedyTypo));

        String actionName = "Action";
        String comedyName = "Comedy";
        TVShowGenre action = makeGenre(1, actionName, eng);
        TVShowGenre comedy = makeGenre(2, comedyName, eng);
        tvShow.getGenres().addAll(Arrays.asList(action, comedy));

        WhiteboxImpl.invokeMethod(
                locTvShowService,
                "mergeGenres",
                tvShow,
                dbTvShow
        );

        assertEquals(
                "Genres count expected to be eq 2",
                2,
                dbTvShow.getGenres().size()
        );


        // Verify 'action' genre has been updated

        Optional<TVShowGenre> actionOpt = dbTvShow.getGenres()
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

        Optional<TVShowGenre> comedyOpt = dbTvShow
                .getGenres()
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

    private LocalizedTVShow makeLocTvShow(String title, Language lang) {
        LocalizedTVShow locTvShow = new LocalizedTVShow();
        locTvShow.setTitle(title);
        locTvShow.setLanguage(lang);

        return locTvShow;
    }

    private TVShowCrew makeCrew(long id, String name) {
        TVShowCrew crewMember = new TVShowCrew();
        crewMember.setId(id);
        crewMember.setName(name);

        return crewMember;
    }

    private TVShowCast makeCast(long id, String name) {
        TVShowCast cast = new TVShowCast();
        cast.setId(id);
        cast.setName(name);

        return cast;
    }

    private TVShowGenre makeGenre(long id, String name, Language lang) {
        LocalizedTVShowGenre locGenre = new LocalizedTVShowGenre();
        locGenre.setName(name);
        locGenre.setLanguage(lang);

        TVShowGenre genre = new TVShowGenre();
        genre.setId(id);
        genre.getLocalizedGenres().add(locGenre);
        return genre;
    }
}
