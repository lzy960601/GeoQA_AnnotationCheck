package jdbcutils;

import java.io.IOException;
import java.io.InputStream;
/**
 * 
 * @author guyu
 * 数据库连接工具，为了降低对接成本，共同使用同一个数据库
 */
import java.sql.*;
import java.util.Properties;

public class JdbcUtil{ 
    private String URL;
    private String JDBC_DRIVER;
    private String USER_NAME;
    private String PASSWORD;
    private Connection connection = null;
    /*
     * 静态代码块，类初始化时加载数据库驱动
     */
    {
        try {
            // 加载dbinfo.properties配置文件
            InputStream in = JdbcUtil.class.getClassLoader()
                    .getResourceAsStream("JavaUtil.properties");
            Properties properties = new Properties();
            properties.load(in);

            // 获取驱动名称、url、用户名以及密码
            JDBC_DRIVER = properties.getProperty("JDBC_DRIVER");
            URL = properties.getProperty("URL");
            USER_NAME = properties.getProperty("USER_NAME");
            PASSWORD = properties.getProperty("PASSWORD");

            // 加载驱动
            Class.forName(JDBC_DRIVER);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        if(connection == null){
        	try {
    			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        return connection;
    }

    public Connection getNewConnection(){
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return connection;
    }

    public void closeConnection(){
        if(connection != null){
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}