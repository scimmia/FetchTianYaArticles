import http.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    int urlCountMax = 6000;
    int sleepTime = 1000;
    int timeOut = 30000;
    long runTime = 3600000;
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

    Statement statement;
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
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
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
            statement = conn.createStatement();
            System.out.println("运行时间\t\t总条数\t\t当前页数");

            while (urlSet.size()<urlCountMax && currentTime-startTime<runTime){
                System.out.println(((currentTime-startTime)/1000)+"\t\t\t"+urlSet.size()+"\t\t\t"+startPage);
                try {
                    fetchHotActionByPage();
                    startPage++;
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                currentTime = System.currentTimeMillis();
            }
            System.out.println(System.currentTimeMillis());

//            for (String url:urlSet){
//                sqlToInsert.append(" ('");
//                sqlToInsert.append(url);
//                sqlToInsert.append("'),");
//            }
//            int lastPoint = sqlToInsert.lastIndexOf(",");
//            if (lastPoint>0){
//                sqlToInsert.deleteCharAt(lastPoint);
//                sqlToInsert.append(";");
//                insertURLs(sqlToInsert.toString());
//            }
            System.out.println(System.currentTimeMillis());
            System.out.println(sqlToInsert);

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

    private void fetchHotActionByPage(){
        try {
            String response = HttpClientUtil.getHtmlByUrl(urlPre+startPage+urlSuf);
            if (response== null){
                return;
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

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void fetchRealUrl(String url,int replyCount){
        try {
            Thread.sleep(sleepTime);
            String response = HttpClientUtil.getHtmlByUrl(UrlHeader + url);
            if (response== null){
                return;
            }
            Document doc = Jsoup.parse(response);
//            Document doc = Jsoup.connect(UrlHeader+url).timeout(timeOut).get();
            Elements scripts = doc.select("script[postType]"); //带有href属性的a元素
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
                                StringBuilder sql =  new StringBuilder(sqlToInsert);
                                statement.execute(sql.append("('").append(urlTemp).append("');").toString());

                            }
                        }
                    }
                }
            }



        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
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
            statement.execute(sql);
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
