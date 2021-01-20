package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Language;
import org.springframework.data.repository.CrudRepository;

public interface LanguageDao extends CrudRepository<Language, Long> {

    Language findByIso639(String iso639);
}
