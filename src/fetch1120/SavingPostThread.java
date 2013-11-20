package fetch1120;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-20
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */
public class SavingPostThread implements Runnable,Global {
    Logger logger;
    Connection connection;
    String item;
    public SavingPostThread(String item) {
        this.item = item;
        connection = null;
        try {
            connection = new MysqlUtil().getConn(item);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        logger = Logger.getLogger("FetchBoardThread");
        String insertSql = "INSERT INTO initiatePost (id,title,crawlTime,type,clickCount,viewCount) VALUES(?,?,?,?,?,?)";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PreparedStatement ps = null;
        try {
            ps =connection.prepareStatement(insertSql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        int i = 0;
        PostStruct postStruct;
        try {
            while (GlobalUtil.fetching || !GlobalUtil.postQ.isEmpty()){
                postStruct = GlobalUtil.postQ.poll();
                if (postStruct == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    if (connection!=null && ps!=null){
                        i++;
                        ps.setString(1,postStruct.getId());
                        ps.setString(2,postStruct.getTitle());
                        ps.setString(3, formatter.format(new java.util.Date()));
                        ps.setString(4, item);
                        ps.setInt(5,postStruct.getClickCount());
                        ps.setInt(6,postStruct.getReplyCount());
                        ps.execute();
                        if (i==saveEveryCount){
                            connection.commit();
                            i = 0;
                        }


                    }
                }
            }
            if (i>0){
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
