package fetch1120;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-19
 * Time: 上午9:25
 * To change this template use File | Settings | File Templates.
 */
public interface Global {
    int fetchCount = 10000;
    String logFolderPatch = "e://logs/";
    /*
        娱乐八卦 funinfo    23
        天涯杂谈 free   10
        时尚资讯 no11   18
        国际观察 worldlook  21
        情感天地 feeling 17
         */
    String datebaseHeader = "tianyatestootz";
    String[] boards = {"yule","zatan","shishang","guoji","qinggan"};
    String[] boardsURL200 = {"23","10","18","21","17"};
    int sleepTime = 1000;
    // 驱动程序名
    String driver = "com.mysql.jdbc.Driver";
    String user = "root";
    String password = "1234";

    String preDatabaseUrl = "jdbc:mysql://127.0.0.1:3306/";
    String endDatabaseUrl = "?useUnicode=true&characterEncoding=UTF-8";

    String querySql = "SELECT COUNT(id) FROM initiatepost";
    String refetchUpdate = "UPDATE initiatepost SET title = NULL WHERE id = ?";
    String refetchDelete = "DELETE FROM viewpost WHERE initiatePostId = ?";
/*
   SELECT * from initiatepost WHERE id = '/post-funinfo-4585260-1.shtml'
    SELECT * from viewpost WHERE initiatePostId = '/post-funinfo-4585260-1.shtml'
*/

    //m.tianya.cn
    String basicBoardUrl = "http://m.tianya.cn/bbs/art.jsp?item=%s&vu=%s&p=%d";
    String basicBoardNextUrl = "http://m.tianya.cn/bbs/art.jsp?item=%s&vu=%s&p=%d&n=%s";
    String basicUrl = "http://m.tianya.cn/bbs/art.jsp?item=%s&id=%s&vu=%s&p=%d";



    String[] items = {"funinfo","free","no11","worldlook","feeling"};
    String[] itemsName = {"娱乐八卦","天涯杂谈","时尚资讯","国际观察","情感天地"};

    int minReplyCount = 500;
    int minReplyProportion = 8;

    int saveEveryCount = 200;
    int saveReplyEveryCount = 2000;
    String vu = "84853088886";
}
