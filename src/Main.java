import mainboard.MainboardDealDocThread;
import mainboard.MainboardFetchUrlThread;
import reply.ReplyDealDocThread;
import reply.ReplyFetchUrlThread;
import replynew.FetchUrlThread;

public class Main {
    final static String funinfoDBname = "funinfo";
    final static String funinfoUrlPre = "util://www.tianya200.com/idx/23/";
    final static String funinfoUrlSuf = "/0.html";
    public static void main(String[] args) {
//        System.out.println(util.HttpClientUtil.getHtmlByUrl("util://3g.tianya.cn/bbs/list.jsp?item=funinfo&p=190"));

//        FetchArticlesUrl fetchArticlesUrl = new FetchArticlesUrl(funinfoDBname,funinfoUrlPre,funinfoUrlSuf,73);
//        fetchArticlesUrl.run();


//        TianYaMainBorad fetchMainBoradUrl = new TianYaMainBorad("free","util://bbs.tianya.cn/list.jsp?item=free&nextid=1378347396000");
//        fetchMainBoradUrl.run();

//        testMainBoard();
//        testReply();

        new FetchUrlThread().start();
    }

    static void testMainBoard(){
        /*
        娱乐八卦 free
        天涯杂谈 funinfo
        时尚资讯 no11
        国际观察 worldlook
        情感天地 feeling
         */
        String dbName = "free";
        new Thread(new MainboardFetchUrlThread(dbName)).start();
        new Thread(new MainboardDealDocThread(dbName)).start();

//        MainboardFetchUrlThread mainboardFetchUrlThread = new MainboardFetchUrlThread(dbName);
//        MainboardDealDocThread mainboardDealDocThread = new MainboardDealDocThread(dbName);
//        mainboardDealDocThread.run();
//        mainboardFetchUrlThread.run();
//        new Thread(mainboardFetchUrlThread).start();
//        new Thread(mainboardDealDocThread).start();
    }

    static void testReply(){
        /*
        娱乐八卦 free
        天涯杂谈 funinfo
        时尚资讯 no11
        国际观察 worldlook
        情感天地 feeling
         */
        String dbName = "free";
//        ReplyFetchUrlThread mainboardFetchUrlThread = new ReplyFetchUrlThread(dbName);
//        ReplyDealDocThread mainboardDealDocThread = new ReplyDealDocThread(dbName);
//        mainboardDealDocThread.run();
//        mainboardFetchUrlThread.run();
        new Thread(new ReplyFetchUrlThread(dbName)).start();
        new Thread(new ReplyDealDocThread(dbName)).start();
    }
}
