package org.example.controllers;

import org.example.models.Product;
import org.example.daos.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for products.
 * This class is responsible for handling all HTTP requests related to products.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/products")
@PreAuthorize("hasAuthority('ADMIN')")
@PreAuthorize("isAuthenticated()")
public class ProductController {
    /**
     * The product data access object.
     */
    @Autowired
    private ProductDao productDao;

    /**
     * Gets all products.
     *
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAll() {
        return productDao.getProducts();
    }

    /**
     * Gets a product by its id.
     *
     * @param name The id of the product.
     * @return The product with the given id.
     */
    @GetMapping(path = "/{id}")
    public Product get(@PathVariable int id) {
        return productDao.getProductById(id);
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("permitAll()")
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }

    /**
     * Updates a product
     *
     * @param product The product to update
     * @return The updated product.
     */
    @PutMapping(path = "/{id}")
    public Product update(@RequestBody Product product) {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return productDao.updateProduct(product);
    }

    /**
     * Deletes a product.
     *
     * @param id The product to delete.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return productDao.deleteProduct(id);
    }


}
