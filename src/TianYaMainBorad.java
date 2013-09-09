import util.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 上午9:53
 * To change this template use File | Settings | File Templates.
 */
public class TianYaMainBorad implements Runnable{
    final int replyMax = 5000;
    final int replyMin = 300;
    int urlCountMax = 6000;
    int sleepTime = 2000;
    long runTime = 3600000;
    final String regex = "\\\\\\\"";
    /*  example
                DBName = "funinfo";
                urlPre = "util://www.tianya200.com/idx/23/";
                urlSuf = "/0.html";
                startPage = "66";
             */
    String postType;
    String urlToFetch;


    StringBuilder sqlToInsert;
    int totalPost = 0;
    LinkedList<Document> docs;

    public TianYaMainBorad(String postType, String urlToFetch) {
        this.postType = postType;
        this.urlToFetch = urlToFetch;

        docs = new LinkedList<Document>();

        sqlToInsert = new StringBuilder();
        sqlToInsert.append("INSERT INTO ");
        sqlToInsert.append(this.postType);
        sqlToInsert.append(" (url) VALUES");
    }

    Statement statement;
    long startTime;
    long currentTime;
    @Override
    public void run() {
        System.out.println(System.currentTimeMillis());
        startTime = System.currentTimeMillis();
        currentTime = startTime;
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

//            fetchHotActionByPage(this.urlToFetch);
//            System.out.println("运行时间\t\t总条数\t\t当前页数");
            new dealDocThread().run();
            while (totalPost<urlCountMax && currentTime-startTime<runTime){
//                System.out.println(((currentTime-startTime)/1000)+"\t\t\t"+urlSet.size()+"\t\t\t"+startPage);
                try {
                    if (this.urlToFetch == null){
                        System.out.println("main  sleep");
                        Thread.sleep(sleepTime);
                    }else {
                        fetchHotActionByPage(this.urlToFetch);
                    }
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                currentTime = System.currentTimeMillis();
            }
            System.out.println(System.currentTimeMillis());

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

//    private void fetchHotActionByPage(String urlToFetch){
//        try {
//            if (urlToFetch == null || totalPost > urlCountMax){
//                return;
//            }
//            Thread.sleep(sleepTime);
//
//            String response = util.HttpClientUtil.getHtmlByUrl(urlToFetch);
//            if (response == null || response.equalsIgnoreCase("")){
//                return;
//            }
//            Document doc = Jsoup.parse(response);
//
//            Element pages = doc.select("div.links").first();
//            Element nextPage = pages.select("a[href]").last();
//            String nextUrlToFetch = nextPage.attr("href");
//            if (!nextPage.text().equalsIgnoreCase("下一页")){
//                System.out.println("刷新"+urlToFetch);
//                fetchHotActionByPage(urlToFetch);
//                return;
//            }
//
//            Element page = doc.select("div.mt5").last();
//            Elements links = page.select("tr"); //带有href属性的a元素
//            for (Element link : links) {
//                Elements tds = link.select("td"); //带有href属性的a元素
//                if (tds!=null && tds.size()==5){
////                    System.out.println(tds);
//                    Element elementTemp = tds.remove(0);
//                    Element tdA = elementTemp.select("a[href]").first();
//                    String postId = tdA.attr("href");
//                    String postTitle = tdA.text();
//                    elementTemp = tds.remove(0);
//                    tdA = elementTemp.select("a[href]").first();
//                    String postAuthor = tdA.text();
//                    elementTemp = tds.remove(0);
//                    int postClick = Integer.parseInt(elementTemp.text());
//                    elementTemp = tds.remove(0);
//                    int postReply = Integer.parseInt(elementTemp.text());
//                    if (postReply>replyMin && postReply<replyMax){
//                        Date currentTime = new Date();
//                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String crawlTime = formatter.format(currentTime);
//                        statement.execute(String.format("INSERT INTO mainboard (id,title,crawlTime,type,clickCount,viewCount) VALUES('%s','%s','%s','%s',%d,%d);",postId,postTitle,crawlTime,postType,postClick,postReply));
//                        totalPost++;
//                    }
//                }
//            }
//            System.out.println(String.format("运行时间\t%tR\t总条数\t%d\t当前页数%s",System.currentTimeMillis(),totalPost,nextUrlToFetch));
//
//            fetchHotActionByPage("util://bbs.tianya.cn"+nextUrlToFetch);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//    }

    private void fetchHotActionByPage(String urlToFetch){
        try {
            String urlTemp = new String(urlToFetch);
            this.urlToFetch = null;

            String response = HttpClientUtil.getHtmlByUrl(urlTemp);
            if (response == null || response.equalsIgnoreCase("")){
                this.urlToFetch = urlTemp;
                return;
            }
            Document doc = Jsoup.parse(response);

            Element pages = doc.select("div.links").first();
            Element nextPage = pages.select("a[href]").last();
            String nextUrlToFetch = nextPage.attr("href");
            if (!nextPage.text().equalsIgnoreCase("下一页")){
                System.out.println("刷新"+urlToFetch);
                this.urlToFetch = urlTemp;
                return;
            }else {
                this.urlToFetch = nextUrlToFetch;
            }


            System.out.println(String.format("运行时间\t%tR\t总条数\t%d\t当前页数%s",System.currentTimeMillis(),totalPost,nextUrlToFetch));

//            fetchHotActionByPage("util://bbs.tianya.cn"+nextUrlToFetch);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public class dealDocThread implements Runnable{

        @Override
        public void run() {
            while (totalPost<urlCountMax && currentTime-startTime<runTime){
                try {
                    if (docs.size()==0){
                        System.out.println("dealDocThread  sleep");

                        Thread.sleep(sleepTime);
                    }else {
                        Document doc = docs.remove();

                        Element pages = doc.select("div.links").first();
                        Element nextPage = pages.select("a[href]").last();
                        String nextUrlToFetch = nextPage.attr("href");
                        if (!nextPage.text().equalsIgnoreCase("下一页")){
                            System.out.println("刷新"+urlToFetch);
                            fetchHotActionByPage(urlToFetch);
                            return;
                        }

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
                                    statement.execute(String.format("INSERT INTO mainboard (id,title,crawlTime,type,clickCount,viewCount) VALUES('%s','%s','%s','%s',%d,%d);",postId,postTitle,crawlTime,postType,postClick,postReply));
                                    totalPost++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
//                currentTime = System.currentTimeMillis();
            }
        }
    }
}


//public class TianYaMainBorad implements Runnable{
//    final int replyMax = 5000;
//    final int replyMin = 300;
//    int urlCountMax = 6000;
//    int sleepTime = 100;
//    int timeOut = 30000;
//    final String UrlHeader = "util://www.tianya200.com";
//    final String regex = "\\\\\\\"";
//    /*  example
//                DBName = "funinfo";
//                urlPre = "util://www.tianya200.com/idx/23/";
//                urlSuf = "/0.html";
//                startPage = "66";
//             */
//    String DBName;
//    String urlToFetch;
//
//
//    StringBuilder sqlToInsert;
//
//    HashSet<String> urlSet;
//    public TianYaMainBorad(String DBName, String urlToFetch) {
//        this.DBName = DBName;
//        this.urlToFetch = urlToFetch;
//
//        urlSet = new HashSet<String>();
//
//        sqlToInsert = new StringBuilder();
//        sqlToInsert.append("INSERT INTO ");
//        sqlToInsert.append(this.DBName);
//        sqlToInsert.append(" (url) VALUES");
//    }
//
//    @Override
//    public void run() {
//        System.out.println(System.currentTimeMillis());
//        long startTime = System.currentTimeMillis();
//        long currentTime = startTime;
//        while (urlSet.size()<urlCountMax && currentTime-startTime<3600000){
//            System.out.println(((currentTime-startTime)/1000)+"\t\t\t"+urlSet.size());
//            try {
//                fetchHotActionByPage();
//                Thread.sleep(sleepTime);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//            currentTime = System.currentTimeMillis();
//        }
//        System.out.println(System.currentTimeMillis());
//
//        for (String url:urlSet){
//            sqlToInsert.append(" ('");
//            sqlToInsert.append(url);
//            sqlToInsert.append("'),");
//        }
//        int lastPoint = sqlToInsert.lastIndexOf(",");
//        if (lastPoint>0){
//            sqlToInsert.deleteCharAt(lastPoint);
//            sqlToInsert.append(";");
//            insertURLs(sqlToInsert.toString());
//        }
//        System.out.println(System.currentTimeMillis());
//        System.out.println(sqlToInsert);
//
//    }
//
//    private void fetchHotActionByPage(){
//        try {
//            String response = util.HttpClientUtil.getHtmlByUrl(this.urlToFetch);
//            if (response == null){
//                return;
//            }
//            Document doc = Jsoup.parse(response);
//
//            Element page = doc.select("div.p").first();
//            Elements links = page.select("a[href]"); //带有href属性的a元素
//            Elements counts = page.select("span.gray");
//            if (links.size() - counts.size() == 2){
//                links.remove(0);
//                links.remove(counts.size());
//            }
//            while (counts.size()>0){
//                Element count = counts.remove(0);
//                String countTemp = count.text();
//                Element link = links.remove(0);
//                if (checkCount(countTemp)){
//                    String linkTemp = link.attr("href");
//                    if (linkTemp!=null){
//                        urlSet.add(linkTemp);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//    }
//    private boolean checkCount(String str){
//        boolean result = false;
//        if (str!=null){
////        String m = "(307/17 小螺丝8585)";
//            String regex = "[(/ )]";
//            String regexTo = "  ";
//            String[] ms = str.replaceAll(regex,"  ").split(regexTo);
//            if (ms.length == 4){
//                int totalreply = Integer.parseInt(ms[2]);
//                if (totalreply > replyMin && totalreply < replyMax){
//                    result = true;
//                }
//            }
//        }
//        return result;
//    }
//    private void fetchRealUrl(String url,int replyCount){
//        try {
//            Thread.sleep(sleepTime);
//            Document doc = Jsoup.connect(UrlHeader+url).timeout(timeOut).get();
//            Elements scripts = doc.select("script[postType]"); //带有href属性的a元素
//            if (scripts!=null){
//                String scriptText;
//                String[] urls;
//                for (Element script : scripts) {
//                    scriptText = script.toString();
//                    if (scriptText!=null && scriptText.contains("bbs.tianya.cn")){
//                        urls = scriptText.split(regex);
//                        for (String urlTemp : urls){
//                            if (urlTemp.contains("bbs.tianya.cn")){
////                                System.out.println(urlTemp);
////                                urlAndCountMap.put(urlTemp, replyCount);
//                                urlSet.add(urlTemp);
//                            }
//                        }
//                    }
//                }
//            }
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//    }
//
//    private void insertURLs(String sql){
//        // 驱动程序名
//        String driver = "com.mysql.jdbc.Driver";
//
//        // URL指向要访问的数据库名scutcs
//        String url = "jdbc:mysql://127.0.0.1:3306/TianYaUrl";
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
//            Connection conn = DriverManager.getConnection(url, user, password);
//
//            if(!conn.isClosed())
//                System.out.println("Succeeded connecting to the Database!");
//
//            // statement用来执行SQL语句
//            Statement statement = conn.createStatement();
//
//            // 结果集
//            statement.execute(sql);
//            conn.close();
//        } catch(ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch(SQLException e) {
//            e.printStackTrace();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
