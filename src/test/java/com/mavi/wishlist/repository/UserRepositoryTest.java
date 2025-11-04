package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @BeforeEach
    void setUp() {
    }

    /*
    ('adam1234', 'adam@mail.dk', '"Adam', 'Adamsen', '80ee1e11b3b393efd0872de7'),
	('erik1234', 'Erik@gmail.com', 'Erik', 'Eriksen', '0a0609f01458afb981cfeead');
     */

    @Test
    void shouldGetUserByMail() throws Exception {
        User user = repository.getUser("adam@mail.dk");

        assertThat(user.getMail()).isEqualTo("adam@mail.dk");
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getPassword()).isEqualTo("adam1234");
        assertThat(user.getFirstName()).isEqualTo("Adam");
        assertThat(user.getLastName()).isEqualTo("Adamsen");
        //assertThat().isEqualTo("80ee1e11b3b393efd0872de7");
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = repository.getUserById(1);

        assertThat(user.getMail()).isEqualTo("adam@mail.dk");
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getPassword()).isEqualTo("adam1234");
        assertThat(user.getFirstName()).isEqualTo("Adam");
        assertThat(user.getLastName()).isEqualTo("Adamsen");
    }

    @Test
    void shouldAddUser() throws Exception {
        User newUser = new User();
        newUser.setFirstName("Peter");
        newUser.setLastName("Petersen");
        newUser.setMail("peter@live.com");
        newUser.setPassword("peter1234");

        String refString = "0b0609f01458afb981cfeead";

        repository.addUser(newUser, refString);

        User user = repository.getUser(newUser.getMail());

        assertThat(user).isNotNull();
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = repository.getUser("adam@mail.dk");
        user.setFirstName("Søren");

        repository.updateUser(user);

        User updatedUser = repository.getUser("adam@mail.dk");

        assertThat(updatedUser.getFirstName()).isEqualTo("Søren");
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = repository.getUser("adam@mail.dk");

        repository.deleteUser(user);

        User deletedUser = repository.getUser("adam@mail.dk");

        assertThat(deletedUser).isNull();


    }
}