package com.watchlist.backend.dao;

import com.watchlist.backend.model.PictureCategory;
import org.springframework.data.repository.CrudRepository;

public interface PictureCategoryDao
        extends CrudRepository<PictureCategory, Long> {
}
