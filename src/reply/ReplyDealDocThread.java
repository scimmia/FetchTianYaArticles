package reply;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class ReplyDealDocThread implements Runnable {
    String postType;

    public ReplyDealDocThread(String postType) {
        this.postType = postType;
    }
    int sleepTime = 200;
    int postCountMax = 6000;
    @Override
    public void run() {
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

                try {
                    HashMap<String,Object> docs = ReplyUtil.removeDocument();
                    if (docs!=null){
                        doc = (Document)docs.get("doc");
//                    if (doc!=null){
                        Elements replies = doc.select("div.atl-head-reply");
                        for (Element replyTemp : replies) {
                            Elements hrefs = replyTemp.select("a[href]"); //带有href属性的a元素
                            if (hrefs!=null){
                                switch (hrefs.size()){
                                    case 4:
                                        //
                                        break;
                                    case 2:
                                        break;

                                }
                            }
                            if (hrefs!=null && hrefs.size()==4){
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
                                    String insql = "INSERT INTO mainboard (id,title,crawlTime,clickCount,viewCount,type) VALUES(?,?,?,?,?,?);";
                                    PreparedStatement ps=conn.prepareStatement(insql);
                                    ps.setString(1,postId);
                                    ps.setString(2,postTitle);
                                    ps.setString(3,crawlTime);
                                    ps.setInt(4,postClick);
                                    ps.setInt(5,postReply);
                                    ps.setString(6,postType);
                                    ps.execute();
//                                    String sql = String.format("INSERT INTO mainboard (id,title,crawlTime,clickCount,viewCount) VALUES('%s','%s', '%s',%d,%d);",postId,postTitle,crawlTime,postType,postClick,postReply);
//                                    System.out.println(sql);
//                                    statement.execute(sql);

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
