package fetch1120;


import org.apache.log4j.PropertyConfigurator;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class MainOneOneOneNine implements Global {


    public static void main(String[] args) {
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8087");

        System.out.println("请输入抓取类型序号: 1 天涯200抓主贴 2 天涯论坛抓详情 3 初始化数据库 4 删除上次最后一条并继续抓取详情");
        int board = SavitchIn.readInt();

        switch (board){
            case 1:
            {
//                new Thread(new FetchUrlFromTwoZeroZeroThread()).start();
                break;
            }
            case 2:
            {
//                new Thread(new FetchReplyFromTianYaThread()).start();
                break;
            }
            case 3:
                try {
                    new MysqlUtil().initDataBase();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                break;
            case 4:
            {
                //delete last one
                try {
                    MysqlUtil mysqlUtil = new MysqlUtil();
                    System.out.println("请输入抓取板块序号: 1 娱乐八卦 2 天涯杂谈 3 时尚资讯 4 国际观察 5 情感天地");
                    String selectedboard = boards[SavitchIn.readInt()-1];
                    System.out.println("请输入最后一个id");
                    String id = SavitchIn.readLine();
                    mysqlUtil.refetch(selectedboard,id);
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            case 5:
            {
                try {
                    GlobalUtil.urlQ.put("list.jsp?item=free&p=3&n=2013-11-20 14:42:35.947&vu=84924361963");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String item = "free";
                GlobalUtil.initLog4j("555"+item);
                new Thread(new FetchBoardThread(item)).start();
                new Thread(new FetchItemUrlThread(item)).start();
                new Thread(new SavingPostThread(item)).start();
                int m = SavitchIn.readInt();
                if (m==1){
                    GlobalUtil.fetching = false;
                }
                break;
            }
            default:
//                HttpClientUtil.testHttp();
                GlobalUtil.getSelectedItem();
                break;
        }


    }
    static Date oldTIme = new Date();
    public static long runtime(){
        Date newDate = new Date();
        long mm = newDate.getTime() - oldTIme.getTime();
        oldTIme = newDate;
        System.out.println("runed:"+mm);
        return mm;
    }

//        PropertyConfigurator.configure("./bin/log4j.properties");
//        new Thread(new FetchUrlFromTwoZeroZeroThread()).start();
//        Logger logger = Logger.getLogger("asdas");
//        logger.debug("\r\ndebug");
//        logger.info("info");
//        logger.warn("warn");
//        logger.trace("trace");
//        logger.debug("debug");
//        logger.info("info");
//        logger.warn("warn");
//        logger.error("error");

}
