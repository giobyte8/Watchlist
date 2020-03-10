package com.watchlist.backend.dao;

import com.watchlist.backend.model.Role;
import com.watchlist.backend.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserDaoTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserDao userDao;

    @Test
    public void save() {
        Role roleWatcher = entityManager.getReference(Role.class, 2L);

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPicture("https://example.com/1.jpg");
        user.setRole(roleWatcher);
        userDao.save(user);

        long userId = user.getId();
        Optional<User> userDb = userDao.findById(userId);

        assertTrue("User was saved", userId > 0);
        assertTrue("User was recovered from db", userDb.isPresent());
        assertEquals(
                "Persisted user is equal to retrieved",
                user.getId(),
                userDb.get().getId()
        );
        assertEquals(
                "Recovered user has right role",
                user.getRole(),
                roleWatcher
        );
    }

    @Test
    public void saveWithoutRole() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPicture("https://example.com/1.jpg");
        userDao.save(user);

        assertEquals(1, user.getId());
    }
}
