package com.boge.jdbc.v2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtils {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mybatis-vip?characterEncoding=utf-8&serverTimezone=UTC";
    private static final String JDBC_NAME = "root";
    private static final String JDBC_PASSWORD = "123456";

    private static  Connection conn;

    /**
     * 对外提供获取数据库连接的方法
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

    /**
     * 关闭资源
     * @param conn
     */
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
}
