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

    public static int insertReservation(Reservation reservation) {
        int id = -1;
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="insert into reservation" +
                    "(user_id, image_name, vm_id, source, public_ip, username, password, end_time)"
                    + " values ("
                    + reservation.getUser_id() + ", \""
                    + reservation.getImage_name() +  "\", \""
                    + reservation.getVm_id() + "\", "
                    + reservation.getSource() + ", \""
                    + reservation.getPublic_ip() + "\", \""
                    + reservation.getUsername() + "\", \""
                    + reservation.getPassword() + "\", \""
                    + reservation.getEnd_time() + "\");";

            System.out.println(sql);
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = statement.getGeneratedKeys();

            if (rs.next())
              id = rs.getInt(1);

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return id;
    }

    public static int getAvailable(){
        try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="SELECT count(*) as c FROM vm_info where availability = 1";

            resultSet = statement.executeQuery(sql);
            if(resultSet.next())
              return resultSet.getInt("c");
            else
              return 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return 4;
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

    public static String getAvailablePrivateCloudVM() {
      try{
          connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
          statement=connection.createStatement();
          String sql = "select vm_id from vm_info where availability=1";

          resultSet = statement.executeQuery(sql);
          // Call getAvailable() to make sure atleast 1 vm is free before calling this function.
          if(resultSet.next())
            return resultSet.getString("vm_id"); // return the first available vm

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
