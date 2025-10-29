package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.Wish;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WishRepository {

    private final RowMapper<Wish> rowMapper = (rs, rowNum) -> {
        Wish wish = new Wish();
        wish.setId(rs.getInt("id"));
        wish.setName(rs.getString("name"));
        wish.setLink(rs.getString("link"));
        return wish;
    };

    private final JdbcTemplate jdbcTemplate;

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Wish getWish(Integer wishId){
        String selectQuery = "SElECT * FROM Wish WHERE id = ?";

        return jdbcTemplate.queryForObject(selectQuery, rowMapper, wishId);
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

    public List<Wish> showWishlistByUser(int userId) {
        String query = "SELECT w.id, w.name, w.link FROM Wish w JOIN wishlist wl ON w.id = wl.wish_id WHERE wl.user_id = ?";

        return jdbcTemplate.query(query, rowMapper, userId);
    }

    public Wish editWish(Wish wish){
        String updateQuery = "UPDATE Wish SET name = ?, link = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(updateQuery, wish.getName(), wish.getLink(), wish.getId());

        if (rowsAffected != 1) {
            throw  new RuntimeException("Could not update the wish");
        }

        return wish;
    }
}
