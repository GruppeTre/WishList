package com.mavi.wishlist.repository;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.mavi.wishlist.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class  UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public final RowMapper<User> userRowMapper = ( (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setMail(rs.getString("mail"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));

        return user;
    });

    //Get user object by mail
    public User getUser(String mail){
        String query = "SELECT * FROM account WHERE mail = ?";

        try{
            return jdbcTemplate.queryForObject(query, userRowMapper, mail);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //Get user object by id
    public User getUserById(int id){
        String query = "SELECT * FROM account WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(query, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //get user from given refString
    public Integer getUserIdFromRefString(String refString) {
        String query = "SELECT id FROM account WHERE refString = ?";

        try {
            return jdbcTemplate.queryForObject(query, Integer.class, refString);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //Get wishlist reference string from user id
    public String getRefStringFromId(Integer id) {
        String query = "SELECT refString FROM account WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(query, String.class, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //Inserts a user in the database
    public User addUser(User user, String urlReference) {

        String query = "INSERT IGNORE INTO account (password, mail, firstname, lastName, refString) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //Uses a prepared statement in lambda form
        try{
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getMail());
                ps.setString(3, user.getFirstName());
                ps.setString(4, user.getLastName());
                //12byte key insert
                ps.setString(5, urlReference);
                return ps;
            }, keyHolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Returns the keyholder for check
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new user");
        }

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    //Updates user
    public User updateUser(User user) {
        String query = "UPDATE account SET firstname = ?, lastname = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(query, user.getFirstName(), user.getLastName(), user.getId());

        //Check if more than 1 row was affected
        if(rowsAffected > 1){
            throw new RuntimeException("Multiple users with same ID found!");
        }

        //Check if no row was affected
        if(rowsAffected == 0){
            return null;
        }

        return user;
    }

    //Deletes user
    public User deleteUser(User user) {
        String query = "DELETE FROM account WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(query, user.getId());

        //Checks if no rows was affected
        if (rowsAffected == 0) {
            return null;
        }

        return user;
    }

}
