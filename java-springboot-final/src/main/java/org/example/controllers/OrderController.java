package org.example.controllers;

import org.example.models.Order;
import org.example.daos.OrderDao;
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
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
@PreAuthorize("hasAuthority('ADMIN')")
public class OrderController {
    /**
     * The order data access object.
     */
    @Autowired
    private OrderDao orderDao;

    /**
     * Gets all orders.
     *
     * @return A list of all orders.
     */
    @GetMapping
    public List<Order> getAll(@RequestParam(required = false) String username) {
        //could throw in check for username = admin here if making a real app
        if (username != null)
        {
            return orderDao.getOrdersByUsername(username);
        }
        return orderDao.getOrders();
    }

    /**
     * Gets a order by its id.
     *
     * @param name The id of the order.
     * @return The order with the given id.
     */
    @GetMapping(path = "/{id}")
    public Order get(@PathVariable int id) {
        Order order = orderDao.getOrderById(id);
        if (order == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        }
        return order;
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return The created order.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("permitAll()")
    public Order create(@RequestBody Order order, Principal user) {
        order.setUsername(user.getName());
        return orderDao.createOrder(order);
    }

    /**
     * Updates a order
     *
     * @param order The order to update
     * @return The updated order.
     */
    @PutMapping(path = "/{id}")
    public Order update(@PathVariable int id, @RequestBody Order order) {
        Order order = orderDao.getOrderById(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        order.setId(id);
        return orderDao.updateOrder(order);
    }

    /**
     * Deletes an order.
     *
     * @param id The order to delete.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        Order order = orderDao.getOrderById(id);
        if (order == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return orderDao.deleteOrder(id);
    }


}
