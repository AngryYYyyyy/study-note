# 一、JDBC简介

## 1.JDBC概述

JDBC（Java Database Connectivity）是一个用于Java语言的数据库访问接口，它提供了一种方法，使得Java应用能够以统一的方式访问不同类型的关系数据库。

JDBC的设计理念是提供一个==独立于特定数据库实现的API==，使Java应用程序可以与任何提供JDBC驱动的数据库进行交互。这意味着开发人员可以编写与数据库无关的代码，这大大增加了代码的可移植性和可重用性。

## 2.JDBC访问数据库的过程

### （1）导入JDBC依赖

### （2）加载和注册JDBC驱动
在Java程序中，你需要加载JDBC驱动，以便与数据库建立连接。

```java
//加载驱动 Driver
Driver driver =new com.mysql.cj.jdbc.Driver();
//注册驱动 DriverManager
DriverManager.registerDriver(driver);
```

观察com.mysql.cj.jdbc.Driver类，发现内部存在一个静态代码块，因此在加载时，就已经完成了加载和注册驱动

```java
public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    public Driver() throws SQLException {
    }
    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException var1) {
            throw new RuntimeException("Can't register driver!");
        }
    }
}
```

因此这通常通过调用`Class.forName()`实现：

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```

注意：在较新的JDBC版本中，这一步可能不是必需的，因为驱动程序可能会自动加载。

### （3）建立连接
使用`DriverManager.getConnection()`方法与数据库建立连接。这需要提供数据库的URL、用户名和密码：

```java
String url = "jdbc:mysql://localhost:3306/数据库名";
String username = "用户名";
String password = "密码";
Connection conn = DriverManager.getConnection(url, username, password);
```

URL：统一资源定位符，定位要连接的数据库

格式：协议://ip:端口/资源路径?参数名=参数值&参数名=参数值&.... 

- 协议：jdbc:mysql
- IP：localhost
- 端口号：3306
- 数据库名字：mydb
- 参数

例如：

```java
jdbc:mysql://127.0.0.1:3306/mydb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
```

### （4）创建语句对象

使用连接对象创建一个`Statement`，`PreparedStatement`或`CallableStatement`对象，用于执行SQL语句

#### `Statement`

```java
Statement stmt = conn.createStatement();
```

#### `PreparedStatement`

与普通的 `Statement` 相比，`PreparedStatement` 的主要优势在于提高性能和安全性，特别是在执行参数化查询时。这些查询是通过在 SQL 语句中使用占位符（通常是问号 `?`）来构建的，然后在执行前用实际的参数值替换这些占位符。

```java
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM 表名 WHERE 列名 = ?");
pstmt.setString(1, "某值");
```

### （5）执行SQL语句
#### DQL

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM 表名");
```

`ResultSet:`

1. **数据遍历**：`ResultSet` 提供了方法来遍历查询结果的每一行。通常使用 `next()` 方法来移动到结果集的下一行。
2. **获取数据**：可以使用各种 `get` 方法（如 `getString`、`getInt` 等）根据列索引或列名从当前行中检索数据。

示例：

```java
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM table_name");
while (rs.next()) {
    String columnData = rs.getString("column_name");
    // 处理数据
}
rs.close();//使用完 ResultSet 后，应该显式地关闭它，以释放数据库资源。
stmt.close();
```

#### DML

```java
int rowsAffected = stmt.executeUpdate("INSERT INTO 表名 (列1, 列2) VALUES (值1, 值2)");
```

### （6）关闭资源
在操作完成后，关闭所有资源，包括`ResultSet`、`Statement`和`Connection`：

```java
if (rs != null) rs.close();
if (stmt != null) stmt.close();
if (conn != null) conn.close();
```

### （7）异常处理
在整个过程中，要妥善处理`SQLException`异常。通常，这是通过将代码块放入`try-catch`结构中来实现的。

# 二、CRUD练习

## 1.实体类
将查询结果封装实体类，用于存储数据，一个实体类对象可以存储一个记录。

实体类要求：

- 类名和表名保持一致 
- 属性的个数、别名、数据类型（包装类）和列保持一致，且设为私有，设置get、set方法
- 日期类型推荐写成java.util.Date
- 必须具备空参构造方法
- 实体类应当实现序列化接口 

```java
//Emp实体类
public class Emp implements Serializable {
    private Integer empno;
    private String ename;
    private String job;
    private Integer mgr;
    private Date hiredate; //java.util.Date
    private Double sal;
    private Double comm;
    private Integer deptno;
    //空参构造器
    //构造器
    //get set方法
}
```

## 2.DML

```java
public class Test {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //加载驱动,自动加载
        //Class.forName("com.mysql.jdbc.Driver");
        //获得链接
        String url="jdbc:mysql://127.0.0.1:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String user="root";
        String password="root";
        Connection connection = DriverManager.getConnection(url, user, password);
        //获取语句工具
        Statement statement = connection.createStatement();
        //发送执行SQL语句
       int rows = statement.executeUpdate("insert into dept values (50,'销售部','北京');");
        //int rows = statement.executeUpdate("delete from dept where deptno=50");
        System.out.println("影响行数："+rows);
        //关闭资源
        statement.close();
        connection.close();
    }
}
```

