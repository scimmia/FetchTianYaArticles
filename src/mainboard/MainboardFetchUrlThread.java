package mainboard;

import http.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午1:35
 * To change this template use File | Settings | File Templates.
 */
public class MainboardFetchUrlThread implements Runnable{
    String postType;

    public MainboardFetchUrlThread(String postType) {
        this.postType = postType;
    }

    long runTime = 3600000;
    int postCountMax = 6000;
    int sleepTime = 20;

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long currenttime = startTime;

        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.MONTH, false);
        long urlStartTime = calendar.getTimeInMillis();

        String urlToFetch = "http://bbs.tianya.cn/list.jsp?item="+postType+"&nextid="+urlStartTime;

        while (MainboardDocUtil.getPostCount() < postCountMax && currenttime - startTime < runTime){
            try {
                System.out.println(String.format("时间\t%tR\t\t%s\t\t%d", System.currentTimeMillis(), urlToFetch, MainboardDocUtil.getPostCount()));
                String response = HttpClientUtil.getHtmlByUrl(urlToFetch);
                if (response != null && !response.equalsIgnoreCase("")){
                    Document doc = Jsoup.parse(response);

                    Element pages = doc.select("div.links").first();
                    Element nextPage = pages.select("a[href]").last();
                    String nextUrlToFetch = nextPage.attr("href");
                    if (!nextPage.text().equalsIgnoreCase("下一页")){
                        System.out.println("刷新" + urlToFetch);
                    }else {
                        urlToFetch = "http://bbs.tianya.cn"+nextUrlToFetch;
                        MainboardDocUtil.addDocument(doc);
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
