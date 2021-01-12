package com.company.service.impl;

import com.company.dto.*;
import com.company.service.UserRepository;
import com.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public MeetingDetailsDto getMeetingDetailsCode(String code) {
        loadCodeMeting(code);
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

    @Override
    public boolean joinThruCode(String code){
        boolean meetingExist = false;
        MeetingDto meetingDto = null;
        for (MeetingDto meeting:userRepository.getMeetings().getMeetingDtoList()
             ) {
            if(meeting.getCode().equals(code)){
                meetingDto = meeting;
                meetingExist = true;
            }
        }
        if (!meetingExist){
            return false;
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
                    "INSERT INTO debt.meeting_person VALUES("+ meetingDto.getId_meeting() +","+ userDto.getId_person() +",'member');");
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Insert person into meeting failed");
            return false;
        }
        System.out.println("Insert person into meeting done successfully");
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

    private void loadCodeMeting(String code){
        List<PersonMeetingDto> personMeetingDtos = new ArrayList<>();
        Integer meId = null;
        String nameMe = null;
        String codeMe = null;
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
                    " WHERE meeting.code LIKE '"+ code +"';");

            while ( rs.next() ) {
                Integer sqlMeId = rs.getInt("id_meeting");
                String sqlNameMe = rs.getString("meeting_name");
                String sqlCode = rs.getString("code");
                meId = sqlMeId;
                nameMe = sqlNameMe;
                codeMe = sqlCode;
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
        meetingDetailsDto = new MeetingDetailsDto(meId,nameMe,codeMe,personMeetingDtos);
        System.out.println("Users from meeting " + code +" downloaded successfully");
    }

    private Integer getFreePaymentId(){
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
            ResultSet rs = stmt.executeQuery( "SELECT MAX(id_payment)+1 AS \"free_id\" FROM debt.payment");
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

    @Override
    public boolean insertPayment(PaymentDto paymentDto){
        Connection c = null;
        Statement stmt  = null;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String currentDate = formatter.format(date);
        System.out.println(formatter.format(date));
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            stmt.executeUpdate(
                    "INSERT INTO debt.payment(id_payment,date,value,id_person,id_meeting) VALUES("+ getFreePaymentId() + ",'" + currentDate + "'," + paymentDto.getValue() +","
                            + userDto.getId_person() +"," + paymentDto.getId_meeting() +");");
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Insert Payment failed");
            return false;
        }
        System.out.println("Insert Payment done successfully");
        return true;
    }
    private Calendar createCalendar(String sqlDate){
        Calendar cal = Calendar.getInstance();
        int yy = Integer.parseInt(sqlDate.substring(0, 4));
        int MM = Integer.parseInt(sqlDate.substring(5,7));
        int dd = Integer.parseInt(sqlDate.substring(8, 10));
        int HH = Integer.parseInt(sqlDate.substring(11, 13));
        int mm = Integer.parseInt(sqlDate.substring(14, 16));
        int ss = Integer.parseInt(sqlDate.substring(17, 19));
        cal.set(Calendar.YEAR,yy);
        cal.set(Calendar.MONTH,MM-1);
        cal.set(Calendar.DAY_OF_MONTH,dd);
        cal.set(Calendar.HOUR_OF_DAY,HH);
        cal.set(Calendar.MINUTE,mm);
        cal.set(Calendar.SECOND,ss);
        return cal;
    }
    @Override
    public PaymentListDto getPayments(Integer idTable, Integer idPerson, String which){
        String query;
        if(which.equals("Person")){
            query = "SELECT * FROM debt.payment WHERE id_person = " + idPerson+";";
        }
        else{
            query = "SELECT * FROM debt.payment WHERE id_meeting = " + idTable+";";
        }
        PaymentListDto paymentListDto;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection c = null;
        Statement stmt  = null;
        List<PaymentGetDto> paymentGetDtos  = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while ( rs.next() ) {
                Integer sqlId = rs.getInt("id_payment");
                String sqlDate = rs.getString("date");
                String sqlVal = rs.getString("value");
                sqlVal = sqlVal.substring(0,sqlVal.length()-3);
                Double noSqlVal = Double.valueOf(sqlVal.replace(',','.'));
                Integer sqlIdPerson = rs.getInt("id_person");
                Integer sqlIdMeeting = rs.getInt("id_meeting");
                paymentGetDtos.add(new PaymentGetDto(sqlId,createCalendar(sqlDate),noSqlVal,sqlIdPerson,sqlIdMeeting));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        paymentListDto = new PaymentListDto(paymentGetDtos);
        System.out.println("Payments downloaded successfully");
        return  paymentListDto;
    }
}
