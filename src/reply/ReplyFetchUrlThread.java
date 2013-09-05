package reply;

import http.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class ReplyFetchUrlThread implements Runnable{
    String urlToFetch;
    String initiatePostId;
    public ReplyFetchUrlThread(String urlToFetch) {
        this.urlToFetch = urlToFetch;
        initiatePostId = new String(urlToFetch);
    }

    long runTime = 3600000;
    int postCountMax = 6000;
    int sleepTime = 2000;

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long currenttime = startTime;
//        String urlTemp;

        while ((this.urlToFetch != null && !this.urlToFetch.isEmpty()) && currenttime - startTime < runTime){
            try {
                System.out.println(String.format("时间\t%tR\t\t%s\t\t",System.currentTimeMillis(),this.urlToFetch));
                String response = HttpClientUtil.getHtmlByUrl("http://bbs.tianya.cn"+this.urlToFetch);
                if (response != null && !response.equalsIgnoreCase("")){
                    Document doc = Jsoup.parse(response);

                    Element pages = doc.select("div.atl-pages").first();
                    Element nextPage = pages.select("a[href]").last();
                    String nextUrlToFetch = nextPage.attr("href");
                    if (!nextPage.text().equalsIgnoreCase("下页")){
                        Element nextPageSpan = pages.select("span").last();
                        if (nextPageSpan.text().equalsIgnoreCase("下页")){
                            this.urlToFetch = null;
                        }else{
                            System.out.println("刷新"+urlToFetch);
                        }
                    }else {
                        this.urlToFetch = nextUrlToFetch;
                        HashMap<String,Object> docs = new HashMap<String, Object>();
                        docs.put("initiatePostId",initiatePostId);
                        docs.put("doc",doc);
                        ReplyUtil.addDocument(docs);
                    }
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
