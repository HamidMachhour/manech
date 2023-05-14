package com.manech.manechapp.repository;

import com.manech.manechapp.dao.AddressDAO;
import com.manech.manechapp.dao.UserDAO;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.User;

import java.util.List;
import java.util.UUID;

public class UserRepository {
    private static UserRepository instance;
    private final UserDAO userDAO;

    private UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserRepository getInstance(UserDAO userDAO) {
        if (instance == null) {
            instance = new UserRepository(userDAO);
        }
        return instance;
    }

    public boolean insertUser(User user) {
       return (userDAO.insert(user) > 0);
    }


    public User userAuth(String email, String password) {
        return userDAO.userAuth(email, password);
    }

    public User getUserByEmail(String email) {
        return userDAO.userByEmail(email);
    }

    public User userById(UUID userId) {
        return userDAO.userById(userId);
    }

    public void disableAccount(UUID userId) {
        userDAO.storesToTrash(userId);
        userDAO.disableUser(userId);
    }
    public int updateUser(User user) {
        return userDAO.updateUser(user);
    }

}
