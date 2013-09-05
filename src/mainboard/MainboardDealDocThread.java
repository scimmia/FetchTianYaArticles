package mainboard;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
public class MainboardDealDocThread implements Runnable {
    String postType;

    public MainboardDealDocThread(String postType) {
        this.postType = postType;
    }
    int sleepTime = 200;
    int postCountMax = 6000;
    @Override
    public void run() {
        int replyMax = 5000;
        int replyMin = 500;
        Document doc;
        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";

        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://127.0.0.1:3306/testb?useUnicode=true&characterEncoding=UTF-8";

        // MySQL配置时的用户名
        String user = "root";

        // MySQL配置时的密码
        String password = "";

        try {
            // 加载驱动程序
            Class.forName(driver);

            // 连续数据库
            Connection conn = DriverManager.getConnection(url, user, password);

            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // statement用来执行SQL语句
//            Statement statement = conn.createStatement();

            while (MainboardDocUtil.getPostCount() < postCountMax){
                try {
                    doc = MainboardDocUtil.removeDocument();
                    if (doc!=null){
                        Element page = doc.select("div.mt5").last();
                        Elements links = page.select("tr"); //带有href属性的a元素
                        for (Element link : links) {
                            Elements tds = link.select("td"); //带有href属性的a元素
                            if (tds!=null && tds.size()==5){
//                    System.out.println(tds);
                                Element elementTemp = tds.remove(0);
                                Element tdA = elementTemp.select("a[href]").first();
                                String postId = tdA.attr("href");
                                String postTitle = tdA.text();
                                elementTemp = tds.remove(0);
                                tdA = elementTemp.select("a[href]").first();
                                String postAuthor = tdA.text();
                                elementTemp = tds.remove(0);
                                int postClick = Integer.parseInt(elementTemp.text());
                                elementTemp = tds.remove(0);
                                int postReply = Integer.parseInt(elementTemp.text());
                                if (postReply>replyMin && postReply<replyMax){
                                    Date currentTime = new Date();
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String crawlTime = formatter.format(currentTime);
                                    String insql = "INSERT INTO "+postType+" (id,title,crawlTime,clickCount,viewCount,type) VALUES(?,?,?,?,?,?);";
                                    PreparedStatement ps=conn.prepareStatement(insql);
                                    ps.setString(1,postId);
                                    ps.setString(2,postTitle);
                                    ps.setString(3, crawlTime);
                                    ps.setInt(4, postClick);
                                    ps.setInt(5,postReply);
                                    ps.setString(6, postType);
                                    ps.executeUpdate();
//                                    String sql = String.format("INSERT INTO mainboard (id,title,crawlTime,clickCount,viewCount) VALUES('%s','%s', '%s',%d,%d);",postId,postTitle,crawlTime,postType,postClick,postReply);
//                                    System.out.println(sql);
//                                    statement.execute(sql);
                                    MainboardDocUtil.addPostCount();

                                }
                            }
                        }
                    }
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            // 结果集
            conn.close();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
