package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    //Get user object by mail
    public User getUser(String mail){
        String checkUser = "SELECT * FROM User WHERE mail = ?";

        try{
            return jdbcTemplate.queryForObject(checkUser, userRowMapper, mail);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //Get password as String by user id
    public String getPassword(int id){
        String checkPassword = "SELECT password FROM user WHERE id = ?";

        try{
            return jdbcTemplate.queryForObject(checkPassword, String.class, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
