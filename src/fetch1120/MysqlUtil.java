package fetch1120;


import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-19
 * Time: 上午9:15
 * To change this template use File | Settings | File Templates.
 */
public class MysqlUtil implements Global {
    Connection conn = null;

    public MysqlUtil() throws ClassNotFoundException {
        // 加载驱动程序
        Class.forName(driver);
    }
    public void initDataBase() throws SQLException {
        // 连续数据库
        String createDatebaseurl = preDatabaseUrl + endDatabaseUrl;
        conn = DriverManager.getConnection(createDatebaseurl, user, password);
        if(!conn.isClosed())
            System.out.println("Succeeded connecting to the Database");
        Statement s = conn.createStatement();
        for (String board : items){
            String tableName = datebaseHeader+board;
            s.addBatch("CREATE DATABASE IF NOT EXISTS "+tableName+
                    " default character set utf8");
            s.addBatch("Create table IF NOT EXISTS " + tableName + ".initiatePost(" +
                    "id varchar(40) primary key," +
                    "title varchar(80)," +
                    "initiateTime datetime," +
                    "crawlTime datetime," +
                    "type varchar(10)," +
                    "clickCount int," +
                    "viewCount int) ENGINE=InnoDB DEFAULT CHARSET=utf8");
            s.addBatch("Create table IF NOT EXISTS "+tableName+".viewPost(" +
                    "id int primary key auto_increment," +
                    "initiatePostId varchar(40)," +
                    "viewTime datetime," +
                    "crawlTime datetime) ENGINE=InnoDB DEFAULT CHARSET=utf8");
        }
        s.executeBatch();
        s.close();
        conn.close();
    }

    public Connection getConn(String name) throws SQLException {
        String url = preDatabaseUrl+datebaseHeader+name+endDatabaseUrl;
        conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);
        return conn;
    }
    public void close(){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void refetch(String name,String id) throws SQLException {
        getConn(name);
        PreparedStatement psUpdate=conn.prepareStatement(refetchUpdate);
        psUpdate.setString(1,id);
        psUpdate.executeUpdate();
        psUpdate.close();
        PreparedStatement psDelete=conn.prepareStatement(refetchDelete);
        psDelete.setString(1,id);
        psDelete.executeUpdate();
        psDelete.close();
        conn.commit();
        conn.close();

    }
}
