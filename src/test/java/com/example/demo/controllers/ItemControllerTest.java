package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private Item item;


    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setPrice(new BigDecimal("1.99"));
        item.setDescription("just for test");
    }

    @Test
    public void getAllItems() throws Exception {
        List<Item> allItems = new ArrayList<>();
        allItems.add(item);

        when(itemRepository.findAll()).thenReturn(allItems);

        final ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    public void getItemById() throws Exception {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));

        final ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item resItem = response.getBody();
        assertNotNull(resItem);
        assertEquals(item.getId(), resItem.getId());
        assertEquals(item.getName(), resItem.getName());
        assertEquals(item.getDescription(), resItem.getDescription());
        assertEquals(item.getPrice(), resItem.getPrice());
    }

    @Test
    public void getItemByName_happyPath() throws Exception {
        List<Item> allItems = new ArrayList<>();
        allItems.add(item);

        when(itemRepository.findByName("test item")).thenReturn(allItems);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("test item");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> resItems = response.getBody();
        assertNotNull(resItems);

        assertEquals(1, resItems.size());

        Item resItem = resItems.get(0);

        assertEquals(item.getId(), resItem.getId());
        assertEquals(item.getName(), resItem.getName());
        assertEquals(item.getDescription(), resItem.getDescription());
        assertEquals(item.getPrice(), resItem.getPrice());
    }

    @Test
    public void getItemByName_missingName() throws Exception {
        when(itemRepository.findByName(null)).thenReturn(null);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(null);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}