package mainboard;

import org.jsoup.nodes.Document;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
public class MainboardDocUtil {
    static LinkedList<Document>documentLinkedList = new LinkedList<Document>();
    static void addDocument(Document document){
        if (document!=null){
            synchronized ("documentLinkedList"){
                documentLinkedList.add(document);
            }
        }
    }
    static Document removeDocument(){
        synchronized ("documentLinkedList"){
            if (documentLinkedList.size()>0){
                return documentLinkedList.remove();
            }else {
                return null;
            }
        }
    }

    static String urlToFetch;
    static void setUrlToFetch(String url){
        synchronized ("urlToFetch"){
            urlToFetch = url;
        }
    }
    static String getUrlToFetch(){
        synchronized ("urlToFetch"){
            String url = new String(urlToFetch);
            urlToFetch = null;
            return url;
        }
    }

    public static int postCount;
    static void addPostCount(){
        synchronized ("postCount"){
            postCount++;
        }
    }
    static int getPostCount(){
        synchronized ("postCount"){
            return postCount;
        }
    }
}
