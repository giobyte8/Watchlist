--
-- Default required data in order to system works
--

-- Auth provider
INSERT INTO auth_provider VALUES
    (1, 'Facebook'),
    (2, 'Google');

-- Roles
INSERT INTO role VALUES
    (1, 'admin'),
    (2, 'watcher');