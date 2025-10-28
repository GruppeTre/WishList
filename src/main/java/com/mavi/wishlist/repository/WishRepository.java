package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.Wish;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class WishRepository {

    private final JdbcTemplate jdbcTemplate;

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Wish insertWish(Wish wish) {
        String query = "INSERT IGNORE INTO wish (name, link) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try{
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, wish.getName());
                ps.setString(2, wish.getLink());
                return ps;
            }, keyHolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new wish");
        }

        wish.setId(keyHolder.getKey().intValue());

        return wish;
    }

    public int insertToJunction(Integer wishId, Integer userId) {
        String query = "INSERT INTO wishlist (user_id, wish_id) VALUES (?, ?)";

        int rowsAffected = jdbcTemplate.update(query, userId, wishId);

        if (rowsAffected != 1) {
            throw new RuntimeException("Couldn't insert into junction table!");
        }

        return rowsAffected;
    }
}
