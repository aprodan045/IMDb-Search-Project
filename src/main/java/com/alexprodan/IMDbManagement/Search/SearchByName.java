package com.alexprodan.IMDbManagement.Search;

import com.alexprodan.IMDbManagement.Constants.IMDbUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SearchByName implements Search {
    private static final Logger logger = LoggerFactory.getLogger(SearchByName.class);

    private final String title;
    private final SearchFor searchFor;

    public SearchByName(String title, SearchFor searchFor) {
        this.title = title;
        this.searchFor = searchFor;
    }

    @Override
    public String getURL() {
        return IMDbUrl.URL + getTheRightPath(title);
    }

    private String getTheRightPath(String searchResultUrl) {
        Document searchResultPage = null;
        try {
            searchResultPage = Jsoup.connect(IMDbUrl.URL + "/find/?q=" + URLEncoder.encode(searchResultUrl, StandardCharsets.UTF_8) + "&ref_=nv_sr_sm")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .header("Accept-Language", "en-US")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (searchFor.name().equalsIgnoreCase("person")) {
            Element personsResulted = searchResultPage.select(".sc-17bafbdb-2").first()
                    .select("ul").first();
            Element firstPerson = personsResulted.select("li").first();
            return firstPerson.select("a").attr("href");
        } else if (searchFor.name().equalsIgnoreCase("tv_show")) {
            Element titles = searchResultPage.select(".sc-17bafbdb-2").first().select("ul").first();
            for (Element element : titles.select(".ipc-metadata-list-summary-item")) {
                Element resultInfo = element.select(".ipc-metadata-list-summary-item__c").first();
                if (resultInfo.text().contains("TV Series") || resultInfo.text().contains("TV Mini Series")) {
                    Element p = element.select(".ipc-metadata-list-summary-item__c").select("a").first();
                    return p.attr("href");
                }
            }
            logger.info("Couldn't retrieve a tv show reference from this title.");
            return "";
        } else {
            Element titles = searchResultPage.select(".sc-17bafbdb-2").first().select("ul").first();
            for (Element element : titles.select(".ipc-metadata-list-summary-item")) {
                Element resultInfo = element.select(".ipc-metadata-list-summary-item__c").first();
                if (resultInfo.text().contains("TV Series") || resultInfo.text().contains("TV Mini Series")) {
                   continue;
                }else {
                    Element p = element.select(".ipc-metadata-list-summary-item__c").select("a").first();
                    return p.attr("href");
                }
            }

//            Element titles = searchResultPage.select(".sc-17bafbdb-2").first();
//            Element firstTitle = titles.select("li").first();
//            Element p = firstTitle.select(".ipc-metadata-list-summary-item__c").select("a").first();
//            return p.attr("href");

            logger.info("Something went wrong getting the right result of the search.");
            return "";
        }
    }
}
