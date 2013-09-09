package newfetch;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.Log4jUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Affe
 * Date: 13-9-8
 * Time: 下午12:42
 * To change this template use File | Settings | File Templates.
 */
public class FetchReplyFromTianYaThread implements Runnable {
    Logger logger;

    @Override
    public void run() {
         /*
        娱乐八卦 free
        天涯杂谈 funinfo
        时尚资讯 no11
        国际观察 worldlook
        情感天地 feeling
         */
        String[] boards = {"funinfo","free","no11","worldlook","feeling"};
        int sleepTime = 20;
        Scanner stdin = new Scanner(System.in);
        System.out.println("请输入抓取板块序号: 1 娱乐八卦 2 天涯杂谈 3 时尚资讯 4 国际观察 5 情感天地");
        //String nextLine()方法:此扫描器执行当前行，并返回跳过的输入信息
        int boardIndex = stdin.nextInt();
        String board = boards[boardIndex-1];
        Log4jUtil.initLog4j("TianYa-" + board);
        Log4jUtil.initLog4j(board);
        logger = Logger.getLogger(board);

        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://127.0.0.1:3306/tianya"+board+"?useUnicode=true&characterEncoding=UTF-8";
        // MySQL配置时的用户名
        String user = "root";
        // MySQL配置时的密码
        String password = "1";

        try {
            // 加载驱动程序
            Class.forName(driver);
            // 连续数据库
            Connection conn = DriverManager.getConnection(url, user, password);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database：" + board);

            String querySql = "SELECT id FROM initiatePost WHERE title IS NULL ORDER BY crawlTime;";
            PreparedStatement psUpdate=conn.prepareStatement(querySql);
            ResultSet rs = psUpdate.executeQuery();
            String initiatePostId = null;
            while(rs.next()) {
                // 选择sname这列数据
                initiatePostId = rs.getString("id");
                // 输出结果
                logger.info(String.format("\n当前id：\t\t%s",initiatePostId));

                String urlToFetch = new String(initiatePostId);
                LinkedList<String> viewTimsList = new LinkedList<String>();
                Document doc;
                String response;
                logger.info("抓取网站：");
                long errorTime = 0;
                while (urlToFetch != null && errorTime < 30){
                    try {
                        logger.info(urlToFetch);
//                            System.out.print(String.format("抓取网站：\t\t%s",urlToFetch));
                        response = HttpClientUtil.getHtmlByUrl("util://bbs.tianya.cn" + urlToFetch);
                        if (response != null && !response.equalsIgnoreCase("")){
                            boolean needUpdate = (urlToFetch.equalsIgnoreCase(initiatePostId));
                            doc = Jsoup.parse(response);

                            Element pages = doc.select("div.atl-pages").first();
                            if (pages == null){
                                urlToFetch = null;
                            } else {
                                Element nextPage = pages.select("a[href]").last();
                                String nextUrlToFetch = nextPage.attr("href");
                                if (!nextPage.text().equalsIgnoreCase("下页")){
                                    Element nextPageSpan = pages.select("span").last();
                                    if (nextPageSpan.text().equalsIgnoreCase("下页")){
                                        urlToFetch = null;
                                    }else{
                                        System.out.println("刷新"+urlToFetch);
                                        continue;
                                    }
                                }else {
                                    urlToFetch = nextUrlToFetch;
                                }
                            }
                            viewTimsList.clear();
                            Elements replies = doc.select("div.atl-head-reply");
                            for (Element replyTemp : replies) {
                                Elements hrefs = replyTemp.select("a[href]"); //带有href属性的a元素
                                if (hrefs!=null){
                                    Element replyTimeELement = hrefs.first();
                                    if (replyTimeELement!=null){
                                        String replytime = replyTimeELement.attr("replytime");
                                        if (replytime!=null){
                                            viewTimsList.add(replytime);
                                        }
                                    }
                                }
                            }
                            if (viewTimsList.size()>0){
                                if (needUpdate){
                                    //第一页，需要update
                                    Element elementTmep = doc.select("span.s_title").first();
                                    elementTmep = elementTmep.select("span").first();
                                    String title = elementTmep.text();

                                    elementTmep = doc.select("div.atl-menu").first();
                                    int clickCount = Integer.parseInt(elementTmep.attr("js_clickcount"));
                                    int replycount = Integer.parseInt(elementTmep.attr("js_replycount"));

                                    String updateSql = "update initiatePost set initiateTime = ? , title = ? ,clickCount = ? ,viewCount = ? where id = ?;";
//                                            System.out.println(updateSql);
                                    PreparedStatement updateInitiateTime =conn.prepareStatement(updateSql);
                                    updateInitiateTime.setString(1,viewTimsList.getFirst());
                                    updateInitiateTime.setString(2,title);
                                    updateInitiateTime.setInt(3,clickCount);
                                    updateInitiateTime.setInt(4,replycount);
                                    updateInitiateTime.setString(5,initiatePostId);
                                    updateInitiateTime.executeUpdate();
                                }
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String insertSql = "INSERT INTO viewPost (initiatePostId,viewTime,crawlTime) VALUES(?,?,?);";
                                PreparedStatement ps=conn.prepareStatement(insertSql);
                                String crawlTimeToInsert;
                                while (!viewTimsList.isEmpty()){
                                    crawlTimeToInsert = formatter.format(new java.util.Date());
                                    ps.setString(1,initiatePostId);
                                    ps.setString(2,viewTimsList.removeFirst());
                                    ps.setString(3,crawlTimeToInsert);
                                    ps.executeUpdate();
                                }
                                errorTime = 0;
                            }
                        }
//                            Thread.sleep(sleepTime);
//                        } catch (InterruptedException e) {
//                            errorTime++;
//                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (NullPointerException e) {
                        errorTime++;
                        urlToFetch = null;
                    }  catch (Exception e) {
                        errorTime++;
                        logger.error(e);
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }

            rs.close();
            conn.close();

        } catch(ClassNotFoundException e) {
            System.out.println("Sorry,can`t find the Driver!");
            logger.error(e);
//            e.printStackTrace();
        } catch(SQLException e) {
            logger.error(e);
//            e.printStackTrace();
        } catch(Exception e) {
            logger.error(e);
//            e.printStackTrace();
        }
    }
}
