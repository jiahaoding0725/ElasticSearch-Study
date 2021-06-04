package com.ding.utils;

import com.ding.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {
    // public static void main(String[] args) throws IOException {
    //     new HtmlParseUtil().parseJD("心理学").forEach(System.out::println);
    // }


    public List<Content> parseJD(String keywords) throws IOException {
        // 获取请求
        // 不能获取到ajax
        String url = "https://search.jd.com/Search?keyword=" + keywords + "&enc=utf-8";

        // 解析网页 Jsoup返回的Document就是浏览器Document对象
        Document document = Jsoup.parse(new URL(url), 30000);
        // 所有在js中可以使用的方法都能使用
        Element element = document.getElementById("J_goodsList");
        // System.out.println(element.html());

        // 获取所欲的li元素
        Elements elements = element.getElementsByTag("li");


        ArrayList<Content> goodsList = new ArrayList<>();
        for (Element el : elements) {
            // 这种图片特别多的网站，所有的图片都是延迟加载的
            //source-data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();


            Content content = new Content(title, img, price);
            goodsList.add(content);
        }
        return goodsList;
    }
}
