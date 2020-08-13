CREATE TABLE auth_provider(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE role(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE user(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(500) NOT NULL,
    email VARCHAR(1000) NOT NULL,
    picture VARCHAR(5000) NOT NULL,
    last_login DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    role_id BIGINT NOT NULL,

    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE credential(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(1000) NOT NULL,
    user_id BIGINT NOT NULL,
    auth_provider_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (auth_provider_id) REFERENCES auth_provider(id)
);

CREATE TABLE session(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(1000) NOT NULL,
    os VARCHAR(255),
    os_version VARCHAR(255),
    browser VARCHAR(255),
    browser_version VARCHAR(255),
    device VARCHAR(255),
    expiration DATETIME NOT NULL,
    active BOOL DEFAULT TRUE,
    user_id BIGINT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE watchlist(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    is_default BOOL NOT NULL DEFAULT FALSE,
    deleted BOOL NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE watchlist_permission(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE user_has_watchlist(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    shared_by BIGINT,
    watchlist_id BIGINT NOT NULL,
    watchlist_permission_id BIGINT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (shared_by) REFERENCES user(id),
    FOREIGN KEY (watchlist_id) REFERENCES watchlist(id),
    FOREIGN KEY (watchlist_permission_id) REFERENCES watchlist_permission(id)
);

CREATE TABLE movie(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tmdb_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255) NOT NULL,
    release_date DATE, # Can we make this NOT NULL?
    runtime INT NOT NULL,
    synopsis VARCHAR(5000) NOT NULL,
    rating DOUBLE NOT NULL,
    poster_path VARCHAR(2000),
    backdrop_path VARCHAR(2000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE watchlist_has_movie(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    added_at DATETIME NOT NULL,
    seen_at DATETIME,
    watchlist_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    added_by BIGINT NOT NULL,

    FOREIGN KEY (watchlist_id) REFERENCES watchlist(id),
    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (added_by) REFERENCES user(id)
);

CREATE TABLE genre(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE movie_has_genre(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,

    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE `cast`(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(500) NOT NULL,
    `character` VARCHAR(500),
    picture_url VARCHAR(5000),
    movie_id BIGINT NOT NULL,

    FOREIGN KEY (movie_id) REFERENCES movie(id)
);

CREATE TABLE crew(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(500) NOT NULL,
    job VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    picture_url VARCHAR(5000),
    movie_id BIGINT NOT NULL,

    FOREIGN KEY (movie_id) REFERENCES movie(id)
);


--
-- Initial data seeding

INSERT INTO auth_provider VALUES
    (1, 'Facebook'),
    (2, 'Google');

INSERT INTO role VALUES
    (1, 'admin'),
    (2, 'watcher');

INSERT INTO watchlist_permission VALUES
    (1, 'owner'),
    (2, 'collaborator'),
    (3, 'follower');
