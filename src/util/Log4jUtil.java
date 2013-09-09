package util;

import newfetch.FetchReplyFromTianYaThread;
import newfetch.FetchUrlFromTwoZeroZeroThread;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;
import java.util.Scanner;

public class Log4jUtil {
    public static void initLog4j(String boardName){
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO, ServerDailyRollingFile, stdout");
        prop.setProperty("log4j.appender.ServerDailyRollingFile", "org.apache.log4j.DailyRollingFileAppender");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.DatePattern", "'.'yyyy-MM-dd");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.File", "c://logs/"+boardName+".log");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.layout", "org.apache.log4j.PatternLayout");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.layout.ConversionPattern", "%d [%c]-[%p] %m%n");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.Append", "true");

        prop.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        prop.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        prop.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %p [%c] %m%n");

        PropertyConfigurator.configure(prop);
    }
//    public static void main(String[] args) {
//        Scanner stdin = new Scanner(System.in);
//        System.out.println("请输入抓取类型序号: 1 天涯200抓主贴 2 天涯论坛抓详情");
//        int board = stdin.nextInt();
//        switch (board){
//            case 1:
//            {
//                new Thread(new FetchUrlFromTwoZeroZeroThread()).start();
//                break;
//            }
//            case 2:
//            {
//                new Thread(new FetchReplyFromTianYaThread()).start();
//                break;
//            }
//        }
//
////        PropertyConfigurator.configure("./bin/log4j.properties");
////        new Thread(new FetchUrlFromTwoZeroZeroThread()).start();
////        Logger logger = Logger.getLogger("asdas");
////        logger.debug("\r\ndebug");
////        logger.info("info");
////        logger.warn("warn");
////        logger.trace("trace");
////        logger.debug("debug");
////        logger.info("info");
////        logger.warn("warn");
////        logger.error("error");
//    }
}
