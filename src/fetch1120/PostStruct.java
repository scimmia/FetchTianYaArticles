package fetch1120;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-20
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class PostStruct {
    String id;
    String title;
    int replyCount;
    int clickCount;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public int getClickCount() {
        return clickCount;
    }

    public PostStruct(String id, String title, int replyCount, int clickCount) {

        this.id = id;
        this.title = title;
        this.replyCount = replyCount;
        this.clickCount = clickCount;
    }

    @Override
    public String toString() {
        return "PostStruct{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", replyCount=" + replyCount +
                ", clickCount=" + clickCount +
                '}';
    }
}
