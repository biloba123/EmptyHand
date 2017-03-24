package com.lvqingyang.emptyhand.Book;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LvQingYang
 * on 2016/9/17.
 */
public class ParseBook {
    private static final String BOOK_LIST_URL= "http://book.meiriyiwen.com/book?page=";
    private static final String TAG = "ParseBook";
    //private static final String BOOK_CAHPTER_URL = "http://book.meiriyiwen.com/book/chapter_list/?bid=";
    private static  int pageCount=1;

    public static void setPage(){
        pageCount=1;
    }

    //获取页面数
    public static int getPageNum() throws IOException {
        Document document = Jsoup.connect(BOOK_LIST_URL+1).get();
        return document.select("div.page_num").select("a[href]").size()-1;
    }

    //获取某页面所有书籍信息
    public static List<BookInfo> getOnePageBooks() throws IOException {
        List<BookInfo> bookInfoList=new ArrayList<>();
        Document document = Jsoup.connect(BOOK_LIST_URL+pageCount++).get();
        Log.d(TAG, "getOnePageBooks: "+document);

        Elements books=document.select("ul.book-list").first().select("li");
        for (Element book : books) {
            BookInfo bookInfo=new BookInfo();
            bookInfo.setName(book.select("div.book-name").text());
            bookInfo.setAuthor(book.select("div.book-author").text());
            bookInfo.setImgUrl(book.select("img").attr("abs:src"));
            bookInfo.setLinkUrl(book.select("a").attr("abs:href"));
            bookInfoList.add(bookInfo);
        }

        return bookInfoList;
    }

    //获取某本书目录
    public static List<Chapter> getBookChapters(String bookUrl) throws IOException {
        List<Chapter> chapterList=new ArrayList<>();
        Document document = Jsoup.connect(bookUrl).get();
        Log.d(TAG, "getBookChapters: "+document);

        Elements chaptersEle=document.select("ul.chapter-list").select("a");
        for (Element chapterEle : chaptersEle) {
            Chapter chapter=new Chapter();
            chapter.setName(chapterEle.text());
            chapter.setLinkUrl(chapterEle.attr("abs:href"));
            chapterList.add(chapter);
        }

        return chapterList;
    }

    public static Part getParts(String chapterUrl) throws IOException {
        Part part =new Part();

        Document document = Jsoup.connect(chapterUrl).get();
        Log.d(TAG, "getParts: "+document);

        part.setTitle(document.select("div.list-header").text());

        String content="";
        Elements paragraphEles=document.select("div.chapter-bg").select("p");
        for (int i = 0; i < paragraphEles.size(); i++) {
            Element element=paragraphEles.get(i);
            content+="        "+element.text()+"\n\n";
        }
        part.setContent(content);

        return part;
    }

    public static String getIntro(String chapterUrl) throws IOException {
        Document document = Jsoup.connect(chapterUrl).get();

        Elements paragraphEles=document.select("div.chapter-bg").select("p");
        for (int i = 0; i < paragraphEles.size(); i++) {
            Element element=paragraphEles.get(i);
            String content=element.text();
            if (content.length()>50) {
                return content;
            }
        }
        return "空";
    }

}
