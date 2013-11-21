package fetch1120;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-21
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class FetchReplyThread implements Runnable,Global {
    Logger logger;
    Connection connection;
    String item;
    HttpClient httpClient;
    public FetchReplyThread(String item) {
        this.item = item;
        connection = null;
        try {
            connection = new MysqlUtil().getConn(item);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger = Logger.getLogger("FetchReplyThread");
    }



    @Override
    public void run() {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        try {
            String querySql = "SELECT id,viewCount FROM initiatePost WHERE initiateTime IS NULL ORDER BY viewCount,crawlTime DESC";
            PreparedStatement psUpdate=connection.prepareStatement(querySql);
            ResultSet rs = psUpdate.executeQuery();
            String deleteSql = "delete from initiatePost where id = ?";
            PreparedStatement deleteWrongId =connection.prepareStatement(deleteSql);
            String updateSql = "update initiatePost set initiateTime = ? where id = ?";
            PreparedStatement updateInitiateTime =connection.prepareStatement(updateSql);
            String insertSql = "replace INTO viewPost (initiatePostId,viewTime,crawlTime) VALUES(?,?,?)";
            PreparedStatement ps=connection.prepareStatement(insertSql);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()){
                String id = rs.getString("id");
                int viewCount = rs.getInt("viewCount");
                int pages = viewCount/100 + 1;
                boolean deleteID = false;
                for (int i = 1;i<=pages;i++){
                    String html = gettHtml(id,i);
                    if (html==null){
                        //tode delete id
                        deleteID = true;

                        break;
                    }else {
                        Document doc = Jsoup.parse(html);
                        String htmlTitle = doc.title();
                        if (htmlTitle.isEmpty() || htmlTitle.contains("出错了")){
                            //tode delete id
                            deleteID = true;
                            deleteWrongId.setString(1, id);
                            deleteWrongId.addBatch();
                            break;
                        }else {
                            Elements replies = doc.select("div.atl-head-reply");
                            for (Element replyTemp : replies) {
                                Elements hrefs = replyTemp.select("a[href]"); //带有href属性的a元素
                                String replyTime = null;
                                String floor = null;
                                try {
                                    replyTime = hrefs.first().attr("replytime");
                                    floor = hrefs.last().attr("floor");
                                    if (replyTime!=null && floor !=null){
                                        //todo

                                    }
                                }catch (NullPointerException e){

                                }
//                                if (hrefs!=null){
//
//                                    String replyTime = null;
//                                    String floor = null;
//                                    for (Element element : hrefs){
//                                        if (element.attr("replytime")!=null){
//                                            replyTime = element.attr("replytime");
//                                        }
//                                        if (element.attr("floor")!=null){
//                                            floor = element.attr("floor");
//                                        }
//                                        if (replyTime!=null && floor !=null){
//
//                                        }
//                                    }
//                                }
                            }
                        }
                    }
                }
                if (deleteID){
                    deleteWrongId.setString(1, id);
                    deleteWrongId.addBatch();
                }else {
                    //todo update
                    updateInitiateTime.setString(1, id);
                    updateInitiateTime.setString(2, id);
                    updateInitiateTime.addBatch();
                }
                connection.commit();
            }
            rs.close();
        } catch(SQLException e) {
            logger.error(e);
        } catch(Exception e) {
            logger.error(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }
    public String gettHtml(String id,int page){
        //http://bbs.tianya.cn/post-funinfo-4775591-1.shtml
        String basicUrl = "http://bbs.tianya.cn/bbs/post-%s-%s-%d.shtml";
        String html = null;
        String url = String.format(basicUrl,item,id,page);
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse responce = httpClient.execute(httpget);
            int resStatu = responce.getStatusLine().getStatusCode();
            if (resStatu== HttpStatus.SC_OK) {
                HttpEntity entity = responce.getEntity();
                if (entity!=null) {
                    html = EntityUtils.toString(entity);
                }
            }
        } catch (Exception e) {
            logger.error("访问【"+url+"】出现异常!");
            logger.error(e);
        }
        return html;
    }
}