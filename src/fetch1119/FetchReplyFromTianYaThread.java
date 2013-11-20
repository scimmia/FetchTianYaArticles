package fetch1119;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Affe
 * Date: 13-9-8
 * Time: 下午12:42
 * To change this template use File | Settings | File Templates.
 */
public class FetchReplyFromTianYaThread implements Runnable,Global {
    Logger logger;

    @Override
    public void run() {
        System.out.println("请输入抓取板块序号: 1 娱乐八卦 2 天涯杂谈 3 时尚资讯 4 国际观察 5 情感天地");
        String board = boards[SavitchIn.readInt()-1];
        MainOneOneOneNine.initLog4j("TianYa-" + board);
        logger = Logger.getLogger(board);
//        MainOneOneOneNine.runtime();
        MysqlUtil mysqlUtil = null;
        try {
            mysqlUtil = new MysqlUtil();
            Connection conn = mysqlUtil.getConn(board);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database：" + board);
//            MainOneOneOneNine.runtime();

            String querySql = "SELECT id FROM initiatePost WHERE title IS NULL ORDER BY viewCount,crawlTime DESC";
            PreparedStatement psUpdate=conn.prepareStatement(querySql);
            ResultSet rs = psUpdate.executeQuery();
            String initiatePostId = null;

            String updateSql = "update initiatePost set initiateTime = ? , title = ? ,clickCount = ? ,viewCount = ? where id = ?";
            PreparedStatement updateInitiateTime =conn.prepareStatement(updateSql);
            String insertSql = "INSERT INTO viewPost (initiatePostId,viewTime,crawlTime) VALUES(?,?,?)";
            PreparedStatement ps=conn.prepareStatement(insertSql);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while(rs.next()) {
                initiatePostId = rs.getString("id");
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
//                        MainOneOneOneNine.runtime();
                        response = HttpClientUtil.getHtmlByUrl("http://bbs.tianya.cn" + urlToFetch);
//                        MainOneOneOneNine.runtime();
                        if (response != null && !response.equalsIgnoreCase("")){
                            boolean needUpdate = (urlToFetch.equalsIgnoreCase(initiatePostId));
                            doc = Jsoup.parse(response);
//                            MainOneOneOneNine.runtime();

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
//                            MainOneOneOneNine.runtime();
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
//                            MainOneOneOneNine.runtime();
                            String insetSql = "INSERT INTO viewPost (initiatePostId,viewTime,crawlTime) VALUES ";
                            if (viewTimsList.size()>0){
                                StringBuilder stringBuilder = new StringBuilder(insetSql);
                                if (needUpdate){
                                    //第一页，需要update
                                    Element elementTmep = doc.select("span.s_title").first();
                                    elementTmep = elementTmep.select("span").first();
                                    String title = elementTmep.text();

                                    elementTmep = doc.select("div.atl-menu").first();
                                    int clickCount = Integer.parseInt(elementTmep.attr("js_clickcount"));
                                    int replycount = Integer.parseInt(elementTmep.attr("js_replycount"));

                                   updateInitiateTime.setString(1,viewTimsList.getFirst());
                                    updateInitiateTime.setString(2,title);
                                    updateInitiateTime.setInt(3,clickCount);
                                    updateInitiateTime.setInt(4,replycount);
                                    updateInitiateTime.setString(5,initiatePostId);
                                    updateInitiateTime.executeUpdate();
                                }
                                String crawlTimeToInsert;
                                while (!viewTimsList.isEmpty()){
                                    crawlTimeToInsert = formatter.format(new java.util.Date());
                                    Timestamp timeStamp = new Timestamp(new java.util.Date().getTime());
                                    ps.setString(1, initiatePostId);
                                    ps.setString(2,viewTimsList.removeFirst());
                                    ps.setString(3,crawlTimeToInsert);
//                                    ps.setTimestamp(3,new Timestamp(new java.util.Date().getTime()));
//                                    ps.executeUpdate();
//                                                                 MainOneOneOneNine.runtime();
                                    ps.addBatch();
                                }
                                ps.executeBatch();
                                conn.commit();
//                                MainOneOneOneNine.runtime();

//                                if (!viewTimsList.isEmpty()){
//                                    stringBuilder.append("('").append(initiatePostId).append("','")
//                                            .append(viewTimsList.removeFirst()).append("','")
//                                            .append(formatter.format(new java.util.Date())).append("')");
//                                }
//                                while (!viewTimsList.isEmpty()){
//                                    stringBuilder.append(",('").append(initiatePostId).append("','")
//                                            .append(viewTimsList.removeFirst()).append("','")
//                                            .append(formatter.format(new java.util.Date())).append("')");
//                                }
////                                MainOneOneOneNine.runtime();
//                                System.out.println("----");
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
        } finally {
            if (mysqlUtil != null) {
                mysqlUtil.close();
            }
        }
    }
}
