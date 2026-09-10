package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for orders.
 */
@Component
public class OrderDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new order data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all orders.
     *
     * @return List of Orders
     */
    public List<Order> getOrders() {
        return jdbcTemplate.query("SELECT * FROM orders ORDER BY id;", this::mapToOrder);
    }

    /**
     * Gets an order by ID.
     *
     * @param id the ID of the order
     * @return Order
     */
    public Order getOrderById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", this::mapToOrder, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order.
     * @param order The order to create.
     * @return Order The created order.
     */
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (username) VALUES (?);";
        jdbcTemplate.update(sql, order.getUsername());
        int newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        return getOrderById(newId);
    }

    /**
     * Updates a order.
     *
     * @param order The order to update.
     * @return Order
     */
    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, order.getUsername(), order.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getOrderById(order.getId());
        }
    }

    /**
     * Deletes an order
     *
     * @param order The order to be deleted
     */
    public int deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ? ";
        return jdbcTemplate.update(sql, id);
    }


    /**
     * Gets all orders for the username.
     *
     * @param username The username to get the orders for.
     * @return List of Orders
     */
    public List<Order> getOrdersByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE username = ? ORDER BY id;", this::mapToOrder, username);
    }

    /**
     * Maps a row in the ResultSet to a Order object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Order The order object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private User mapToOrder(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Order(
                resultSet.getInt("id"),
                resultSet.getString("username")
        );
    }
}
