import mainboard.MainboardDealDocThread;
import mainboard.MainboardFetchUrlThread;
import reply.ReplyDealDocThread;
import reply.ReplyFetchUrlThread;

public class Main {
    final static String funinfoDBname = "funinfo";
    final static String funinfoUrlPre = "http://www.tianya200.com/idx/23/";
    final static String funinfoUrlSuf = "/0.html";
    public static void main(String[] args) {
//        System.out.println(http.HttpClientUtil.getHtmlByUrl("http://3g.tianya.cn/bbs/list.jsp?item=funinfo&p=190"));

//        FetchArticlesUrl fetchArticlesUrl = new FetchArticlesUrl(funinfoDBname,funinfoUrlPre,funinfoUrlSuf,73);
//        fetchArticlesUrl.run();


//        TianYaMainBorad fetchMainBoradUrl = new TianYaMainBorad("free","http://bbs.tianya.cn/list.jsp?item=free&nextid=1378347396000");
//        fetchMainBoradUrl.run();

//        testMainBoard();
        testReply();
    }

    static void testMainBoard(){
        /*
        娱乐八卦 free
        天涯杂谈 funinfo
        时尚资讯 no11
        国际观察 worldlook
        情感天地 feeling
         */
        String dbName = "worldlook";
        MainboardFetchUrlThread mainboardFetchUrlThread = new MainboardFetchUrlThread("http://bbs.tianya.cn/list.jsp?item="+dbName+"&nextid=1375685616000");
        MainboardDealDocThread mainboardDealDocThread = new MainboardDealDocThread(dbName);
//        mainboardDealDocThread.run();
//        mainboardFetchUrlThread.run();
        new Thread(mainboardFetchUrlThread).start();
        new Thread(mainboardDealDocThread).start();
    }

    static void testReply(){

        ReplyFetchUrlThread mainboardFetchUrlThread = new ReplyFetchUrlThread("/post-free-1000264-1.shtml");
        ReplyDealDocThread mainboardDealDocThread = new ReplyDealDocThread("mainboard");
//        mainboardDealDocThread.run();
//        mainboardFetchUrlThread.run();
        new Thread(mainboardFetchUrlThread).start();
        new Thread(mainboardDealDocThread).start();
    }
}
