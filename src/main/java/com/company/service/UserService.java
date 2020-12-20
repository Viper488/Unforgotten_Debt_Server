package com.company.service;

import com.company.dto.LoginDto;
import com.company.dto.RegisterDto;
import com.company.dto.UserDto;
import com.company.dto.UserListDto;

public interface UserService {
    UserListDto getUsers();
    boolean logIn(LoginDto loginDto);
    UserDto getLoggedUser();
    boolean registerUser(RegisterDto registerDto);
}
