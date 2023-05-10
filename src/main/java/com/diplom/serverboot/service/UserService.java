package com.diplom.serverboot.service;

import com.diplom.serverboot.dao.UserDao;
import com.diplom.serverboot.dto.AuthMessage;
import com.diplom.serverboot.entity.User;
import com.diplom.serverboot.exception.BadCreditionalsException;
import com.diplom.serverboot.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    private UserDao userDao;

    public User authenticateUserByCreditionals(String identifier, String password) throws UserNotFoundException, BadCreditionalsException {
        User user = userDao.getUserByIdentifier(identifier);
        if (user.getPassword().equals(password)) {
            return user;
        }
        else {
            throw new BadCreditionalsException();
        }
    }

    public User authenticateUserByAuthToken(String authToken) throws UserNotFoundException {
        return userDao.getUserByAuthToken(authToken);
    }
}
