package com.dssathe.cloudburst.utility;

import com.dssathe.cloudburst.model.Reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.concurrent.*;

public class Helper {

    static String driverName = "com.mysql.jdbc.Driver";
    static String connectionUrl = "jdbc:mysql://localhost:3306/";
    static String dbName = "accounts";
    static String userId = "root";
    static String password = "root";

    static Connection connection = null;
    static Statement statement = null;
    static ResultSet resultSet = null;

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public static long insertReservation(Reservation reservation) {
        long id = -1;
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="insert into reservation" +
                    "(user_id, image_name, vm_id, source, public_ip, username, password, start_time, end_time)"
                    + " values ("
                    + reservation.getUser_id() + ", \""
                    + reservation.getImage_name() +  "\", \""
                    + reservation.getVm_id() + "\", "
                    + reservation.getSource() + ", \""
                    + reservation.getPublic_ip() + "\", \""
                    + reservation.getUsername() + "\", \""
                    + reservation.getPassword() + "\", \""
                    + reservation.getStart_time() + "\", \""
                    + reservation.getEnd_time() + "\");";

            System.out.println(sql);
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = statement.getGeneratedKeys();

            if (rs.next())
              id = rs.getLong(1);

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return id;
    }

    public static void updateReservation(long reservationID, String ip, String pw) {
      try{
          connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
          statement=connection.createStatement();
          String sql = "update reservation set public_ip = '" + ip + "', password = '" + pw + "' where id = " + reservationID;
          statement.executeUpdate(sql);

      } catch (Exception e) {
          e.printStackTrace();
          System.out.println(e);
      }
    }

    public static Boolean reservationExists(long reservationID) {
      try{
          connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
          statement=connection.createStatement();
          String sql = "select id from reservation where id = " + reservationID;

          resultSet = statement.executeQuery(sql);
          if(resultSet.next())
            return true;

      } catch (Exception e) {
          e.printStackTrace();
          System.out.println(e);
      }
      return false;
    }

    public static int getAvailable(){
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="SELECT count(*) as c FROM vm_info where availability = 1";

            resultSet = statement.executeQuery(sql);
            if(resultSet.next())
              return resultSet.getInt("c");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return 0;
    }

    public static int markAvailability(String vm_id, int val){
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql = "update vm_info set availability = " + val + " where vm_id = '" + vm_id + "'";

            int update_count = statement.executeUpdate(sql);
            return update_count;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return 0;
    }

    public static synchronized String getAvailablePrivateCloudVM() {
      try{
          connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
          statement=connection.createStatement();
          String sql = "select vm_id from vm_info where availability=1";

          resultSet = statement.executeQuery(sql);
          // Call getAvailable() to make sure atleast 1 vm is free before calling this function.
          if(resultSet.next()) {
            String vm_id = resultSet.getString("vm_id"); // get the first available vm
            // mark the vm as unavailable
            markAvailability(vm_id, 0);
            return vm_id;
          }

      } catch (Exception e) {
          e.printStackTrace();
          System.out.println(e);
      }
      return "";
    }

    public static String getIPofPrivateCloudVM(String vm_id) {
      try{
          connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
          statement=connection.createStatement();
          String sql = "select public_ip from vm_info where vm_id='" + vm_id + "'";

          resultSet = statement.executeQuery(sql);
          if(resultSet.next())
            return resultSet.getString("public_ip");

      } catch (Exception e) {
          e.printStackTrace();
          System.out.println(e);
      }
      return "";
    }
}
