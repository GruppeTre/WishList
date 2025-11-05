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
        return wish;
    };

    private final JdbcTemplate jdbcTemplate;

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //Get wish from database
    public Wish getWish(Integer wishId){
        String query = "SElECT * FROM Wish WHERE id = ?";

        return jdbcTemplate.queryForObject(query, rowMapper, wishId);
    }

    //Inserts wish into database
    public Wish insertWish(Wish wish) {
        String query = "INSERT IGNORE INTO wish (name, link) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //Uses a prepared statement and maps ids for keyholder
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

        //Fails if id is not set
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new wish");
        }

        wish.setId(keyHolder.getKey().intValue());

        return wish;
    }

    //Insert wish into wishlist junction list
    public int insertToWishlistJunction(int wishId, int userId) {
        String query = "INSERT INTO wishlist (user_id, wish_id) VALUES (?, ?)";

        int rowsAffected = jdbcTemplate.update(query, userId, wishId);

        //Checks if no rows were affected
        if (rowsAffected != 1) {
            throw new RuntimeException("Couldn't insert into 'wishlist' junction table!");
        }

        return rowsAffected;
    }

    //Inserts wish into reservation junction
    public int insertToReservationJunction(int wishId, int userId) {
        String query = "INSERT INTO reservation (user_id, wish_id) VALUES (?, ?)";

        int rowsAffected = jdbcTemplate.update(query, userId, wishId);

        //Checks if no rows were affected
        if (rowsAffected != 1) {
            throw new RuntimeException("Couldn't insert into 'reservation' junction table!");
        }

        return rowsAffected;
    }

    //Gets wishlist by user id
    public List<Wish> getWishlistByUser(int userId) {
        String query = "SELECT w.id, w.name, w.link FROM Wish w JOIN wishlist wl ON w.id = wl.wish_id WHERE wl.user_id = ?";

        return jdbcTemplate.query(query, rowMapper, userId);
    }

    //Gets reservation list by user id
    public List<Integer> getReservationListByUserId(int userId) {
        String query = "SELECT wish_id FROM reservation WHERE user_id = ?";

        return jdbcTemplate.queryForList(query, Integer.class, userId);
    }

    public boolean isReserved(Wish wish) {
        String query = "SELECT COUNT(wish_id) FROM reservation WHERE wish_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, wish.getId());
        return count != null && count > 0;
    }

    public List<Integer> getReservedWishes(int userId) {
        String query = """
                        SELECT r.wish_id
                        FROM reservation r
                        JOIN wishlist wl ON r.wish_id = wl.wish_id
                        WHERE wl.user_id = ?;
                        """;

        return jdbcTemplate.queryForList(query, Integer.class, userId);
    }


    //Updates wish by wish id
    public Wish editWish(Wish wish){

        String query = "UPDATE Wish SET name = ?, link = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(query, wish.getName(), wish.getLink(), wish.getId());

        //Checks if rows were affected
        if (rowsAffected != 1) {
            throw  new RuntimeException("Could not update the wish");
        }

        return wish;
    }

    //Deletes wish from reservation list by wish id
    public int deleteWishReservation(int wishId) {
        String query = "DELETE FROM reservation WHERE wish_id = ?";

        return jdbcTemplate.update(query, wishId);
    }

    //Deletes wish from wish table by id
    public int deleteWish(Wish wish) {
        String query = "DELETE FROM wish WHERE id = ?";

        return jdbcTemplate.update(query, wish.getId());
    }
}
