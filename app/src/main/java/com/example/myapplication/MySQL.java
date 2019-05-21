package com.example.myapplication;

import android.util.Log;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class MySQL {
    String driver;// JDBCドライバの登録
    String server, dbname, url, user, password;// データベースの指定
    Connection con;
    Statement stmt;
    Map<String, Object> lng = new HashMap<>();
    String time;
    String x;
    String y;
    String z;
    String latitude;
    String longitude;

    public MySQL() {
        this.driver = "org.gjt.mm.mysql.Driver";
        this.server = "mznjerk.mizunolab.info";
        this.dbname = "mznjerk";
        this.url = "jdbc:mysql://" + server + "/" + dbname + "?useUnicode=true&characterEncoding=UTF-8";
        this.user = "mznjerk";
        this.password = "kansoukikashiteyo";
        try {
            System.out.println("url"+url);
            System.out.println("user"+user);
            System.out.println("password"+password);
            this.con = DriverManager.getConnection(url, user, password);
            this.stmt = con.createStatement ();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Class.forName (driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //値の無い物全て
    public ResultSet getID() {
        ResultSet rs = null;
        String sql = "SELECT * FROM `Test1` WHERE 1";
        try {
            rs = stmt.executeQuery (sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;
    }

    public void updateImage(String time, String x, String y, String z, String latitude, String longitude) {
        //テーブルへ格納
        StringBuffer buf = new StringBuffer();
        buf.append("INSERT INTO `Test1` (`date`, `x`, `y`, `z`, `latitude`, `longitude`) VALUES( "+time+","+ x +","+ y +","+ z +","+ latitude +","+ longitude +");");
        String sql = buf.toString();
        try {
            stmt.execute (sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
