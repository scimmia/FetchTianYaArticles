package fetch1120;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-20
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
public class FetchBoardThread implements Runnable,Global {
    Logger logger;
    String item;

    public FetchBoardThread(String item) {
        this.item = item;
    }
    @Override
    public void run() {
        logger = Logger.getLogger("FetchBoardThread");
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        while (GlobalUtil.fetching){
            String url = GlobalUtil.urlQ.poll();
            if (url== null){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                logger.info(url);
                HttpGet httpget = new HttpGet("http://m.tianya.cn/bbs/"+url.replace(" ","%20"));
                try {
                    HttpResponse responce = httpClient.execute(httpget);
                    if (responce.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                        HttpEntity entity = responce.getEntity();
                        if (entity!=null) {
                            GlobalUtil.htmlQ.put(EntityUtils.toString(entity));
                        }
                    }
                } catch (Exception e) {
                    logger.error("访问【"+url+"】出现异常!");
                }
            }

        }
        httpClient.getConnectionManager().shutdown();
    }
}
