package com.company.service;

import com.company.dto.UserDto;
import com.company.dto.UserListDto;

public interface UserRepository {
    UserListDto getUsers();
    void addUser(UserDto user);
    void initUsers();
    UserDto findUser(String email, String password);

}
