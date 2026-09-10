package org.example.controllers;

import org.example.models.OrderItem;
import org.example.daos.OrderItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for orders.
 * This class is responsible for handling all HTTP requests related to orders.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/order-items")
@PreAuthorize("isAuthenticated()")

public class OrderItemController {
    /**
     * The order data access object.
     */
    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Gets all order items.
     *
     * @return A list of all order items.
     */
    @GetMapping
    public List<OrderItem> getAll(@RequestParam(required = false) Integer orderId) {
        if (orderId != null)
        {
            return orderItemDao.getOrderItemsByOrderId(orderId);
        }
        return orderItemDao.getOrderItems();
    }

    /**
     * Gets an order item by its id.
     *
     * @param name The id of the orderItem.
     * @return The order with the given id.
     */
    @GetMapping(path = "/{id}")
    public OrderItem get(@PathVariable int id) {
        OrderItem orderitem = orderItemDao.getOrderItemByOrderId(id);
        if (orderItem == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        return orderItem;
    }

    /**
     * Creates a new order item.
     *
     * @param orderItem The item to create.
     * @return The created order item.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.createOrderItem(orderItem);
    }

    /**
     * Updates an order item
     *
     * @param order The order item to update
     * @return The updated order item.
     */
    @PutMapping(path = "/{id}")
    public OrderItem update(@PathVariable int id, @RequestBody OrderItem orderItem) {
        if (orderItemDao.getOrderItemByOrderId(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        orderItem.setId(id);
        return orderItemDao.updateOrderItem(orderItem);
    }

    /**
     * Deletes an order item.
     *
     * @param id The order item to delete.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        OrderItem orderItem = orderItemDao.getOrderItemByOrderId(id);
        if (orderItem == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        return orderItemDao.deleteOrderItem(id);
    }


}
