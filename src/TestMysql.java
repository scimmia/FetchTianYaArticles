import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-4
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */
public class TestMysql {
    public static void main(String[] args){
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, false);
                System.out.println(calendar.getTimeInMillis());
return;
//        // 驱动程序名
//        String driver = "com.mysql.jdbc.Driver";
//
//        // URL指向要访问的数据库名scutcs
//        String url = "jdbc:mysql://127.0.0.1:3306/testa";
//
//        // MySQL配置时的用户名
//        String user = "root";
//
//        // MySQL配置时的密码
//        String password = "";
//
//        try {
//            // 加载驱动程序
//            Class.forName(driver);
//
//            // 连续数据库
//
//            Connection conn = DriverManager.getConnection(url, user, password);
//
//            if(!conn.isClosed())
//                System.out.println("Succeeded connecting to the Database!");
//
//            // statement用来执行SQL语句
//            Statement statement = conn.createStatement();
////            statement.execute("insert into a (name,des) values('斯蒂芬速度12','司123法所跌幅');");
////            statement.execute("insert into a (name,des) values('斯蒂芬123速度','司法123所跌幅');");
////            statement.execute("insert into a (name,des) values('斯蒂芬123速度','司法所跌幅');");
////            statement.execute("insert into a (name,des) values('斯123蒂芬速度','司法124所跌幅');");
////            statement.execute("insert into a (name,des) values('斯123蒂芬速度','司法342所跌幅');");
//            // 要执行的SQL语句
//            String sql = "select * from a";
//
//            // 结果集
//            ResultSet rs = statement.executeQuery(sql);
//
//            System.out.println("-----------------");
//            System.out.println("执行结果如下所示:");
//            System.out.println("-----------------");
//            System.out.println(" 学号" + "\t" + " 姓名");
//            System.out.println("-----------------");
//
//            String name = null;
//
//            while(rs.next()) {
//
//                // 选择sname这列数据
//                name = rs.getString("name");
//
//                // 首先使用ISO-8859-1字符集将name解码为字节序列并将结果存储新的字节数组中。
//                // 然后使用GB2312字符集解码指定的字节数组
//                name = new String(name.getBytes("ISO-8859-1"),"GB2312");
//
//                // 输出结果
//                System.out.println(rs.getString("des") + "\t" + name);
//            }
//
//            rs.close();
//            conn.close();
//
//        } catch(ClassNotFoundException e) {
//
//
//            System.out.println("Sorry,can`t find the Driver!");
//            e.printStackTrace();
//
//
//        } catch(SQLException e) {
//
//
//            e.printStackTrace();
//
//
//        } catch(Exception e) {
//
//
//            e.printStackTrace();
//
//
//        }
    }

    class xxx{
        HashMap<String,String> hashMap = new HashMap<String, String>();
        void setId(String id){
            hashMap.put("id",id);
        }
        String getId(){
            return  hashMap.get("id");
        }

        void setVaule(int num,String sth){
            hashMap.put(""+num,sth);
        }
        String getValue(int num){
            return hashMap.get(""+num);
        }
    }
}
