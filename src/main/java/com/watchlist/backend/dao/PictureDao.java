package com.watchlist.backend.dao;

import com.watchlist.backend.model.Picture;
import org.springframework.data.repository.CrudRepository;

public interface PictureDao extends CrudRepository<Picture, Long> {
}
