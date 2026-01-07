package util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

//    String drivername="com.mysql.cj.jdbc.Driver";
//    Class.forName(drivername);

    static String dburl = "jdbc:mysql://localhost:3306/experiments";
    static String dbuser = "root";
    static String dbpasswd = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dburl,dbuser,dbpasswd);
    }
}