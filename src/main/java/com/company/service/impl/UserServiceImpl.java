package com.company.service.impl;

import com.company.dto.LoginDto;
import com.company.dto.RegisterDto;
import com.company.dto.UserDto;
import com.company.dto.UserListDto;
import com.company.service.UserRepository;
import com.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private UserDto userDto;

    public UserServiceImpl() {

    }
    @Override
    public UserListDto getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public boolean logIn(LoginDto loginDto) {
        userDto = userRepository.findUser(loginDto.getEmail(), loginDto.getPassword());
        return userDto != null;
    }
    @Override
    public UserDto getLoggedUser() {
        return userDto;
    }

    @Override
    public boolean registerUser(RegisterDto registerDto) {
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM debt.person");
            while ( rs.next() ) {
                Integer sqlId = rs.getInt("id_person");
                String sqlNick = rs.getString("nick");
                String sqlName = rs.getString("name");
                String sqlSurname = rs.getString("surname");
                String sqlEmail = rs.getString("email");
                String sqlPassword = rs.getString("password");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return false;
    }

    private Integer getFreeId(){
        Connection c = null;
        Statement stmt  = null;
        Integer freeId = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT MAX(id_person)+1 AS \"free_id\" FROM debt.person");
            while ( rs.next() ) {
                Integer sqlId = rs.getInt("free_id");
                freeId = sqlId;
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        System.out.println("Free id: " + freeId);
        return freeId;
    }
}
