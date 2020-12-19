package com.company.domain;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Repository
public class AccountData implements Serializable {
    private String nick;
    private String password;


    public AccountData(){
        this.nick=null;
        this.password=null;
    }

    //konstruktor nowego elementu
    public AccountData(String nick, String password) {
        this.nick = nick;
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void findUser(String appNick){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("postgres://ezstzdga:o6zkA0o8vitrVts8Z37XcUEY1v9Z61rw@rogue.db.elephantsql.com:5432/ezstzdga",
                            "ezstzdga", "o6zkA0o8vitrVts8Z37XcUEY1v9Z61rw");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM public.users u where u.nick LIKE '" + appNick +"'" );
            while ( rs.next() ) {
                String sqlNick = rs.getString("nick");
                String  sqlpassword = rs.getString("password");
                String age  = rs.getString("question");
                String  address = rs.getString("answer");
                System.out.println( "ID = " + sqlNick );
                System.out.println( "NAME = " + sqlpassword );
                System.out.println( "AGE = " + age );
                System.out.println( "ADDRESS = " + address );
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }

    @Override
    public String toString() {
        return "AccountData{" +
                "nick='" + nick + '\'' +
                ", password='" + password + '\'' +

                '}';
    }
}

