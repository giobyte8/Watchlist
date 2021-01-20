package com.watchlist.backend.services;

import com.watchlist.backend.dao.LanguageDao;
import com.watchlist.backend.entities.db.Language;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class LanguageServiceTest {

    @Mock
    private LanguageDao languageDao;

    @InjectMocks
    private LanguageService langService;

    @Test
    public void testSanitizeISO639ESMX() {
        String lang = "es-MX";

        String sanitizedLang = langService.sanitizeISO639(lang);
        Assert.assertEquals(
                "es-MX expected to be returned",
                Language.ISO_ES_MX,
                sanitizedLang
        );
    }

    @Test
    public void testSanitizeISO639ENUS() {
        String lang = "en-US";

        String sanitizedLang = langService.sanitizeISO639(lang);
        Assert.assertEquals(
                "en-US expected to be returned",
                Language.ISO_EN_US,
                sanitizedLang
        );
    }

    @Test
    public void testSanitizeISO639UnsupportedLang() {
        String lang = "fr-CA";

        String sanitizedLang = langService.sanitizeISO639(lang);
        Assert.assertEquals(
                "en-US expected to be returned",
                Language.ISO_EN_US,
                sanitizedLang
        );
    }
}
