package com.boge.jdbc.v3;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtils {

    private static final String JDBC_URL ;
    private static final String JDBC_NAME ;
    private static final String JDBC_PASSWORD ;

    private static  Connection conn;

    static {
        Properties properties = new Properties();
        InputStream in = DBUtils.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JDBC_URL = properties.getProperty("JDBC_URL");
        JDBC_NAME = properties.getProperty("JDBC_NAME");
        JDBC_PASSWORD = properties.getProperty("JDBC_PASSWORD");
    }

    /**
     * 获取连接通道
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        if(conn == null){
            try{
                conn = DriverManager.getConnection(JDBC_URL,JDBC_NAME,JDBC_PASSWORD);
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception();
            }
        }
        return conn;
    }

    public static void close(Connection conn ){
        close(conn,null);
    }

    public static void close(Connection conn, Statement sts ){
        close(conn,sts,null);
    }

    public static void close(Connection conn, Statement sts , ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(sts != null){
            try {
                sts.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 执行数据库的DML操作
     * @return
     */
    public static Integer update(String sql,Object ... paramter) throws Exception{
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(paramter != null && paramter.length > 0){
            for (int i = 0; i < paramter.length; i++) {
                ps.setObject(i+1,paramter[i]);
            }
        }
        int i = ps.executeUpdate();
        close(conn,ps);
        return i;
    }
}
