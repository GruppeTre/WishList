package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public final RowMapper<User> userRowMapper = ( (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setMail(rs.getString("mail"));
        user.setPasswordHash(rs.getString("password"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));

        return user;
    });

    public List<User> getUser(String mail){
        String checkUser = "SELECT * FROM User WHERE mail = ?";

        return jdbcTemplate.query(checkUser, userRowMapper, mail);
    }

    public List<User> getPassword(String mail, String password){
        String checkPassword = "SELECT password FROM user WHERE mail = ?";

        return jdbcTemplate.query(checkPassword, userRowMapper, mail);
    }

    public User userLogin(User user){
    }
}
