package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser_happyPath() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testPassword");
        req.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }
    @Test
    public void createUser_passwordMismatch() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testPassword");
        req.setConfirmPassword("testPasswordMismatch");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    public void createUser_invalidPassword() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("pw");
        req.setConfirmPassword("pw");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void getUserByName() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(user.getId(), responseBody.getId());
        assertEquals(user.getUsername(), responseBody.getUsername());
    }

    @Test
    public void getUserByID() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(user.getId(), responseBody.getId());
        assertEquals(user.getUsername(), responseBody.getUsername());
    }
}