加入异常处理

```java
public class Test {
    public static void main(String[] args)  {
        //加载驱动
        //Class.forName("com.mysql.jdbc.Driver");
        //获得链接
        String url="jdbc:mysql://127.0.0.1:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String user="root";
        String password="root";
        Connection connection = null;
        Statement statement=null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            //获取语句工具
            statement = connection.createStatement();
            //发送执行SQL语句
            int rows = statement.executeUpdate("insert into dept values (50,'销售部','北京');");
            //int rows = statement.executeUpdate("delete from dept where deptno=50");
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if(statement!=null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

## 3.DQL

```java
public class Test01 {
    private static String url="jdbc:mysql://127.0.0.1:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    public static void main(String[] args)  {
        testQuery();
    }
    public static void testQuery()  {
        //加载驱动
        //获取链接
        Connection connection = null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            //获取SQL语句工具
            statement = connection.createStatement();
            //发送执行SQL语句
            resultSet = statement.executeQuery("select * from emp");
            while(resultSet.next()){
                int empno = resultSet.getInt("empno");
                String ename = resultSet.getString("ename");
                String job = resultSet.getString("job");
                int mgr = resultSet.getInt("mgr");
                Date hiredate = resultSet.getDate("hiredate");
                double sal= resultSet.getDouble("sal");
                double comm= resultSet.getDouble("comm");
                int deptno= resultSet.getInt("deptno");
                System.out.println(""+empno+" "+ename+" "+job+" "+mgr+" "+hiredate+" "+sal+" "+comm+" "+deptno);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭资源
            if (resultSet!=null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (statement!=null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
```



# 三、SQL注入攻击

SQL注入攻击是一种代码注入技术，它利用了应用程序中的安全漏洞，允许攻击者将恶意的SQL代码插入到后端数据库的查询中。这种攻击可以用来绕过安全措施，获取、修改或删除数据库中的数据，甚至有可能完全控制或破坏受影响的系统。

## 1.引入

模拟登录:在前台输入用户名和密码，后台判断信息是否正确，并给出前台反馈信息，前台输出反馈信息。

```java
//实体类:Account
public class Account implements Serializable {
    private  int aid;
    private String username;
    private String password;
    private  int money;
}
```

```java
public class Test {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.print("输入账号:");
        String userName=sc.next();
        System.out.print("输入密码:");
        String passWord=sc.next();
        Account account = getAccount(userName,passWord);//获取账号，如果数据库中存在对应的用户和密码，则返回一个实体
        System.out.println(account == null ? "登录失败" : "登录成功");//判断登录状态
    }
    public static Account getAccount(String userName,String passWord){
        Connection connection = null;
        Statement statement=null;
        Account account=null;
        ResultSet resultSet=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //获取语句对象
             statement = connection.createStatement();
            //发送执行SQL语句
            resultSet = statement.executeQuery("select *from account where username='" + userName + "' and password='" + passWord + "'");
            while(resultSet.next()){
                int aid = resultSet.getInt("aid");
                String username = resultSet.getString("username");
                String pwda = resultSet.getString("password");
                int money = resultSet.getInt("money");
                account=new Account(aid,username,pwda,money);
                System.out.println(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (resultSet!=null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement!=null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return account;
    }
}
```

当我们测试时发现：

![image-20240617210608475](D:\note\数据库\JDBC\assets\image-20240617210608475.png)

如果输入了精心设计的用户名密码后，即使是错误的，也能登录成功。让登录功能形同虚设。这是为什么呢，这就是SQL注入风险，原因在于SQL语句是字符串拼接的。SQL语句中拼接的内容破坏了SQL语句原有的判断逻辑。

如何解决呢?使用PreparedStatement预编译语句对象就可以解决掉。

## 2.解决SQL注入攻击

`PreparedStatement` 是一种在执行 SQL 语句前预编译它们的方法。当你使用 `PreparedStatement` 时，SQL 语句在发送到数据库之前就已经编译了，只是留下了一些参数位置（用问号 `?` 表示），这些参数稍后会被安全地绑定到编译好的语句中。这就是为什么 `PreparedStatement` 可以有效防止 SQL 注入的原因：

- **参数的隔离**：使用 `PreparedStatement` 时，所有的输入参数都是作为绑定值传递给已经预编译的 SQL 语句的。这些参数不会被直接解释为 SQL 代码的一部分，因此恶意的 SQL 代码注入到参数中也不会被执行。

- **转义处理**：当使用如 `setString()` 方法设置参数值时，任何可能破坏 SQL 查询结构的字符（如单引号）都会被自动转义。例如，如果用户输入包含单引号（如 O'Reilly），`PreparedStatement` 会自动将其转义，确保这个值不会改变 SQL 命令的结构，而是被视为普通字符处理。

**示例：**

```java
public class Test01 {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.print("输入账号:");
        String userName=sc.next();
        System.out.print("输入密码:");
        String passWord=sc.next();
        Account account = getAccount(userName,passWord);
        System.out.println(account == null ? "登录失败" : "登录成功");
    }
    public static Account getAccount(String userName,String passWord){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        ResultSet resultSet=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="select*from account where username=? and password=?";//使用 ? 作为参数的占位符
            //获取预编译语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置占位符实际参数
            preparedStatement.setString(1,userName);
            preparedStatement.setString(2,passWord);
            //发送执行SQL语句
            resultSet = preparedStatement.executeQuery();
            //处理结果
            while(resultSet.next()){
                int aid = resultSet.getInt("aid");
                String username = resultSet.getString("username");
                String pwda = resultSet.getString("password");
                int money = resultSet.getInt("money");
                account=new Account(aid,username,pwda,money);
                System.out.println(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (resultSet!=null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return account;
    }
}
```

## 3.PreparedStatement原理

### （1）MySQL执行流程

![image-20240617211133018](D:\note\数据库\JDBC\assets\image-20240617211133018.png)

- 连接处理和安全验证：当客户端尝试连接到MySQL服务器时，MySQL首先处理连接请求，建立一个连接。在这个阶段，MySQL会进行用户认证，验证尝试连接的用户是否有权限访问数据库。如果认证失败，连接会被拒绝。

- 查询缓存：MySQL会检查查询缓存，看是否之前已经执行过相同的查询并且结果已经被缓存。如果找到了匹配的查询结果，MySQL可以直接返回缓存的结果，从而跳过后续的解析、优化和执行阶段，显著提高查询效率。

  **注意**：从MySQL 8.0开始，查询缓存功能已被移除，因为在高并发场景下它可能导致性能问题。

- 解析：如果查询缓存中没有找到结果，查询将进入解析阶段。在这一步，SQL语句被解析器解析成一个数据结构（解析树）。解析器会检查SQL语句的语法是否正确。如果语法有误，将返回错误。
- 预处理：在预处理阶段，MySQL对解析后的结构进行进一步检查，验证查询中的表和列是否存在，以及用户是否有权访问这些表和列。
- 优化：优化器是查询处理过程中的关键组成部分。优化器会评估多种可能的查询执行计划，并选择一个成本最低（预计最快）的执行计划。优化器的选择基于统计信息，如表的大小、索引的存在与否等。这个阶段可能包括重新组织查询，选择合适的索引，决定表的连接顺序等。
- 执行：一旦选择了最佳的执行计划，执行引擎将按照这个计划执行查询。在这个阶段，数据库引擎会从存储层检索数据，执行所有必要的操作，如表的扫描、排序、连接等。
- 返回结果：执行完毕后，最终的结果集会被发送回客户端。客户端收到结果后，查询处理流程结束。

### （2）预编译原理

**预编译**：当创建一个 `PreparedStatement` 对象并指定一个 SQL 语句时，该语句被发送到数据库服务器进行预编译。这个步骤涉及解析 SQL 语句、进行语法检查、优化查询计划等。预编译的结果是一个已编译的查询计划，它被存储在数据库服务器上待后续使用。

**参数绑定**：在执行 `PreparedStatement` 时，您可以通过提供参数值来“填充” SQL 语句中的占位符（通常是问号 `?`）。这些参数不会重新解析 SQL 语句，因此不会改变查询结构或触发额外的编译过程。

**执行**：执行预编译的 SQL 语句时，数据库服务器可以直接使用之前创建的查询计划。由于省略了编译步骤，执行速度通常比普通的 `Statement` 快。

**缓存**：SQL为Hash并与缓存中Hash表对应。如果有结果直接返回结果，如果没有对应继续向下执行

### （3）预编译作用

- **提高性能**：由于SQL语句在首次执行时就已经编译，后续执行相同结构的语句只需传递不同的参数，无需重新编译，这提高了性能。
- **防止SQL注入**：预编译语句通过参数化查询的方式，使得插入的数据不会被解释为SQL代码的一部分，从而有效防止SQL注入攻击。

### （4）启动预编译

通过设置URL中的参数来控制预编译是否开启

- useServerPrepStmts=true 开启预编译

- cachePrepStmts=true  启用预编译缓存

## 4.PreparedStatementCRUD

```java
public class Test02 {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useServerPrepStmts=true&cachePrepStmts=true";//useServerPrepStmts=true&cacheP																				repStmts=true增加了预编译
    private static String user="root";
    private static String password="root";
    public static void main(String[] args) {
        //testAdd();
        //testUpdata();
        //testQuery();
        testDelete();
    }
    public static void testAdd(){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="insert into emp values default,?,?,?,?,?,?,?";//设置自动递增，可以使用null或者default
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setString(1,"Mark");
            preparedStatement.setString(2,"MANAGER" );
            preparedStatement.setInt(3,7839);
            preparedStatement.setDate(4,new Date(System.currentTimeMillis()));//传入java.sql.Date
            preparedStatement.setDouble(5,3000.12);
            preparedStatement.setDouble(6,0.0);
            preparedStatement.setDouble(7,30);
            //发送执行SQL语句
            int rows= preparedStatement.executeUpdate();
            //处理结果
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void testUpdata(){
        //根据工号，修改员工姓名、工作
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="update emp set ename=?,job=? where empno=?";
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setString(1,"ZhangSan");
            preparedStatement.setString(2,"Clean");
            preparedStatement.setInt(3,7369);
            //发送执行SQL语句
            int rows= preparedStatement.executeUpdate();
            //处理结果
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void testQuery(){
        //根据工号，查询员工信息
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        ResultSet resultSet=null;
        List<Emp> emps=new ArrayList<>();
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="select*from emp where empno=?";
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,7369);
            //发送执行SQL语句
            resultSet = preparedStatement.executeQuery();
            //处理结果
            while(resultSet.next()){
                int empno = resultSet.getInt("empno");
                String ename = resultSet.getString("ename");
                String job = resultSet.getString("job");
                int mgr = resultSet.getInt("mgr");
                Date hiredate = resultSet.getDate("hiredate");
                double sal= resultSet.getDouble("sal");
                double comm= resultSet.getDouble("comm");
                int deptno= resultSet.getInt("deptno");
                Emp emp =new Emp(empno, ename, job, mgr, hiredate, sal, comm, deptno);
                emps.add(emp);
            }
            System.out.println(emps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (resultSet!=null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void testDelete(){
        //根据工号，删除员工信息
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="delete from emp where empno=?";
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setInt(1,7369);
            //发送执行SQL语句
            int rows= preparedStatement.executeUpdate();
            //处理结果
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

## 5.PreparedStatement批处理

`PreparedStatement` 的批处理（batch processing）用于在单个数据库会话中执行大量的 SQL 语句。这种方法可以显著提高性能，特别是当需要执行大量相似的数据库操作时，比如插入或更新多行数据。

**注意：**需要开启批处理模式，在url添加参数&rewriteBatchedStatements=true。

**示例：**向部门表增加10628条数据

```java
public class Test {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useServerPrepStmts=true&cachePrepStmts=true&rewriteBatchedStatements=true";//增加了批量处理
    private static String user="root";
    private static String password="root";
    public static void main(String[] args) {
        testBatchAdd();
    }
    public static void testBatchAdd(){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="insert into dept values (default,?,?)";
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < 10628; i++) {
                //设置参数
                preparedStatement.setString(1,"name"+i);
                preparedStatement.setString(2,"local" +i);
                //批量处理,将语句放入一个批量里面
                preparedStatement.addBatch();
                if((i&1000)==0){
                    //发送执行SQL语句
                    preparedStatement.executeBatch();//返回值int[]，代表执行的结果代号
                    								//SUCCESS_NO_INFO -2 EXECUTE_FAILED  -3
                    preparedStatement.clearBatch();
                }
            }
            int[] ints = preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            //处理结果
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

# 四、事务

## 1.禁用自动提交模式

默认情况下，每个 SQL 语句都是在其执行完毕后自动提交的。要开始一个事务，首先需要关闭这种自动提交行为。

```java
connection.setAutoCommit(false);
```

## 2. 提交或回滚事务

如果所有操作都成功执行，你可以提交事务。如果在执行过程中遇到任何错误，你应该回滚事务。

```java
connection.commit();
connection.rollback();//一般放到catch中，处理异常
```

**示例：**使用事务保证转账安全性

```java
public class Test {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    public static void main(String[] args) {
        test();
    }
    public static void test(){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        ResultSet resultSet=null;
        try {
            connection = DriverManager.getConnection(url,user,password);
            //设置取消自动提交
            connection.setAutoCommit(false);
            String sql="update account set money=money+? where username=?";
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //转入
            preparedStatement.setInt(1,50);
            preparedStatement.setString(2,"lisi");
            preparedStatement.executeUpdate();
            //制造异常
            int a=3/0;
            //转出
            preparedStatement.setInt(1,-50);
            preparedStatement.setString(2,"zhangsan");
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //设置回滚
            if(connection!=null){
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
        }finally {
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    //设置提交
                    connection.commit();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

## 3.设置回滚点

在 JDBC 中，设置回滚点（也称为保存点或 Savepoint）允许你在事务的特定点创建一个标记，这样你可以仅回滚到该特定点，而不是回滚整个事务。这在执行长事务时特别有用，可以在发生错误时仅撤销事务的一部分，而不是全部。

示例：批量添加10628个部门，但在增加4000个时产生异常，回滚到指定位置

```java
public class Test01 {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useServerPrepStmts=true&cachePrepStmts=true&rewriteBatchedStatements=true";//增加了批量处理
    private static String user="root";
    private static String password="root";
    public static void main(String[] args) {
        testBatchAdd();
    }
    public static void testBatchAdd(){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        Account account=null;
        //记录回滚点
        LinkedList<Savepoint> sps=new LinkedList<>();
        try {
            connection = DriverManager.getConnection(url,user,password);
            //设置取消自动提交
            connection.setAutoCommit(false);
            String sql="insert into dept values (default,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < 10628; i++) {
                preparedStatement.setString(1,"name"+i);
                preparedStatement.setString(2,"local" +i);
                preparedStatement.addBatch();
                if((i&1000)==0){
                    //在执行前记录回滚点
                    Savepoint sp = connection.setSavepoint();
                    sps.add(sp);
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
                //制造异常
                if(i==4000){
                    int a=1/0;
                }
            }
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            if(connection!=null){
                try {
                    //选择回滚的位置
                    Savepoint sp=sps.getLast();
                    if(sp!=null){
                        connection.rollback(sp);
                    }
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
            e.printStackTrace();
        }finally {
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.commit();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

# 五、JDBC API

JDBC API是一个用于Java应用程序与数据库之间交互的API。它提供了一组接口和类，使开发者能够通过统一的方法连接到数据库、执行SQL语句以及处理结果。

## 1. DriverManager
`DriverManager` 是 JDBC 的管理层，负责管理数据库驱动程序的列表。它可以动态加载数据库驱动，并为应用程序提供数据库连接。

**主要方法**：

- `getConnection(String url)`：尝试建立到给定数据库 URL 的连接。
- `getConnection(String url, Properties info)`：使用给定的属性尝试建立到数据库 URL 的连接。
- `getConnection(String url, String user, String password)`：使用用户名和密码尝试建立到数据库 URL 的连接。

## 2. Connection
`Connection` 接口代表数据库上下文，即对数据库的连接。通过这个连接，应用程序可以执行SQL语句、管理事务等。

**主要方法**：

- `close()`：关闭数据库连接。
- `createStatement()`：创建一个 `Statement` 对象以发送 SQL 语句到数据库。
- `prepareCall(String sql)`：创建一个 `CallableStatement` 对象以调用数据库存储过程。
- `prepareStatement(String sql)`：创建一个 `PreparedStatement` 对象以发送参数化的 SQL 语句到数据库。
- `commit()`：提交当前事务。
- `rollback()`：回滚当前事务。
- `setAutoCommit(boolean autoCommit)`：设置此连接的自动提交模式。

## 3. Statement
`Statement` 是用于执行静态 SQL 语句并返回它所生成结果的接口。

**主要方法**：

- `executeQuery(String sql)`：执行 SQL 查询并返回 `ResultSet`。
- `executeUpdate(String sql)`：执行 INSERT、UPDATE、或 DELETE 操作，返回受影响的行数。

## 4. PreparedStatement
`PreparedStatement` 接口是 `Statement` 的子接口，它表示预编译的 SQL 语句。这种方式比 `Statement` 更有效，通常用于执行参数化的 SQL 查询。

### （1）设置参数
- `setInt(int parameterIndex, int x)`: 设置指定参数的值为 Java `int` 数据类型。
- `setString(int parameterIndex, String x)`: 设置指定参数的值为 Java `String` 数据类型。
- `setDouble(int parameterIndex, double x)`: 设置指定参数的值为 Java `double` 数据类型。
- `setDate(int parameterIndex, Date x)`: 设置指定参数的值为 Java `Date` 数据类型。
- `setObject(int parameterIndex, Object x)`: 设置指定参数的值为 Java `Object` 数据类型，这是一个通用的设置方法，可以用于各种数据类型。

### （2）执行 SQL 语句
- `execute()`: 执行任何类型的 SQL 语句。返回 `true` 如果第一个结果是一个 `ResultSet` 对象；返回 `false` 如果第一个结果是更新计数或者没有结果。
- `executeQuery()`: 执行 SQL 查询并返回 `ResultSet` 对象。
- `executeUpdate()`: 执行 SQL 语句，如 INSERT、UPDATE 或 DELETE；返回一个整数，表示受影响的行数。

### （3）批量更新
这些方法用于执行批处理命令，可以一次执行多个更新操作，提高执行效率：

- `addBatch()`: 将一组参数添加到此 `PreparedStatement` 对象的批处理命令中。
- `executeBatch()`: 执行批处理命令。

## 5. ResultSet
`ResultSet` 接口表示数据库查询结果。它是执行查询后返回的一组数据，可以通过一系列的方法来访问和操作这些数据。

**主要方法**：

- `next()`：将光标从当前位置向下移动一行，用于遍历结果集。
- `close()`：关闭 `ResultSet` 对象，释放相关资源。
- `getInt(int colIndex)`, `getString(int colIndex)` 等：获取当前行的指定列的值。

# 六、DAO

DAO（Data Access Object）是一种设计模式，==用于抽象和封装对数据源的所有访问==。在Java编程中，DAO是一种常见的实践，用于分离应用程序的业务逻辑和数据访问逻辑。这种模式允许应用程序通过DAO接口与数据源交互，而不是直接与数据源交互，从而提高了代码的可维护性和可重用性。

## 1.DAO模式的组成

- 实体类：和数据库表格一一对应的类,单独放入一个包中,包名往往是 pojo/entity/bean,要操作的每个表格都应该有对应的实体类
- DAO层：定义了对数据要执行那些操作的接口和实现类,包名往往是 dao/mapper,要操作的每个表格都应该有对应的接口和实现类

## 2.DAO简单的实现

示例：员工管理系统

DAO层接口

```java
//部门
public interface DeptDao {
    List<Dept> findAll();
    int addDept(Dept dept);
}
//员工
public interface EmpDao {
    int addEmp(Emp emp);
    int deleteByEmpno(int empno);
    List<Emp> findAll();
    int updateEmp(Emp emp);
}
```

DAO层实现类

```java
public class DeptDaoImpl implements DeptDao {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    @Override
    public List<Dept> findAll() {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        List<Dept> depts =null;
        try{
            connection = DriverManager.getConnection(url, user,password);
            String sql="select * from dept";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            depts=new ArrayList<Dept>() ;
            while(resultSet.next()){
                int deptno = resultSet.getInt("deptno");
                String dname = resultSet.getString("dname");
                String loc = resultSet.getString("loc");
                Dept dept =new Dept(deptno,dname,loc);
                depts.add(dept);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != resultSet){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != connection){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return depts;
    }
    @Override
    public int addDept(Dept dept) {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        int rows=0;
        try{
            connection = DriverManager.getConnection(url, user,password);
            String sql="insert into dept values(?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1,dept.getDeptno());
            preparedStatement.setObject(2,dept.getDname());
            preparedStatement.setObject(3,dept.getLoc() );
            rows =preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != connection){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rows;
    }
}
```

```java
public class EmpDaoImpl implements EmpDao {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    public EmpDaoImpl() {
        super();
    }

    @Override
    public int addEmp(Emp emp) {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        int rows=0;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="insert into emp values (default,?,?,?,?,?,?,?)";//设置自动递增
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setObject(1,emp.getEname());
            preparedStatement.setObject(2,emp.getJob() );
            preparedStatement.setObject(3,emp.getMgr());
            preparedStatement.setObject(4,emp.getHiredate());
            preparedStatement.setObject(5,emp.getSal());
            preparedStatement.setObject(6,emp.getComm());
            preparedStatement.setObject(7,emp.getDeptno());
            //发送执行SQL语句
            rows= preparedStatement.executeUpdate();
            //处理结果
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rows;
    }

    @Override
    public int deleteByEmpno(int empno) {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        int rows=0;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="delete from emp where empno=?";//设置自动递增
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setObject(1,empno);
            //发送执行SQL语句
            rows= preparedStatement.executeUpdate();
            //处理结果
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rows;
    }

    @Override
    public List<Emp> findAll() {
        List<Emp> emps = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="select *from emp";//设置自动递增
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            //发送执行SQL语句
            ResultSet resultSet = preparedStatement.executeQuery();
            //处理结果
            while(resultSet.next()){
                int empno = resultSet.getInt("empno");
                String ename = resultSet.getString("ename");
                String job = resultSet.getString("job");
                int mgr = resultSet.getInt("mgr");
                Date hiredate = resultSet.getDate("hiredate");
                double sal= resultSet.getDouble("sal");
                double comm= resultSet.getDouble("comm");
                int deptno= resultSet.getInt("deptno");
                Emp emp =new Emp(empno, ename, job, mgr, hiredate, sal, comm, deptno);
                emps.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return emps;
    }

    @Override
    public int updateEmp(Emp emp) {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        int rows=0;
        //加载驱动
        //获取链接
        try {
            connection = DriverManager.getConnection(url,user,password);
            //创建SQL语句
            String sql="update emp set ename =? ,job=?, mgr =?,hiredate =?,sal=?,comm=?,deptno=? where empno =?;";//设置自动递增
            //获取语句对象
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setObject(1,emp.getEname());
            preparedStatement.setObject(2,emp.getJob() );
            preparedStatement.setObject(3,emp.getMgr());
            preparedStatement.setObject(4,emp.getHiredate());
            preparedStatement.setObject(5,emp.getSal());
            preparedStatement.setObject(6,emp.getComm());
            preparedStatement.setObject(7,emp.getDeptno());
            preparedStatement.setObject(8,emp.getEmpno());
            //发送执行SQL语句
            rows= preparedStatement.executeUpdate();
            //处理结果
            System.out.println("影响行数："+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if (preparedStatement!=null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rows;
    }
}
```

实体类

```java
public class Emp implements Serializable {
    private Integer empno;
    private String ename;
    private String job;
    private Integer mgr;
    private Date hiredate;
    private Double sal;
    private Double comm;
    private Integer deptno;

    public Emp() {
    }

    public Emp(Integer empno, String ename, String job, Integer mgr, Date hiredate, Double sal, Double comm, Integer deptno) {
        this.empno = empno;
        this.ename = ename;
        this.job = job;
        this.mgr = mgr;
        this.hiredate = hiredate;
        this.sal = sal;
        this.comm = comm;
        this.deptno = deptno;
    }

    public Integer getEmpno() {
        return empno;
    }

    public void setEmpno(Integer empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getMgr() {
        return mgr;
    }

    public void setMgr(Integer mgr) {
        this.mgr = mgr;
    }

    public Date getHiredate() {
        return hiredate;
    }

    public void setHiredate(Date hiredate) {
        this.hiredate = hiredate;
    }

    public Double getSal() {
        return sal;
    }

    public void setSal(Double sal) {
        this.sal = sal;
    }

    public Double getComm() {
        return comm;
    }

    public void setComm(Double comm) {
        this.comm = comm;
    }

    public Integer getDeptno() {
        return deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "empno=" + empno +
                ", ename='" + ename + '\'' +
                ", job='" + job + '\'' +
                ", mgr=" + mgr +
                ", hiredate=" + hiredate +
                ", sal=" + sal +
                ", comm=" + comm +
                ", deptno=" + deptno +
                '}';
    }
}

```

```java
public class Dept implements Serializable {
    private Integer deptno;
    private String dname;
    private String loc;

    public Dept() {
    }

    public Dept(Integer deptno, String dname, String loc) {
        this.deptno = deptno;
        this.dname = dname;
        this.loc = loc;
    }

    public Integer getDeptno() {
        return deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Dept{" +
                "deptno=" + deptno +
                ", dname='" + dname + '\'' +
                ", loc='" + loc + '\'' +
                '}';
    }
}
```

控制台

```java
public class EmpManageSystem {
    private static Scanner sc =new Scanner(System.in);
    private static EmpDao empDao =new EmpDaoImpl();
    private static DeptDao deptDao=new DeptDaoImpl();
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");;
    public static void main(String[] args) {
        while(true){
            showMenu();
            System.out.println("请录入选项");
            int option  =sc.nextInt();
            switch (option){
                case 1:
                    case1();
                    break;
                case 2:
                    case2();
                    break;
                case 3:
                    case3();
                    break;
                case 4:
                    case4();
                    break;
                case 5:
                    case5();
                    break;
                case 6:
                    case6();
                    break;
                case 7:
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("请正确输入选项");
            }
        }
    }
    private static void case1(){
        List<Emp> emps = empDao.findAll();
        emps.forEach(System.out::println);
    }
    private static void case2(){
        List<Dept> depts = deptDao.findAll();
        depts.forEach(System.out::println);
    }
    private static void case3(){
        System.out.println("请输入要删除的员工编号");
        int empno=sc.nextInt();
        empDao.deleteByEmpno(empno);
    }
    private static void case4(){
        System.out.println("请输入员工编号");
        int empno =sc.nextInt();
        System.out.println("请输入员工姓名");
        String ename =sc.next();
        System.out.println("请输入员工职位");
        String job =sc.next();
        System.out.println("请输入员工上级");
        int mgr =sc.nextInt();
        System.out.println("请输入员工入职日期,格式为yyyy-MM-dd");
        Date hiredate =null;
        try {
            hiredate =simpleDateFormat.parse(sc.next());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("请输入员工工资");
        double sal =sc.nextDouble();
        System.out.println("请输入员工补助");
        double comm=sc.nextDouble();
        System.out.println("请输入员工部门号");
        int deptno =sc.nextInt();
        Emp emp=new Emp(empno, ename, job, mgr, hiredate, sal, comm,deptno);
        empDao.updateEmp(emp);
    }
    private static void case5(){
        System.out.println("请输入员工姓名");
        String ename =sc.next();
        System.out.println("请输入员工职位");
        String job =sc.next();
        System.out.println("请输入员工上级");
        int mgr =sc.nextInt();
        System.out.println("请输入员工入职日期,格式为yyyy-MM-dd");
        Date hiredate =null;
        try {
            hiredate = simpleDateFormat.parse(sc.next());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("请输入员工工资");
        double sal =sc.nextDouble();
        System.out.println("请输入员工补助");
        double comm=sc.nextDouble();
        System.out.println("请输入员工部门号");
        int deptno =sc.nextInt();
        Emp emp=new Emp(null, ename, job, mgr, hiredate, sal, comm,deptno);
        empDao.addEmp(emp);
    }
    private static void case6(){
        System.out.println("请录入部门号");
        int deptno =sc.nextInt();
        System.out.println("请录入部门名称");
        String dname =sc.next();
        System.out.println("请录入部门位置");
        String loc =sc.next();
        Dept dept =new Dept(deptno,dname,loc);
        deptDao.addDept(dept);
    }
    public static void showMenu(){
        System.out.println("************************************");
        System.out.println("* 1 查看所有员工信息");
        System.out.println("* 2 查看所有部门信息");
        System.out.println("* 3 根据工号删除员工信息");
        System.out.println("* 4 根据工号修改员工信息");
        System.out.println("* 5 增加员工信息");
        System.out.println("* 6 增加部门信息");
        System.out.println("* 7 退出");
        System.out.println("************************************");
    }
}
```

## 3.BaseDao的抽取

将empDaoImpl、deptDaoImpl的重复代码抽象

```java
public class BaseDao {
    private static String url="jdbc:mysql://localhost:3306/testdb?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static String user="root";
    private static String password="root";
    public int baseUpdate(String sql,Object...args){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        int rows=0;
        try{
            connection = DriverManager.getConnection(url, user,password);
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <=args.length ; i++) {
                preparedStatement.setObject(i,args[i-1]);
            }
            rows= preparedStatement.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != resultSet){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != connection){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rows;
    }
    public List baseQuery(Class clazz, String sql, Object ... args){
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        ArrayList<Object> list = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(url, user,password);
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <=args.length ; i++) {
                preparedStatement.setObject(i,args[i-1]);
            }
            resultSet = preparedStatement.executeQuery();
            //设置属性可以访问
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }

            while (resultSet.next()){
                //通过反射创建对象,因此实体类必须有无参构造器
                Object o = clazz.newInstance();
                for(Field e:fields){
                    String fieldName = e.getName();
                    Object data = resultSet.getObject(fieldName);
                    //修改对象的属性
                    e.set(o,data);
                }
                list.add(o);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != resultSet){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != connection){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
```

```java
public class DeptDaoImpl extends BaseDao implements DeptDao {

    @Override
    public List<Dept> findAll() {
        String sql="select *from dept ";
        return baseQuery(Dept.class, sql);
    }
    @Override
    public int addDept(Dept dept) {
        String sql="insert into dept values(?,?,?)";
        return baseUpdate(sql,dept.getDeptno(),dept.getDname(),dept.getLoc());
    }
}
```

```java
public class EmpDaoImpl extends BaseDao implements EmpDao {
    public EmpDaoImpl() {
        super();
    }

    @Override
    public int addEmp(Emp emp) {
        String sql="insert into emp values (default,?,?,?,?,?,?,?)";
        return baseUpdate(sql, emp.getEname(),emp.getJob(),emp.getMgr(),emp.getHiredate(),emp.getSal(),emp.getComm(),emp.getDeptno());
    }

    @Override
    public int deleteByEmpno(int empno) {
       String sql="delete from emp where empno=?";
       return baseUpdate(sql,empno);
    }

    @Override
    public List<Emp> findAll() {
        String sql="select *from emp";
        return baseQuery(Emp.class,sql);
    }

    @Override
    public int updateEmp(Emp emp) {
        String sql="update emp set ename =? ,job=?, mgr =?,hiredate =?,sal=?,comm=?,deptno=? where empno =?";
        return baseUpdate(sql, emp.getEname(),emp.getJob(),emp.getMgr(),emp.getHiredate(),emp.getSal(),emp.getComm(),emp.getDeptno(),emp.getEmpno());
    }
}
```

# 七、连接池

## 1.传统连接方式
在传统的数据库连接方式中，每次操作数据库时，应用程序都需要：
1. 加载数据库驱动：`Class.forName("com.mysql.cj.jdbc.Driver")`。
2. 创建新的数据库连接：`DriverManager.getConnection(url, username, password)`。
3. 执行SQL语句。
4. 关闭数据库连接。

这种方式虽然简单直接，但在高并发场景下效率极低，因为每次操作都要进行连接的建立和断开，这不仅增加了延迟，也极大地消耗了系统资源。

## 2.连接池方式

### （1）工作流程

数据库连接池在应用程序启动时预先创建并维护一定数量的数据库连接对象，这些连接对象被存储在一个连接池中，以备后续使用。

1. **预先创建连接**：应用程序启动时，连接池根据配置预先建立一组数据库连接，这些连接处于待命状态。
2. **连接分配**：当应用程序发起数据库请求时，连接池会分配一个现有的空闲连接给该请求。这避免了每次请求都需要创建和销毁连接的开销。
3. **连接回收**：请求处理完毕后，应用程序通过调用 `close()` 方法并非真正关闭连接，而是将连接返回到连接池中。这样连接就可以被后续的请求重复使用。
4. **动态管理**：连接池可以根据实际的使用情况动态调整池中的连接数量。如果检测到高负载情况，连接池可以自动增加连接数，反之则减少。
5. **等待机制**：如果所有连接都在使用中，新的请求将等待直到有连接变为可用。大多数连接池实现都支持配置最大等待时间，超过该时间请求将被拒绝或抛出异常。

### （2）优势

- 提高资源利用效率：避免了连接频繁创建和销毁的资源浪费。
- 增强应用性能：减少了每次请求的延迟，提高了响应速度。
- 支持高并发：连接池使应用能够支持同时处理多个数据库请求，适应高并发场景。

# 八、三大范式

## 1.范式概述

**范式**（Normal Form，NF）是数据库设计中用以确保数据结构合理、减少冗余以及避免各种异常的一系列规则的总结。范式的应用关键在于优化关系数据库的结构。

## 2.数据库设计的目标
- **结构合理**：确保数据组织逻辑清晰，易于维护。
- **冗余较小**：减少数据重复，节省存储资源，提高查询效率。
- **避免异常**：尽量减少插入、删除和修改操作引起的数据不一致问题。

## 3.范式的分类及其应用
1. **第一范式 (1NF)**：确保每列保持原子性，即列的值不可分割。
2. **第二范式 (2NF)**：在第一范式的基础上，确保所有非主键列完全依赖于主键。
3. **第三范式 (3NF)**：在第二范式的基础上，确保每列只依赖于主键，不依赖于其他非主键列。
4. **Boyce-Codd范式 (BCNF)**：一种更严格的第三范式，处理一些特殊的依赖情况，确保主键列之外没有过多依赖。
5. **第四范式 (4NF)**：进一步处理多值依赖，确保没有非主键属性之间的依赖。
6. **第五范式 (5NF)**：解决复合多值依赖，通常认为是完全规范化的形式。

## 4.范式的实用性
虽然范式等级越高，设计质量通常认为越高，但实际应用中，达到第三范式通常已足够，可以避免大部分常见的设计问题。高级范式（如第四范式和第五范式）可能使设计更加复杂，并不总是必要的，应根据具体需求和情况权衡设计的复杂度和性能需求。

