CREATE TABLE auth_provider(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE role(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE user(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(500) NOT NULL,
    email VARCHAR(1000) NOT NULL,
    picture VARCHAR(5000) NOT NULL,
    last_login DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    role_id INT NOT NULL,

    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE credential(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(1000) NOT NULL,
    user_id INT NOT NULL,
    auth_provider_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (auth_provider_id) REFERENCES auth_provider(id)
);

CREATE TABLE session(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(1000) NOT NULL,
    os VARCHAR(255),
    os_version VARCHAR(255),
    browser VARCHAR(255),
    browser_version VARCHAR(255),
    device VARCHAR(255),
    expiration DATETIME NOT NULL,
    active BOOL DEFAULT TRUE,
    user_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE watchlist(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    is_default BOOL NOT NULL DEFAULT FALSE,
    deleted BOOL NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE watchlist_permission(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE user_has_watchlist(
    id INT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    user_id INT NOT NULL,
    shared_by INT,
    watchlist_id INT NOT NULL,
    watchlist_permission_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (shared_by) REFERENCES user(id),
    FOREIGN KEY (watchlist_id) REFERENCES watchlist(id),
    FOREIGN KEY (watchlist_permission_id) REFERENCES watchlist_permission(id)
);

CREATE TABLE movie(
    id INT PRIMARY KEY AUTO_INCREMENT,
    tmdb_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255) NOT NULL,
    release_date DATE, # Can we make this NOT NULL?
    runtime INT NOT NULL,
    synopsis VARCHAR(5000) NOT NULL,
    rating DOUBLE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE watchlist_has_movie(
    id INT PRIMARY KEY AUTO_INCREMENT,
    added_at DATETIME NOT NULL,
    seen_at DATETIME,
    watchlist_id INT NOT NULL,
    movie_id INT NOT NULL,
    added_by INT NOT NULL,

    FOREIGN KEY (watchlist_id) REFERENCES watchlist(id),
    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (added_by) REFERENCES user(id)
);

CREATE TABLE genre(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE movie_has_genre(
    id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,

    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE picture_category(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE picture(
    id INT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(5000) NOT NULL,
    movie_id INT NOT NULL,
    picture_category_id INT NOT NULL,

    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (picture_category_id) REFERENCES picture_category(id)
);

CREATE TABLE crew_category(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE crew(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(500) NOT NULL,
    character_name VARCHAR(500) NOT NULL,
    picture_url VARCHAR(5000),
    movie_id INT NOT NULL,
    crew_category_id INT NOT NULL,

    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (crew_category_id) REFERENCES crew_category(id)
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
