package com.company.domain;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


@Repository
public class UserBase {
    private ArrayList<AccountData> platformBase;

    public UserBase() {
        platformBase = new ArrayList<>();

        // ##################################### tutaj mozna dodac z palca uzytkownikow
        downloadBase();  // ---- pobiera baze danych tylko trzeba adres zmienic
    }

    public ArrayList<AccountData> getPlatformBase() {
        return platformBase;
    }

    public void setPlatformBase(ArrayList<AccountData> platformBase) {
        this.platformBase = platformBase;
    }

    public void add(AccountData aD){
        System.out.println("dodawanie: "+ aD.getNick());

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.DriverManager");
            c = DriverManager
                    .getConnection("//195.150.230.210:5434",
                            "2020_hamernik_artur", "31996");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            stmt = c.createStatement();

            String sql =
                    "SELECT * FROM debt.person";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Wyswietlono");
        downloadBase();
    }

    public AccountData findUser(String nick){
        for(AccountData thisOne :platformBase) {
            if(nick.equals(thisOne.getNick())){
                return thisOne;
            }
        }
        return null;
    }

 // pobiera dane z bazy danych
    public void downloadBase(){
        Connection c = null;
        Statement stmt = null;
        platformBase = new ArrayList<>();
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
                String sqlNick = rs.getString("nick");
                String sqlPassword = rs.getString("password");
                platformBase.add(new AccountData(sqlNick,sqlPassword));
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

    public void deleteUser(String nick) {
        if (!nick.equals("administrator")) {
            Connection c = null;
            Statement stmt = null;
            try {
                Class.forName("org.postgresql.DriverManager");
                c = DriverManager
                        .getConnection("jdbc:postgresql://rogue.db.elephantsql.com:5432/ezstzdga",
                                "ezstzdga", "o6zkA0o8vitrVts8Z37XcUEY1v9Z61rw");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                String sql = "DELETE from public.users where nick = '"+ nick+"'";
                System.out.println(sql);
                stmt.executeUpdate(sql);
                c.commit();
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Operation done successfully");
        }
        downloadBase();
    }
}


