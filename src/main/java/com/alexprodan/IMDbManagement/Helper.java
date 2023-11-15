package com.alexprodan.IMDbManagement;

import com.alexprodan.IMDbManagement.Search.Search;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Helper {

    public static Connection obtainConnection(Search search) {
        return Jsoup.connect(search.getURL())
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .header("Accept-Language", "en-US");
    }
}
