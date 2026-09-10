package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Product;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for users.
 */
@Component
public class ProductDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new user data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all users.
     *
     * @return List of Product
     */
    public List<Product> getProducts() {
        return jdbcTemplate.query("SELECT * FROM products ORDER BY id;", this::mapToProduct);
    }

    /**
     * Gets a product by ID.
     *
     * @param id the ID of the product
     * @return Product
     */
    public Product getProductById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new product.
     * @param product The product to create.
     * @return Product The created product.
     */
    public Product createProduct(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?,?);";
        jdbcTemplate.update(sql, product.getName(), product.getPrice());
        int newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        return getProductById(newId);
    }

    /**
     * Updates a product.
     *
     * @param product The product to update.
     * @return Product
     */
    public Product updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getProductById(product.getId());
        }
    }

    /**
     * Deletes a product
     *
     * @param product The product to be deleted
     */
    public int deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ? ";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Maps a row in the ResultSet to a Product object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Product The product object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private User mapToProduct(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price")
        );
    }
}
