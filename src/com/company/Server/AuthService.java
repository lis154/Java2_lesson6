package com.company.Server;

import java.sql.*;

/**
 * Created by i.lapshinov on 19.06.2018.
 */
public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:userDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) throws SQLException { // возвращаем nikname
        String query = String.format("SELECT nickname FROM main " +
                "where LOGIN = '%s'" +
                " and PASSWORD = '%s'",login, pass);
        ResultSet rs = stmt.executeQuery(query);// запрос к базе, который ожидает ответа
        //ResultSet rs = stmt.execute();
        if (rs.next()){ // есть ли в нашей таблице записи
            return rs.getString(1);
        }
        return null;
    }
    public static boolean stateLogin (String login) throws SQLException
    {
        String query = String.format("SELECT state FROM main " +
                "where LOGIN = '%s'",login);
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            if (rs.getString(1).equals("false")) {
                return true;
            } else return false;
        } return false;
    }


    public static void disconnect()
    {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setTrueOnClient (String login) // клиент уже есть
    {
        String query = String.format("update  main set state = 'true' where login == '%s'",login);
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setFalseOnClient (String nick) // клиент свободен
    {
        String query = String.format("update  main set state = 'false' where nickname == '%s'",nick);
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }







}
