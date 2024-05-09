package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);


    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

    }

    @Test
    public void submitOrder_happyPath() throws Exception {
        //create user.
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        //create cart fort user
        Cart cart = new Cart();

        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setDescription("for test");
        item.setName("testItem");
        item.setPrice(new BigDecimal("1.99"));
        items.add(item);
        cart.setItems(items);
        cart.setUser(user);
        cart.setTotal(new BigDecimal("9.99"));
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(user);
        assertEquals(items, userOrder.getItems());
        assertEquals("test", userOrder.getUser().getUsername());
        assertEquals(new BigDecimal("9.99"), userOrder.getTotal());
    }

    @Test
    public void submitOrder_missingUsername() throws Exception {
        when(userRepository.findByUsername(null)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(null);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());


    }

    @Test
    public void getOrderHistory_happyPath() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getOrderHistory_missingUsername() throws Exception {
        when(userRepository.findByUsername(null)).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(null);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

}