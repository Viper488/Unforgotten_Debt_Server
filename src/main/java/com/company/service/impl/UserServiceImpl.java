package com.company.service.impl;

import com.company.dto.*;
import com.company.service.UserRepository;
import com.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private UserDto userDto;
    private MeetingDetailsDto meetingDetailsDto;



    public UserServiceImpl() {
    }
    @Override
    public UserListDto getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public MeetingListDto getMeetings() {
        return userRepository.getMeetings();
    }
    @Override
    public MeetingDetailsDto getPersonMeetingListDto(Integer id_meeting) {
        loadPeopleMeting(id_meeting);
        return meetingDetailsDto;
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
        for (UserDto checkUser:userRepository.getUsers().getUsers()
             ) {
            if(checkUser.getEmail().equals(registerDto.getEmail())){
                System.out.println("Person with this email already exists");
                return false;
            }
        }
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            stmt.executeUpdate(
                    "INSERT INTO debt.person(id_person,nick,name,surname,email,password) VALUES("+ getFreeId() + ",'" + registerDto.getNick() + "','" + registerDto.getName() +"','"
                            + registerDto.getSurname() +"','" + registerDto.getEmail()  +"','"+ registerDto.getPassword()+"');");
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Insert Person failed");
            return false;
        }
        System.out.println("Insert Person done successfully");
        userRepository.initUsers();
        return true;
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
        System.out.println("Free id: " + freeId);
        return freeId;
    }

    private void loadPeopleMeting(Integer id_meeting){
        List<PersonMeetingDto> personMeetingDtos = new ArrayList<>();
        Integer meId = null;
        String nameMe = null;
        String code = null;
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT meeting.id_meeting,meeting.name AS \"meeting_name\", meeting.code, pe.id_person,pe.nick,pe.name,pe.surname,pe.email,mp.user_type FROM debt.person AS pe" +
                    " INNER JOIN debt.meeting_person AS mp ON pe.id_person = mp.id_person" +
                    " INNER JOIN debt.meeting ON meeting.id_meeting = mp.id_meeting" +
                    " WHERE meeting.id_meeting = "+ id_meeting);

            while ( rs.next() ) {
                Integer sqlMeId = rs.getInt("id_meeting");
                String sqlNameMe = rs.getString("meeting_name");
                String sqlCode = rs.getString("code");
                meId = sqlMeId;
                nameMe = sqlNameMe;
                code = sqlCode;
                Integer sqlPeId = rs.getInt("id_person");
                String sqlNick = rs.getString("nick");
                String sqlName = rs.getString("name");
                String sqlSurname = rs.getString("surname");
                String sqlEmail = rs.getString("email");
                String sqlType = rs.getString("user_type");
                personMeetingDtos.add(new PersonMeetingDto(sqlPeId,sqlNick,sqlName,sqlSurname,sqlEmail,sqlType));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        meetingDetailsDto = new MeetingDetailsDto(meId,nameMe,code,personMeetingDtos);
        System.out.println("Users from meeting " + id_meeting +" downloaded successfully");
    }
}
