package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.Wish;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
        wish.setReserved(rs.getBoolean("isReserved"));
        return wish;
    };

    private final JdbcTemplate jdbcTemplate;

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Wish getWish(Integer wishId){
        String query = "SElECT * FROM Wish WHERE id = ?";

        return jdbcTemplate.queryForObject(query, rowMapper, wishId);
    }

    public Wish insertWish(Wish wish) {
        String query = "INSERT INTO wish (name, link, isReserved) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try{
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, wish.getName());
                ps.setString(2, wish.getLink());
                ps.setBoolean(3, wish.isReserved());
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

    public int insertToWishlistJunction(int wishId, int userId) {
        String query = "INSERT INTO wishlist (user_id, wish_id) VALUES (?, ?)";

        int rowsAffected = jdbcTemplate.update(query, userId, wishId);

        if (rowsAffected != 1) {
            throw new RuntimeException("Couldn't insert into 'wishlist' junction table!");
        }

        return rowsAffected;
    }

    public int insertToReservationJunction(int wishId, int userId) {
        String query = "INSERT INTO reservation (user_id, wish_id) VALUES (?, ?)";

        int rowsAffected = jdbcTemplate.update(query, userId, wishId);

        if (rowsAffected != 1) {
            throw new RuntimeException("Couldn't insert into 'reservation' junction table!");
        }

        return rowsAffected;
    }

    public List<Wish> getWishlistByUser(int userId) {
        String query = "SELECT w.id, w.name, w.link, w.isReserved FROM Wish w JOIN wishlist wl ON w.id = wl.wish_id WHERE wl.user_id = ?";

        return jdbcTemplate.query(query, rowMapper, userId);
    }

    public List<Integer> getReservationListByUserId(int userId) {
        String query = "SELECT wish_id FROM reservation WHERE user_id = ?";

        return jdbcTemplate.queryForList(query, Integer.class, userId);
    }

    public Wish editWish(Wish wish){
        String query = "UPDATE Wish SET name = ?, link = ?, isReserved = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(query, wish.getName(), wish.getLink(), wish.isReserved(), wish.getId());

        if (rowsAffected != 1) {
            throw  new RuntimeException("Could not update the wish");
        }

        return wish;
    }

    public int deleteWishReservation(int wishId) {
        String query = "DELETE FROM reservation WHERE wish_id = ?";

        return jdbcTemplate.update(query, wishId);
    }

    public int deleteWish(Wish wish) {
        String query = "DELETE FROM wish WHERE id = ?";

        return jdbcTemplate.update(query, wish.getId());
    }
}
