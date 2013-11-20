package fetch1120;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-20
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class FetchItemUrlThread implements Runnable,Global {
    Logger logger;
    String item;

    public FetchItemUrlThread(String item) {
        this.item = item;
    }

    @Override
    public void run() {
//        GlobalUtil.initLog4j("FetchItemUrlThread"+item);
        logger = Logger.getLogger("FetchItemUrlThread");
        String htmlContent;
        while (GlobalUtil.fetching || !GlobalUtil.htmlQ.isEmpty()){
            htmlContent = GlobalUtil.htmlQ.poll();
            if (htmlContent==null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Document doc = Jsoup.parse(htmlContent);
                if (fetchNextPageUrl(doc)==1){
                    fetchPost(doc);
                }
            }
        }
        logger.error(GlobalUtil.urlQ.poll());
    }

    public int fetchNextPageUrl(Document doc){
        int result = 0;
        Elements lks = doc.select("div.lk");
        boolean gotUrl = false;
        for (Element lk:lks){
            Elements hrefs = lk.select("a[href]"); //带有href属性的a元素
            for (Element href:hrefs){
                if (href.text().equals("下一页")){
                    try {
                        GlobalUtil.urlQ.put(href.attr("href"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gotUrl = true;
                    result = 1;
                    break;
                }
            }
            if (gotUrl){
                break;
            }
        }
        return result;
    }

    public int fetchPost(Document doc){
        int result = 0;
        Element p = doc.select("div.p").first();
        Elements ps = p.select("p");
        for (Element pTemp:ps){
            Element count = pTemp.select("span.gray").first();
            String countStr = count.text().substring(1, count.text().indexOf(' '));
            int position = countStr.indexOf('/');
            int replyCount = Integer.parseInt(countStr.substring(position+1));
            if (replyCount < minReplyCount){
                continue;
            }else{
                int clickCount = Integer.parseInt(countStr.substring(0,position));
                if (clickCount/replyCount < minReplyProportion){
                    continue;
                }else {
                    Element href = pTemp.select("a[href]").first();
                    String title = href.text();
                    String url = href.attr("href");
                    String id;
                    for (String mm :url.split("&")){
                        if (mm.contains("id=")){
                            id = mm.substring(3);
                            deal(id,title,replyCount,clickCount);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    public void deal(String id,String title,int replyCount,int clickCount){
        logger.info(id+'\t'+title+'\t'+replyCount+'\t'+clickCount);
        try {
            GlobalUtil.postQ.put(new PostStruct(id,title,replyCount,clickCount));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
