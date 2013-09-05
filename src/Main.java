import mainboard.MainboardDealDocThread;
import mainboard.MainboardFetchUrlThread;

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
        testMainBoard();
    }

    static void testMainBoard(){

        MainboardFetchUrlThread mainboardFetchUrlThread = new MainboardFetchUrlThread("http://bbs.tianya.cn/list.jsp?item=free&nextid=1375685616000");
        MainboardDealDocThread mainboardDealDocThread = new MainboardDealDocThread("free");
//        mainboardDealDocThread.run();
//        mainboardFetchUrlThread.run();
        new Thread(mainboardFetchUrlThread).start();
        new Thread(mainboardDealDocThread).start();
    }
}
