package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Constants.IMDbUrl;
import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.Persistance.IMDbPersistance.entity.TvShow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TVShowParser extends ProductionParser {
    private static final Logger LOG = LoggerFactory.getLogger(TVShowParser.class);

    @Override
    public TvShow parse(Document document) throws ParseException {
        TvShow tvShow = new TvShow(super.parse(document));
        tvShow.setCreators(parseCreators(document));
        tvShow.setPeriodOfRelease(parsePeriod(document));
        tvShow.setNumberOfEpisodes(parseNumberOfEpisodes(document));
        return tvShow;
    }

    private List<String> parseCreators(Document document) {
        List<String> creators = new ArrayList<>();
        Elements body = document.select(".sc-e226b0e3-6");
        Elements personsList = body.select(".sc-e6498a88-3")
                .select("ul.ipc-metadata-list");

        //personsList.select("[data-testid='title-pc-principal-credit']").get(0).select(".ipc-inline-list");
        Element creatorsElement = null;
        for(Element element:personsList.select("[data-testid='title-pc-principal-credit']")){
            if(element.select("a").first().text().equalsIgnoreCase("creators") ||
                    element.select("a").first().text().equalsIgnoreCase("creator")){
                creatorsElement = element;
                break;
            }
        }
        if(creatorsElement == null){
            LOG.error("This tv show doesn't have creators listed in the imdb page");
            return new ArrayList<>();
        }

        for (Element element : creatorsElement.select("ul").get(0).select("li")) {
            creators.add(element.select("a").text());
        }
        return creators;
    }

    private String parsePeriod(Document document)   {
        Elements lineElements = document.select("div.sc-e226b0e3-3");
        Element list = lineElements.select("div").first().selectFirst("ul");
        String period = list.getElementsByTag("li").get(1).text();
        String[] periodYears = period.split("–");
        if (periodYears.length == 1) {
            return periodYears[0] + "-" + "present";
        }
        return period;
    }

    private Integer parseNumberOfEpisodes(Document document) {
        try {
            Elements episodeGuide = document.select("section[data-testid='Episodes']");
            Elements episodesHeader = episodeGuide.select("[data-testid='episodes-header']");
//            System.out.println(episodesHeader);
            return Integer.parseInt(episodesHeader.select(".ipc-title__subtext").text());

        }catch (Exception e){
            LOG.info("Something went wrong getting the number of episodes");
            LOG.error(e.getMessage());
        }
        return 0;
    }
}
