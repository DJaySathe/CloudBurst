package com.dssathe.cloudburst.utility;

import com.dssathe.cloudburst.model.Reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Helper {

    static String driverName = "com.mysql.jdbc.Driver";
    static String connectionUrl = "jdbc:mysql://localhost:3306/";
    static String dbName = "accounts";
    static String userId = "root";
    static String password = "root";

    static Connection connection = null;
    static Statement statement = null;
    static ResultSet resultSet = null;

    public static void insertReservation(Reservation reservation) {
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="insert into reservation() values ();";

            resultSet = statement.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public static int getAvailable(){
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="SELECT count(*) as c FROM vm where Available = 1";

            resultSet = statement.executeQuery(sql);
            return resultSet.getInt("c");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return 4;
    }
}
