public class Main {
    final static String funinfoDBname = "funinfo";
    final static String funinfoUrlPre = "http://www.tianya200.com/idx/23/";
    final static String funinfoUrlSuf = "/0.html";
    public static void main(String[] args) {
        FetchArticlesUrl fetchArticlesUrl = new FetchArticlesUrl(funinfoDBname,funinfoUrlPre,funinfoUrlSuf,66);
        fetchArticlesUrl.run();

    }                            // write your code here

}
