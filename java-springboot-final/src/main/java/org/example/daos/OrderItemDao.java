package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.OrderItem;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for order items.
 */
@Component
public class OrderItemDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new orderItem data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all order items.
     *
     * @return List of OrderItems
     */
    public List<OrderItem> getOrderItems() {
        return jdbcTemplate.query("SELECT * FROM order_items ORDER BY id;", this::mapToOrderItem);
    }

    /**
     * Gets an order item by ID.
     *
     * @param id the ID of the order item
     * @return OrderItem
     */
    public OrderItem getOrderItemById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM order_items WHERE id = ?", this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new item.
     * @param orderItem The item to create.
     * @return OrderItem The created order item.
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?,?,?);";
        jdbcTemplate.update(sql, orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity());
        int newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        return getOrderItemById(newId);
    }

    /**
     * Updates an order item.
     *
     * @param orderItem The order item to update.
     * @return OrderItem
     */
    public OrderItem updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity(), orderItem.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getOrderItemById(orderItem.getId());
        }
    }

    /**
     * Deletes an order item
     *
     * @param orderItem The orderItem to be deleted
     */
    public int deleteOrderItem(int id) {
        String sql = "DELETE FROM order_items WHERE id = ? ";
        return jdbcTemplate.update(sql, id);
    }


    /**
     * Gets all orders for the order Id.
     *
     * @param username The ID to get the items for.
     * @return List of OrderItems
     */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return jdbcTemplate.query("SELECT * FROM order_items WHERE order_id = ? ORDER BY id;", this::mapToOrderItem, orderId);
    }

    /**
     * Maps a row in the ResultSet to a OrderItem object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return OrderItem The orderItem object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private User mapToOrderItem(ResultSet resultSet, int rowNumber) throws SQLException {
        return new OrderItem(
                resultSet.getInt("id"),
                resultSet.getInt("order_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("quantity")
        );
    }
}
