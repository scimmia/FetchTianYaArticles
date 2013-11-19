package fetch1119;

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
    String[] boards = {"yule","zatan","shishang","guoji","qinggan"};
    String[] boardsURL200 = {"23","10","18","21","17"};
    int sleepTime = 1000;
    // 驱动程序名
    String driver = "com.mysql.jdbc.Driver";
    String user = "root";
    String password = "1234";

    String datebaseHeader = "tianyatest";
    String preDatabaseUrl = "jdbc:mysql://127.0.0.1:3306/";
    String endDatabaseUrl = "?useUnicode=true&characterEncoding=UTF-8";

    String querySql = "SELECT COUNT(id) FROM initiatepost";
    String refetchUpdate = "UPDATE initiatepost SET title = NULL WHERE id = ?";
    String refetchDelete = "DELETE FROM viewpost WHERE initiatePostId = ?";

}
