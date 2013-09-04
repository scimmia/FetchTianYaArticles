import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-4
 * Time: 上午10:02
 * To change this template use File | Settings | File Templates.
 */

public class FetchArticlesUrl implements Runnable{
    final int replyMax = 5000;
    final int replyMin = 300;
    int urlCountMax = 500;
    int sleepTime = 80;
    int timeOut = 5000;
    final String UrlHeader = "http://www.tianya200.com";
    final String regex = "\\\\\\\"";
    /*  example
                DBName = "funinfo";
                urlPre = "http://www.tianya200.com/idx/23/";
                urlSuf = "/0.html";
                startPage = "66";
             */
    String DBName;
    String urlPre;
    String urlSuf;
    int startPage;

    StringBuilder sqlToInsert;

    LinkedList<HashMap> articleList;
    HashSet<String> urlSet;
    HashMap<String,Integer> urlAndCountMap;
    public FetchArticlesUrl(String DBName, String urlPre, String urlSuf, int startPage) {
        this.DBName = DBName;
        this.urlPre = urlPre;
        this.urlSuf = urlSuf;
        this.startPage = startPage;
        articleList = new LinkedList<HashMap>();
        urlAndCountMap = new HashMap<String,Integer>();
        urlSet = new HashSet<String>();

        sqlToInsert = new StringBuilder();
        sqlToInsert.append("INSERT INTO ");
        sqlToInsert.append(this.DBName);
        sqlToInsert.append(" (url) VALUES");
    }

    @Override
    public void run() {
        System.out.println(System.currentTimeMillis());
//        while (articleList.size()<5000){
//            try {
//                fetchHotActionByPage();
//                startPage++;
//                Thread.sleep(100);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//        }

//        try {
//            fetchHotActionByPage();
//            startPage++;
//            Thread.sleep(100);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        while (urlSet.size()<urlCountMax){
            try {
                fetchHotActionByPage();
                startPage++;
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        System.out.println(System.currentTimeMillis());

        for (String url:urlSet){
            sqlToInsert.append(" ('");
            sqlToInsert.append(url);
            sqlToInsert.append("'),");
        }
        int lastPoint = sqlToInsert.lastIndexOf(",");
        if (lastPoint>0){
            sqlToInsert.deleteCharAt(lastPoint);
            sqlToInsert.append(";");
            insertURLs(sqlToInsert.toString());
        }
        System.out.println(sqlToInsert);

    }

    private void fetchHotActionByPage(){
        try {
            Document doc = Jsoup.connect(urlPre+startPage+urlSuf).timeout(timeOut).get();
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
                        if (countTotalReplyInt > replyMin && countTotalReplyInt < replyMax && (countTotalReplyInt/countTotalLzInt > 3)){
                            Element sjtTemp = link.select("td.sjt").first(); //带有href属性的a元素
                            if (sjtTemp!=null){
                                Element hrefTemp = sjtTemp.select("a[href]").first();
                                String articleUrlTemp = hrefTemp.attr("href");
                                if (articleUrlTemp!=null){
//                                    articleList.add(articleUrlTemp);
                                    fetchRealUrl(articleUrlTemp,countTotalReplyInt);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void fetchRealUrl(String url,int replyCount){
        try {
            Thread.sleep(sleepTime);
            Document doc = Jsoup.connect(UrlHeader+url).timeout(timeOut).get();
            Elements scripts = doc.select("script[type]"); //带有href属性的a元素
            if (scripts!=null){
                String scriptText;
                String[] urls;
                for (Element script : scripts) {
                    scriptText = script.toString();
                    if (scriptText!=null && scriptText.contains("bbs.tianya.cn")){
                        urls = scriptText.split(regex);
                        for (String urlTemp : urls){
                            if (urlTemp.contains("bbs.tianya.cn")){
//                                System.out.println(urlTemp);
//                                urlAndCountMap.put(urlTemp, replyCount);
                                urlSet.add(urlTemp);
                            }
                        }
                    }
                }
            }



        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void insertURLs(String sql){
        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";

        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://127.0.0.1:3306/TianYaUrl";

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
            Statement statement = conn.createStatement();

            // 结果集
            Boolean result = statement.execute(sql);

            System.out.println("-----------------");

            conn.close();

        } catch(ClassNotFoundException e) {


            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();


        } catch(SQLException e) {


            e.printStackTrace();


        } catch(Exception e) {


            e.printStackTrace();


        }
    }
}
