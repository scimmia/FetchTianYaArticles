package reply;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-5
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class ReplyUtil {
    static LinkedList<HashMap> replyDocumentLinkedList = new LinkedList<HashMap>();
    static void addDocument(HashMap document){
        if (document!=null){
            synchronized ("replyDocumentLinkedList"){
                replyDocumentLinkedList.add(document);
            }
        }
    }
    static HashMap removeDocument(){
        synchronized ("replyDocumentLinkedList"){
            if (replyDocumentLinkedList.size()>0){
                return replyDocumentLinkedList.remove();
            }else {
                return null;
            }
        }
    }

    static String replyUrlToFetch;
    static void setUrlToFetch(String url){
        synchronized ("replyUrlToFetch"){
            replyUrlToFetch = url;
        }
    }
    static String getUrlToFetch(){
        synchronized ("replyUrlToFetch"){
            String url = new String(replyUrlToFetch);
            replyUrlToFetch = null;
            return url;
        }
    }


}
