package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private User testUser;
    private Cart reqCart;
    private Item item;

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);


        //create user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test name");
        //create cart for user
        reqCart = new Cart();
        //create item for cart
        List<Item> items = new ArrayList<>();

        item = new Item();
        item.setId(1L);
        item.setDescription("for test");
        item.setName("test item");
        item.setPrice(new BigDecimal("1.99"));
        items.add(item);

        reqCart.setItems(items);
        reqCart.setUser(testUser);
        reqCart.setTotal(new BigDecimal("9.99"));
        testUser.setCart(reqCart);
    }

    @Test
    public void addToCart_happyPath() throws Exception {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("test name");
        req.setItemId(1);
        req.setQuantity(1);


        when(userRepository.findByUsername("test name")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));

        final ResponseEntity<Cart> response = cartController.addTocart(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertEquals(2, Objects.requireNonNull(response.getBody()).getItems().size());

    }

    @Test
    public void removeFromCart_happyPath() throws Exception {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("test name");
        req.setItemId(1);
        req.setQuantity(1);

        when(userRepository.findByUsername("test name")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));

        final ResponseEntity<Cart> response = cartController.removeFromcart(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertEquals(0, Objects.requireNonNull(response.getBody()).getItems().size());
    }

    @Test
    public void getCart_happyPath() throws Exception {

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));


        final ResponseEntity<Cart> response = cartController.getCart(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

    }

}
