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
    int sleepTime = 10;
    int postCountMax = 6000;
    @Override
    public void run() {
        Document doc;
        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";

        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://127.0.0.1:3306/tianya"+postType+"?useUnicode=true&characterEncoding=UTF-8";

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
            while (ReplyUtil.getRunFlag() || (!ReplyUtil.isDocsEmpty())){
                try {
                    HashMap<String,Object> docs = ReplyUtil.removeDocument();
                    if (docs!=null){
                        doc = (Document)docs.get("doc");
                        String initiatePostId = (String)docs.get("initiatePostId");
//                    if (doc!=null){
                        Elements replies = doc.select("div.atl-head-reply");
                        for (Element replyTemp : replies) {
                            Elements hrefs = replyTemp.select("a[href]"); //带有href属性的a元素
                            if (hrefs!=null){
                                switch (hrefs.size()){
                                    case 5: {
                                        //主帖,需update
                                        Element replyTimeELementUpdate = hrefs.first();
                                        String replytimeUpdate = replyTimeELementUpdate.attr("replytime");
                                        if (replytimeUpdate!=null && !replytimeUpdate.isEmpty()){
                                            //update location set languages = 'zh' where locationid = '12344';
                                            String updateSql = "update initiatePost set initiateTime = ? where id = ?;";
//                                            System.out.println(updateSql);
                                            PreparedStatement psUpdate=conn.prepareStatement(updateSql);
                                            psUpdate.setString(1,replytimeUpdate);
                                            psUpdate.setString(2,initiatePostId);
                                            psUpdate.executeUpdate();
                                        }
                                    }
                                    case 2:{
                                        Element replyTimeELement = hrefs.first();
                                        String replytime = replyTimeELement.attr("replytime");
                                        if (replytime!=null && !replytime.isEmpty()){
                                            Date currentTime = new Date();
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            String crawlTime = formatter.format(currentTime);
                                            String insql = "INSERT INTO viewPost (initiatePostId,viewTime,crawlTime) VALUES(?,?,?);";
//                                            System.out.println(insql);
                                            PreparedStatement ps=conn.prepareStatement(insql);
                                            ps.setString(1,initiatePostId);
                                            ps.setString(2,replytime);
                                            ps.setString(3,crawlTime);
                                            ps.executeUpdate();
                                        }
                                    }
                                        break;

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
