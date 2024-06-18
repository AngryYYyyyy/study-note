package com.boge.jdbc.v4;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static Integer update(String sql,Object ... parameter) throws Exception{
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(parameter != null && parameter.length > 0){
            for (int i = 0; i < parameter.length; i++) {
                ps.setObject(i+1,parameter[i]);
            }
        }
        int i = ps.executeUpdate();
        close(conn,ps);
        return i;
    }

    /**
     * 查询方法的简易封装
     * @param sql
     * @param clazz
     * @param parameter
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> query(String sql, Class clazz, Object ... parameter) throws  Exception{
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(parameter != null && parameter.length > 0){
            for (int i = 0; i < parameter.length; i++) {
                ps.setObject(i+1,parameter[i]);
            }
        }
        ResultSet rs = ps.executeQuery();
        // 获取对应的表结构的元数据
        ResultSetMetaData metaData = ps.getMetaData();
        List<T> list = new ArrayList<>();
        while(rs.next()){
            // 根据 字段名称获取对应的值 然后将数据要封装到对应的对象中
            int columnCount = metaData.getColumnCount();
            Object o = clazz.newInstance();
            for (int i = 1; i < columnCount+1; i++) {
                // 根据每列的名称获取对应的值
                String columnName = metaData.getColumnName(i);
                Object columnValue = rs.getObject(columnName);
                setFieldValueForColumn(o,columnName,columnValue);
            }
            list.add((T) o);
        }
        return list;
    }

    /**
     * 根据字段名称设置 对象的属性
     * @param o
     * @param columnName
     */
    private static void setFieldValueForColumn(Object o, String columnName,Object columnValue) {
        Class<?> clazz = o.getClass();
        try {
            // 根据字段获取属性
            Field field = clazz.getDeclaredField(columnName);
            // 私有属性放开权限
            field.setAccessible(true);
            field.set(o,columnValue);
            field.setAccessible(false);
        }catch (Exception e){
            // 说明不存在 那就将 _ 转换为 驼峰命名法  user_name  --> userName
            if(columnName.contains("_")){
                Pattern linePattern = Pattern.compile("_(\\w)");
                columnName = columnName.toLowerCase();
                Matcher matcher = linePattern.matcher(columnName);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
                }
                matcher.appendTail(sb);
                // 再次调用复制操作
                setFieldValueForColumn(o,sb.toString(),columnValue);
            }
        }
    }
}
