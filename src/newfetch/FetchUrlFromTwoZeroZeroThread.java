package newfetch;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.Log4jUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Affe
 * Date: 13-9-6
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class FetchUrlFromTwoZeroZeroThread implements Runnable {
    Logger logger;
    @Override
    public void run() {
         /*
        娱乐八卦 funinfo    23
        天涯杂谈 free   10
        时尚资讯 no11   18
        国际观察 worldlook  21
        情感天地 feeling 17
         */
        String[] boards = {"funinfo","free","no11","worldlook","feeling","23","10","18","21","17"};
//        String[] boards = {"23","10","18","21","17"};
//        String[] boards = {"funinfo","23"};
        int boardsNum = boards.length/2;
        int urlCountMax = 6000;

        int sleepTime = 1500;
        Scanner stdin = new Scanner(System.in);
        System.out.println("请输入抓取板块序号: 1 娱乐八卦 2 天涯杂谈 3 时尚资讯 4 国际观察 5 情感天地");
        //String nextLine()方法:此扫描器执行当前行，并返回跳过的输入信息
        int boardIndex = stdin.nextInt();
        System.out.println("请输入开始的页数:");
//        int startPage = 195;
        int startPage = stdin.nextInt();
        boardIndex--;
        String dbName = boards[boardIndex];
        String urlName = boards[boardIndex+boardsNum];
        Log4jUtil.initLog4j("TianYa200-" + dbName);
        logger = Logger.getLogger(dbName);
        int urlCount = 0;
        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://127.0.0.1:3306/tianya"+dbName+"?useUnicode=true&characterEncoding=UTF-8";
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
                System.out.println("Succeeded connecting to the Database：" + dbName);
            String querySql = "SELECT COUNT(id) FROM initiatepost;";
            PreparedStatement psUpdate=conn.prepareStatement(querySql);
            ResultSet rs = psUpdate.executeQuery();
            if (rs.next()){
                urlCount = rs.getInt("COUNT(id)");
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String insertSql = "REPLACE INTO initiatePost (id,type,crawlTime,viewCount) VALUES(?,?,?,?);";
            PreparedStatement ps=conn.prepareStatement(insertSql);
            String crawlTimeToInsert;

            while (urlCount<urlCountMax){
                logger.info(String.format("当前page：%d\t帖子总数：%d",startPage,urlCount));
//                System.out.print(String.format("\n时间\t%tR\t当前page：%d\t帖子总数：%d", System.currentTimeMillis(),startPage,urlCount));
                try {
                    Thread.sleep(sleepTime);
                    String response = HttpClientUtil.getHtmlByUrl(String.format("util://www.tianya200.com/idx/%s/%d/0.html",urlName,startPage));
                    if (response== null){
                        continue;
                    }
                    Document doc = Jsoup.parse(response);
                    Element page = doc.select("table.idx_ph").first();
                    Elements links = page.select("tr"); //带有href属性的a元素
                    for (Element link : links) {
                        Elements tds = link.select("td.num"); //带有href属性的a元素
                        if (tds == null || tds.size()<1){
                            continue;
                        }else {
                            Element countTotalReply = tds.last().select("span.green").first();
                            Element countTotalLz = tds.last().select("span.red").first();
                            if (countTotalReply!=null && countTotalLz!=null){
                                int countTotalReplyInt = Integer.parseInt(countTotalReply.text());
                                int countTotalLzInt = Integer.parseInt(countTotalLz.text());
                                if ( (countTotalReplyInt/countTotalLzInt >= 8) && countTotalReplyInt<= 6000){
                                    Element sjtTemp = link.select("td.sjt").first(); //带有href属性的a元素
                                    if (sjtTemp!=null){
                                        Element hrefTemp = sjtTemp.select("a[href]").first();
                                        String articleUrlTemp = hrefTemp.attr("href");
                                        String articleTitle = hrefTemp.text();
                                        if (articleUrlTemp!=null){
                                            Thread.sleep(sleepTime);
                                            String realUrl = fetchRealUrl("util://www.tianya200.com"+articleUrlTemp);
                                            if (realUrl!=null){
                                                logger.info(String.format("抓到url：%s",realUrl));
//                                                System.out.print(String.format("\t%s",realUrl.substring(realUrl.indexOf("-"),realUrl.lastIndexOf("-1"))));
                                                crawlTimeToInsert = formatter.format(new java.util.Date());
                                                ps.setString(1,realUrl.substring(realUrl.indexOf("/post-")));
                                                ps.setString(2,dbName);
                                                ps.setString(3,crawlTimeToInsert);
                                                ps.setInt(4,countTotalReplyInt);
                                                ps.executeUpdate();
                                                urlCount++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.error(e);
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                startPage++;
            }
            ps.close();
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

    private String fetchRealUrl(String url){
        String result = null;
        try {
            String response = HttpClientUtil.getHtmlByUrl( url);
            if (response== null){
                return result;
            }
            Document doc = Jsoup.parse(response);
            Elements scripts = doc.select("script"); //带有href属性的a元素
            if (scripts!=null){
                String scriptText;
                String[] urls;
                for (Element script : scripts) {
                    scriptText = script.toString();
                    if (scriptText!=null && scriptText.contains("bbs.tianya.cn")){
                        urls = scriptText.split("\\\\\\\"");
                        for (String urlTemp : urls){
                            if (urlTemp.contains("bbs.tianya.cn")){
                                result = urlTemp;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            return result;
        }

    }
}
