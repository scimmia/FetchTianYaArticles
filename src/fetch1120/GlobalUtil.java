package fetch1120;

import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-20
 * Time: 下午2:22
 * To change this template use File | Settings | File Templates.
 */
public class GlobalUtil implements Global {
    private GlobalUtil() {
    }
    public static String getSelectedItem(){
        System.out.print("\n请输入抓取板块序号:");
        for (int i =0;i<itemsName.length;i++){
            System.out.printf("  %d. %s",i,itemsName[i]);
        }
        int selected = SavitchIn.readInt();
        if (selected<0||selected>=itemsName.length){
            System.out.print("\n请输入正确的抓取板块序号:");
            return getSelectedItem();
        }
        return items[selected];
    }
    static void initLog4j(String boardName){
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO, ServerDailyRollingFile, stdout");
        prop.setProperty("log4j.appender.ServerDailyRollingFile", "org.apache.log4j.DailyRollingFileAppender");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.DatePattern", "'.'yyyy-MM-dd");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.File", logFolderPatch+boardName+".log");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.layout", "org.apache.log4j.PatternLayout");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.layout.ConversionPattern", "%d [%c]-[%p] %m%n");
        prop.setProperty("log4j.appender.ServerDailyRollingFile.Append", "true");

        prop.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        prop.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        prop.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %p [%c] %m%n");

        PropertyConfigurator.configure(prop);
    }
    public static boolean fetching = true;
    public static LinkedBlockingQueue<String> urlQ = new LinkedBlockingQueue<String>();
    public static LinkedBlockingQueue<String> htmlQ = new LinkedBlockingQueue<String>();

    public static LinkedBlockingQueue<PostStruct> postQ = new LinkedBlockingQueue<PostStruct>();
}
