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
        userRepository.initUsers();
        userRepository.initMeetings();
        return userRepository.getUsers();
    }

    @Override
    public MeetingListDto getMeetings() {
        userRepository.initUsers();
        userRepository.initMeetings();
        return userRepository.getMeetings();
    }
    @Override
    public MeetingDetailsDto getPersonMeetingListDto(Integer id_meeting) {
        userRepository.initUsers();
        userRepository.initMeetings();
        loadPeopleMeting(id_meeting);
        return meetingDetailsDto;
    }
    @Override
    public MeetingDetailsDto getMeetingDetailsCode(String code) {
        userRepository.initUsers();
        userRepository.initMeetings();
        loadCodeMeting(code);
        return meetingDetailsDto;
    }

    @Override
    public MeetingListDto getPersonMeetings(Integer id_person) {
        userRepository.initUsers();
        userRepository.initMeetings();
        MeetingListDto meetingListDto;
        List<MeetingDto> list = new ArrayList<>();
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT m.id_meeting, name, code, password FROM debt.meeting AS m "
                    + "INNER JOIN debt.meeting_person AS mp ON mp.id_meeting = m.id_meeting "+
                    "WHERE mp.id_person = "+ id_person +';');

            while ( rs.next() ) {
                Integer sqlId = rs.getInt("id_meeting");
                String sqlName = rs.getString("name");
                String sqlCode = rs.getString("code");
                String sqlPassword = rs.getString("password");
                list.add(new MeetingDto(sqlId,sqlName,sqlCode,sqlPassword));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        meetingListDto = new MeetingListDto(list);
        System.out.println("Person" + id_person + " meetings downloaded successfully");
        return meetingListDto;
    }
    @Override
    public boolean logIn(LoginDto loginDto) {
        userRepository.initUsers();
        userRepository.initMeetings();
        userDto = userRepository.findUser(loginDto.getEmail(), loginDto.getPassword());
        return userDto != null;
    }
    @Override
    public UserDto getLoggedUser() {
        return userDto;
    }

    @Override
    public boolean registerUser(RegisterDto registerDto) {
        userRepository.initUsers();
        userRepository.initMeetings();

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
            System.out.println("Insert Person done successfully");
            userRepository.initUsers();
            return true;

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Insert Person failed");
            return false;
        }

    }

    private boolean checkIfPerson(Integer id_meeting, Integer id_person){
        Connection c = null;
        Statement stmt  = null;
        Integer meetId = null;
        Integer perId = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT id_meeting, id_person FROM debt.meeting_person WHERE id_meeting = "+ id_meeting + " AND id_person = " + id_person+";");
            while ( rs.next() ) {
                meetId = rs.getInt("id_meeting");
                perId  = rs.getInt("id_person");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        if(meetId == null){
            return false;
        }
        if(meetId.equals(id_meeting) && perId.equals(id_person)){
            return true;
        }
        return false;
    }
    @Override
    public boolean joinThruCode(String code, String password, String memberType){
        userRepository.initUsers();
        userRepository.initMeetings();

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
        else if(!meetingDto.getPassword().equals(password)){
            return false;
        }
        if(checkIfPerson(meetingDto.getId_meeting(), getLoggedUser().getId_person())){
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
                    "INSERT INTO debt.meeting_person VALUES("+ meetingDto.getId_meeting() +","+ getLoggedUser().getId_person() +",'"+ memberType +"');");
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
    @Override
    public boolean addToMeeting(Integer id_meeting, String nick){
        userRepository.initUsers();
        userRepository.initMeetings();
        UserListDto users = userRepository.getUsers();
        MeetingListDto meetingListDto = userRepository.getMeetings();
        Integer id_added_person = null;
        boolean flag = false;
        //person exists check
        for (UserDto user:users.getUsers()
             ) {
            if(user.getNick().equals(nick)){
                id_added_person = user.getId_person();
            }
        }
        if(id_added_person == null){
            return false;
        }
        //meeting exists check
        for (MeetingDto meeting:meetingListDto.getMeetingDtoList()
             ) {
            if(meeting.getId_meeting() == id_meeting){
                flag  = true;
            }
        }
        if(!flag){
            return false;
        }
        if(checkIfPerson(id_meeting, id_added_person)){
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
                    "INSERT INTO debt.meeting_person VALUES("+ id_meeting +","+ id_added_person +",'member');");
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
        String password = null;
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT meeting.id_meeting,meeting.name AS \"meeting_name\", meeting.code, meeting.password, pe.id_person,pe.nick,pe.name,pe.surname,pe.email,mp.user_type FROM debt.person AS pe" +
                    " INNER JOIN debt.meeting_person AS mp ON pe.id_person = mp.id_person" +
                    " INNER JOIN debt.meeting ON meeting.id_meeting = mp.id_meeting" +
                    " WHERE meeting.id_meeting = "+ id_meeting);

            while ( rs.next() ) {
                Integer sqlMeId = rs.getInt("id_meeting");
                String sqlNameMe = rs.getString("meeting_name");
                String sqlCode = rs.getString("code");
                String sqlPassword = rs.getString("password");
                meId = sqlMeId;
                nameMe = sqlNameMe;
                code = sqlCode;
                password = sqlPassword;
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
        meetingDetailsDto = new MeetingDetailsDto(meId,nameMe,code,password,personMeetingDtos);
        System.out.println("Users from meeting " + id_meeting +" downloaded successfully");
    }

    private void loadCodeMeting(String code){
        List<PersonMeetingDto> personMeetingDtos = new ArrayList<>();
        Integer meId = null;
        String nameMe = null;
        String codeMe = null;
        String passwordMe = null;
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT meeting.id_meeting,meeting.name AS \"meeting_name\", meeting.code, meeting.password, pe.id_person,pe.nick,pe.name,pe.surname,pe.email,mp.user_type FROM debt.person AS pe" +
                    " INNER JOIN debt.meeting_person AS mp ON pe.id_person = mp.id_person" +
                    " INNER JOIN debt.meeting ON meeting.id_meeting = mp.id_meeting" +
                    " WHERE meeting.code LIKE '"+ code +"';");

            while ( rs.next() ) {
                Integer sqlMeId = rs.getInt("id_meeting");
                String sqlNameMe = rs.getString("meeting_name");
                String sqlCode = rs.getString("code");
                String sqlPassword = rs.getString("password");
                meId = sqlMeId;
                nameMe = sqlNameMe;
                codeMe = sqlCode;
                passwordMe = sqlPassword;
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
        meetingDetailsDto = new MeetingDetailsDto(meId,nameMe,codeMe,passwordMe,personMeetingDtos);
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
    private Integer getFreeMeetingId(){
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
            ResultSet rs = stmt.executeQuery( "SELECT MAX(id_meeting)+1 AS \"free_id\" FROM debt.meeting");
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
    private Integer getFreeProductId(){
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
            ResultSet rs = stmt.executeQuery( "SELECT MAX(id_product)+1 AS \"free_id\" FROM debt.products");
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
        userRepository.initUsers();
        userRepository.initMeetings();
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
                    "INSERT INTO debt.payment(id_payment, date, time, value,id_person,id_meeting) VALUES("+ getFreePaymentId() + ",'" + currentDate.substring(0,10) + "','" + currentDate.substring(11,19) + "'," + paymentDto.getValue() +","
                            + paymentDto.getId_person() +"," + paymentDto.getId_meeting() +");");
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
        userRepository.initUsers();
        userRepository.initMeetings();
        String query;
        if(which.equals("Person")){
            query = "SELECT id_payment, value, (SELECT nick FROM debt.person AS pe WHERE pe.id_person = pa.id_person), id_meeting, date, time FROM debt.payment AS pa WHERE id_person = " + idPerson+";";
        }
        else{
            query = "SELECT id_payment, value, (SELECT nick FROM debt.person AS pe WHERE pe.id_person = pa.id_person), id_meeting, date, time FROM debt.payment AS pa WHERE id_meeting = " + idTable+";";
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
                String sqlTime = rs.getString("time");
                String sqlVal = rs.getString("value");
                sqlVal = sqlVal.substring(0,sqlVal.length()-3);
                Double noSqlVal = Double.valueOf(sqlVal.replace(',','.'));
                String sqlNick = rs.getString("nick");
                Integer sqlIdMeeting = rs.getInt("id_meeting");
                paymentGetDtos.add(new PaymentGetDto(sqlId,sqlDate,sqlTime,noSqlVal,sqlNick,sqlIdMeeting));
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

    @Override
    public Double getSumPayments(Integer idTable, Integer idPerson, String which){
        userRepository.initUsers();
        userRepository.initMeetings();
        String query;
        if(which.equals("Person")){
            query = "SELECT SUM(value) FROM debt.payment AS pa WHERE id_person = " + idPerson+";";
        }
        else{
            query = "SELECT SUM(value) FROM debt.payment AS pa WHERE id_meeting = " + idTable+";";
        }
        Double sumPayments = 0.0;
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
            ResultSet rs = stmt.executeQuery(query);
            while ( rs.next() ) {
                String sqlVal = rs.getString("sum");
                if(sqlVal == null){
                    return sumPayments;
                }
                sqlVal = sqlVal.substring(0,sqlVal.length()-3);
                sumPayments = Double.valueOf(sqlVal.replace(',','.'));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Payments Sum downloaded successfully");
        return  sumPayments;
    }
    private String randomCode(){
        String numbers = "0123456789";
        char[] chars = numbers.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i =0; i<6;i++){
            stringBuilder.append(chars[(int) (Math.random() * chars.length)]);
        }
        return stringBuilder.toString();
    }
    @Override
    public boolean createMeeting(String name, String password){
        userRepository.initUsers();
        userRepository.initMeetings();
        Connection c = null;
        Statement stmt  = null;
        String code = null;
        MeetingListDto meetingListDto = userRepository.getMeetings();
        code = randomCode();
        for (MeetingDto meeting:meetingListDto.getMeetingDtoList()
             ) {
            if(code.equals(meeting.getCode())){
                code = randomCode();
            }
        }
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            stmt.executeUpdate(
                    "INSERT INTO debt.meeting(id_meeting, name, code, password) VALUES("+ getFreeMeetingId() + ",'" + name + "','" + code + "','" + password +"');");
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Insert Meeting failed");
            return false;
        }
        System.out.println("Insert Meeting done successfully");
        userRepository.initMeetings();
        return joinThruCode(code,password,"owner");
    };
    @Override
    public ProductListDto getProducts(Integer id_meeting){
        userRepository.initUsers();
        userRepository.initMeetings();

        List<ProductDto> list = new ArrayList<>();
        ProductListDto productListDto;
        Connection c = null;
        Statement stmt  = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://195.150.230.210:5434/2020_hamernik_artur",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT id_product, pr.name, price, nick, id_meeting, date, time FROM debt.products AS pr " +
                    "INNER JOIN debt.person AS pe ON pe.id_person = pr.id_person " +
                    "WHERE id_meeting = "+ id_meeting+';');

            while ( rs.next() ) {
                Integer sqlId = rs.getInt("id_product");
                String sqlName = rs.getString("name");
                String sqlVal = rs.getString("price");
                sqlVal = sqlVal.substring(0,sqlVal.length()-3);
                Double noSqlVal = Double.valueOf(sqlVal.replace(',','.'));
                String sqlNick = rs.getString("nick");
                Integer sqlMeId = rs.getInt("id_meeting");
                String sqlDate = rs.getString("date");
                String sqlTime = rs.getString("time");
                list.add(new ProductDto(sqlId,sqlName,noSqlVal,sqlNick,sqlDate,sqlTime,sqlMeId));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        productListDto = new ProductListDto(list);
        System.out.println("Products from meeting " + id_meeting +" downloaded successfully");

        return productListDto;
    };
    @Override
    public boolean insertProduct(String name, Double price, Integer id_person, Integer id_meeting){
        userRepository.initUsers();
        userRepository.initMeetings();

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
                    "INSERT INTO debt.products(id_product, name, price, id_person, id_meeting, date, time) VALUES("+ getFreeProductId() + ",'" + name + "'," + price +","
                            + id_person +"," + id_meeting +",'"+ currentDate.substring(0,10) +"','"+ currentDate.substring(11,19) +"');");
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Insert Product failed");
            return false;
        }
        System.out.println("Insert Product done successfully");
        return true;
    };

    @Override
    public boolean deleteProduct(Integer id_product){
        userRepository.initUsers();
        userRepository.initMeetings();

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
                    "DELETE FROM debt.products WHERE id_product = " + id_product +";");
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
            System.out.println("Delete Product failed");
            return false;
        }
        System.out.println("Delete Product done successfully");
        return true;
    };
}
