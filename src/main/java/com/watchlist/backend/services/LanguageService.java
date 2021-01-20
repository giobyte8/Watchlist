package com.watchlist.backend.services;

import com.watchlist.backend.dao.LanguageDao;
import com.watchlist.backend.entities.db.Language;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {

    private final LanguageDao langDao;

    public LanguageService(LanguageDao langDao) {
        this.langDao = langDao;
    }

    /**
     * Retrieves entity for provided iso639. If code is invalid
     * will default to 'en-US'
     * @param iso639 ISO639 language code
     * @return entity for provided iso code or default english
     */
    public Language parseISO639(String iso639) {
        return langDao.findByIso639(sanitizeISO639(iso639));
    }

    /**
     * Verifies that provided code is valid and defaults
     * to english "en-US" in case of invalid code
     *
     * @param iso639 Language code
     * @return Valid ISO code
     */
    public String sanitizeISO639(String iso639) {
        if (iso639.equals(Language.ISO_EN_US)) {
            return iso639;
        }

        if (iso639.equals(Language.ISO_ES_MX)) {
            return iso639;
        }

        return Language.ISO_EN_US;
    }
}
