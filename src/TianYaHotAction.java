import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Affe
 * Date: 13-8-23
 * Time: 下午6:16
 * To change this template use File | Settings | File Templates.
 */
public class TianYaHotAction {
    final static String hotActionUrl = "util://bbs.tianya.cn/hotArticle.jsp";
    final static String hotActionPageUrl = "util://bbs.tianya.cn/hotArticle.jsp?pn=";
    int pageNumber = 0;

    final static String funinfoDBname = "funinfo";
    final static String funinfoUrlPre = "util://www.tianya200.com/idx/23/";
    final static String funinfoUrlSuf = "/0.html";

    LinkedList<HashMap> hotArticles;
    public TianYaHotAction() {
        hotArticles = new LinkedList<HashMap>();
    }

    public void startFetch(){
        try {
            Document doc = Jsoup.connect(hotActionUrl).get();
            Element page = doc.select("div.long-pages").first();
            Elements links = page.select("a[href]"); //带有href属性的a元素
            Pattern pattern = Pattern.compile("\\d+");
            System.out.println(links);
            for (Element link : links) {
                String linkHref = link.attr("href");
                String linkText = link.text();
                Matcher matcher = pattern.matcher(linkText);
                if (matcher.find()){
                    int currentPage = Integer.parseInt(linkText);
                    if (currentPage>pageNumber){
                        pageNumber = currentPage;
                    }
                }
                System.out.println(linkHref);

            }
            System.out.println(pageNumber);

//            fetchHotActionByPage(1);
             new FetchAllHotActions().run();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private class FetchAllHotActions implements Runnable{

        @Override
        public void run() {
            for (int i=1;i<=pageNumber;i++){
                fetchHotActionByPage(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            System.out.println(hotArticles);
            //----------------从这里继续下一步--------------
        }
    }
    private void fetchHotActionByPage(int pageNum){
        try {
            Document doc = Jsoup.connect(hotActionPageUrl+pageNum).get();
            Element page = doc.select("div.mt5").first();
            Elements links = page.select("tr"); //带有href属性的a元素
            for (Element link : links) {
                Elements tds = link.select("td"); //带有href属性的a元素
                if (tds.size()==3){
                    System.out.println(tds);
                    HashMap<String,String> tempMap = new HashMap<String, String>();
                    Element tdA = tds.first().select("a[href]").first();
                    tempMap.put("actionId",tdA.attr("href"));
                    tempMap.put("actionTitle",tdA.text());
                    tds.remove(0);
                    Element tdB = tds.first();
                    tempMap.put("authorID",tdB.text());
                    hotArticles.add(tempMap);
                }

            }
//            System.out.println(links);

        } catch (IOException e) {
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
