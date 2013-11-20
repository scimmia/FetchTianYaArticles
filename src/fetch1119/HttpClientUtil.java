package fetch1119;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-4
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientUtil {
    static Logger logger = Logger.getLogger("http");

    /**
     * 根据URL获得所有的html信息
     * @param url
     * @return html
     */
    public static String getHtmlByUrl(String url){
        String html = null;
        //创建httpClient对象
        HttpClient httpClient = new DefaultHttpClient();
        //以get方式请求该URL
        HttpGet httpget = new HttpGet(url);
        try {
            //得到responce对象
            HttpResponse responce = httpClient.execute(httpget);
            //返回码
            int resStatu = responce.getStatusLine().getStatusCode();
            //200正常  其他就不对
            if (resStatu== HttpStatus.SC_OK) {
                //获得相应实体
                HttpEntity entity = responce.getEntity();
                if (entity!=null) {
                    //获得html源代码
                    html = EntityUtils.toString(entity);
                }
            }
        } catch (Exception e) {
            logger.error("访问【"+url+"】出现异常!");
            logger.error(e);
//            System.out.println("访问【"+url+"】出现异常!");
//            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return html;
    }

    public static void testHttp(){
        String basicUrl = "http://m.tianya.cn/bbs/art.jsp?item=%s&id=%s&vu=%s&p=%d";

        BasicNameValuePair item = new BasicNameValuePair("item","funinfo");
        BasicNameValuePair id = new BasicNameValuePair("id","4776361");
        BasicNameValuePair vu = new BasicNameValuePair("vu","84853088886");
        String html = null;
        //创建httpClient对象
        HttpClient httpClient = new DefaultHttpClient();
        for (int i = 1;i<20;i++){
        //以get方式请求该URL
            String url = String.format(basicUrl,"funinfo","4776361","84853088886",i);
            HttpGet httpget = new HttpGet(url);
        try {
            //得到responce对象
            HttpResponse responce = httpClient.execute(httpget);
            //返回码
            int resStatu = responce.getStatusLine().getStatusCode();
            //200正常  其他就不对
            if (resStatu== HttpStatus.SC_OK) {
                //获得相应实体
                HttpEntity entity = responce.getEntity();
                if (entity!=null) {
                    //获得html源代码
                    html = EntityUtils.toString(entity);
                    System.out.println(url);
                    System.out.println(html);
                }
            }
        } catch (Exception e) {
            logger.error("访问【"+url+"】出现异常!");
            logger.error(e);
//            System.out.println("访问【"+url+"】出现异常!");
//            e.printStackTrace();
        }
        }
            httpClient.getConnectionManager().shutdown();

    }
}