INSERT INTO auth_provider
VALUES (1, 'Facebook'),
       (2, 'Google');

INSERT INTO role
VALUES (1, 'admin'),
       (2, 'watcher');

INSERT INTO watchlist_permission
VALUES (1, 'owner'),
       (2, 'collaborator'),
       (3, 'follower');

INSERT INTO user (id, name, email, picture, last_login, created_at, updated_at, role_id)
VALUES (101, 'John Doe', 'john@example.com', 'https://example.com', null, now(), now(), 2);