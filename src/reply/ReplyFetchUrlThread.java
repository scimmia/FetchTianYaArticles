package reply;

import http.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class ReplyFetchUrlThread implements Runnable{
//    String urlToFetch;
//    String initiatePostId;
//    public ReplyFetchUrlThread(String urlToFetch) {
//        this.urlToFetch = urlToFetch;
//        initiatePostId = new String(urlToFetch);
//    }
    String postType;

    public ReplyFetchUrlThread(String postType) {
        this.postType = postType;
    }
    long runTime = 360000;
    int sleepTime = 20;

    @Override
    public void run() {
        String crawlTime = null;
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

            if (crawlTime==null){
                java.util.Date currentTime = new java.util.Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                crawlTime = formatter.format(currentTime);
            }
            String querySql = "SELECT id FROM initiatePost;";

//                String updateSql = "SELECT id FROM initiatePost WHERE crawlTime < ? order by crawlTime desc LIMIT ?;";
            PreparedStatement psUpdate=conn.prepareStatement(querySql);
//                psUpdate.setString(1,crawlTime);
//                psUpdate.setInt(2,fetchLimit);
            ResultSet rs = psUpdate.executeQuery();

            String initiatePostId = null;

            while(rs.next()) {
                // 选择sname这列数据
                initiatePostId = rs.getString("id");
                // 输出结果
                System.out.println(rs.getString("id"));

                String urlToFetch = new String(initiatePostId);
                long startTime = System.currentTimeMillis();
                long currenttime = startTime;
//        String urlTemp;

                while ((urlToFetch != null && !urlToFetch.isEmpty()) && currenttime - startTime < runTime){
                    try {
                        System.out.println(String.format("时间\t%tR\t\t%s\t\t",System.currentTimeMillis(),urlToFetch));
                        String response = HttpClientUtil.getHtmlByUrl("http://bbs.tianya.cn"+urlToFetch);
                        if (response != null && !response.equalsIgnoreCase("")){
                            Document doc = Jsoup.parse(response);

                            Element pages = doc.select("div.atl-pages").first();
                            Element nextPage = pages.select("a[href]").last();
                            String nextUrlToFetch = nextPage.attr("href");
                            if (!nextPage.text().equalsIgnoreCase("下页")){
                                Element nextPageSpan = pages.select("span").last();
                                if (nextPageSpan.text().equalsIgnoreCase("下页")){
                                    urlToFetch = null;
                                    HashMap<String,Object> docs = new HashMap<String, Object>();
                                    docs.put("initiatePostId",initiatePostId);
                                    docs.put("doc",doc);
                                    ReplyUtil.addDocument(docs);
                                }else{
                                    System.out.println("刷新"+urlToFetch);
                                }
                            }else {
                                urlToFetch = nextUrlToFetch;
                                HashMap<String,Object> docs = new HashMap<String, Object>();
                                docs.put("initiatePostId",initiatePostId);
                                docs.put("doc",doc);
                                ReplyUtil.addDocument(docs);
                            }
                        }
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }

            rs.close();
            conn.close();

        } catch(ClassNotFoundException e) {
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        ReplyUtil.setRunFlag();
    }
}
