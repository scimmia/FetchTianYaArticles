package fetch1119;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Affe
 * Date: 13-9-6
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class FetchUrlFromTwoZeroZeroThread implements Runnable,Global {
    Logger logger;
    @Override
    public void run() {
        System.out.println("请输入抓取板块序号: 1 娱乐八卦 2 天涯杂谈 3 时尚资讯 4 国际观察 5 情感天地");
        int boardIndex = SavitchIn.readInt()-1;
        String dbName = boards[boardIndex];
        String urlName = boardsURL200[boardIndex];
        MainOneOneOneNine.initLog4j("TianYa200-" + dbName);
        logger = Logger.getLogger(dbName);

        System.out.println("请输入开始的页数:");
        int startPage = SavitchIn.readInt();
        int urlCount = 0;
        MysqlUtil mysqlUtil = null;
        try {
            mysqlUtil = new MysqlUtil();
            Connection conn = mysqlUtil.getConn(dbName);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database：" + dbName);
            PreparedStatement psUpdate=conn.prepareStatement(querySql);
            ResultSet rs = psUpdate.executeQuery();
            if (rs.next()){
                urlCount = rs.getInt("COUNT(id)");
            }
            rs.close();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String insertSql = "REPLACE INTO initiatePost (id,type,crawlTime,viewCount) VALUES(?,?,?,?);";
            PreparedStatement ps=conn.prepareStatement(insertSql);
            String crawlTimeToInsert;

            while (urlCount<fetchCount){
                logger.info(String.format("当前page：%d\t帖子总数：%d",startPage,urlCount));
                try {
                    Thread.sleep(sleepTime);
                    String response = HttpClientUtil.getHtmlByUrl(String.format("http://www.tianya200.com/idx/%s/%d/0.html",urlName,startPage));
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
                                if ( (countTotalReplyInt/countTotalLzInt >= 8) && countTotalReplyInt>= 500){
                                    Element sjtTemp = link.select("td.sjt").first(); //带有href属性的a元素
                                    if (sjtTemp!=null){
                                        Element hrefTemp = sjtTemp.select("a[href]").first();
                                        String articleUrlTemp = hrefTemp.attr("href");
                                        if (articleUrlTemp!=null){
                                            Thread.sleep(sleepTime);
                                            String realUrl = fetchRealUrl("http://www.tianya200.com"+articleUrlTemp);
                                            if (realUrl!=null){
                                                logger.info(String.format("抓到url：%s",realUrl));
//                                                System.out.print(String.format("\t%s",realUrl.substring(realUrl.indexOf("-"),realUrl.lastIndexOf("-1"))));
                                                crawlTimeToInsert = formatter.format(new java.util.Date());
                                                ps.setString(1,realUrl.substring(realUrl.indexOf("/post-")));
                                                ps.setString(2,dbName);
                                                ps.setString(3, crawlTimeToInsert);
                                                ps.setInt(4, countTotalReplyInt);
                                                ps.addBatch();
                                                urlCount++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ps.executeBatch();

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
        } finally {
            if (mysqlUtil != null) {
                mysqlUtil.close();
            }
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
