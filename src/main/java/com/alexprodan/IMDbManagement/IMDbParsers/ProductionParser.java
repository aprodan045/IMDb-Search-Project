package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Constants.IMDbUrl;
import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.Persistance.IMDbPersistance.entity.Actor;
import com.alexprodan.Persistance.IMDbPersistance.entity.Production;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public abstract class ProductionParser implements Parser<Production> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionParser.class);

    @Override
    public Production parse(Document document) throws ParseException {
        return Production.builder()
                .imdbId(parseIMDbID(document))
                .name(parseIMDbName(document))
                .poster_url(parsePosterURL(document))
                .trailer_url(parseTrailerURL(document))
                .plot(parsePlot(document))
                .rating(parseRating(document))
                .imdb_rating(parseUserRating(document))
                .genres(parseGenres(document))
                .cast(parseCast(document))
                .build();

    }

    private String parseIMDbID(Document document) {
        Element element = document.select("meta[property=og:url]").get(0);
        String movieUrl = element.attr("content");
        String[] paths = movieUrl.split("/");
        return paths[paths.length - 1];
    }

    private String parseIMDbName(Document document) {
        Elements elements = document.select(".sc-e226b0e3-3");
        return elements.select(".sc-e6498a88-0").select("h1").text();
    }

    private String parsePosterURL(Document document) {
        return document.select("meta[property=og:image]").get(0).attr("content");
    }

    private String parseTrailerURL(Document document) {
        Elements section = document.select(".sc-9178d6fe-1");
        Elements videos = section.select("[cel_widget_id='StaticFeature_Videos']");
//        System.out.println(videos.select("[data-testid='shoveler']").select("[data-testid='shoveler-items-container']"));
        Element trailer = null;
        for (Element element : videos.select("[data-testid='shoveler']").select(".sc-d729715-0")) {
            Element videoElement = element.select("div").first();
            if (videoElement.select("a").first().select(".ipc-lockup-overlay__content").text()
                    .contains("Trailer")) {
                return IMDbUrl.URL + videoElement.select("a").first().attr("href");
            } else {
                        Element subContentElement = element.select(".ipc-slate-card__content").first();
                assert subContentElement != null;
                if (subContentElement.text().contains("Trailer")) {
                    trailer = element;
                    break;
                }
            }
        }
        if (trailer == null) {
            Elements videoSection = document.select("section[data-testid='videos-section']");
            String videosPath = videoSection.select("[data-testid='videos-title']").select("a")
                    .first().attr("href");
            try {
                document = Jsoup.connect(IMDbUrl.URL + videosPath)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .header("Accept-Language", "en-US")
                        .get();
                Elements allVideos = document.select(".search-results");
                for (Element element : allVideos.select("li")) {
                    if (element.select("h2").text().contains("trailer") ||
                            element.select("h2").text().contains("Trailer")) {
                        return IMDbUrl.URL + element.select("a").attr("href");
                    }
                }
            } catch (IOException e) {
                LOG.error("Unable to connect to the videos section" + e.getMessage());
            }
        }
        if (trailer == null) {
            LOG.info("Unable to find a trailer video");
            return "Unable to find a trailer link";
        }
        String trailerPath = "";
        try {
            trailerPath = trailer.select("a").first().attr("href");
        } catch (Exception e) {
            LOG.error("Couldn't extract the trailer path.");
        }
        return IMDbUrl.URL + trailerPath;
    }

    private String parsePlot(Document document) {
        Elements body = document.select(".sc-e226b0e3-6");
        return body
                .select("p[data-testid='plot']")
                .select(".sc-466bb6c-2").text();
    }

    private String parseRating(Document document) {
        Elements lineElements = document.select("div.sc-e226b0e3-3");
        Element list = Objects.requireNonNull(lineElements.first()).getElementsByTag("ul").first();
        assert list != null;
        if (list.getElementsByTag("li").get(0).text().equalsIgnoreCase("tv series") ||
                list.getElementsByTag("li").get(0).text().equalsIgnoreCase("tv mini series")) {
            if (list.getElementsByTag("li").size() <= 2) {
                LOG.info("This production doesn't have the rating present on the imdb page.");
                return "No rating found";
            }
            return list.getElementsByTag("li").get(2).text();
        }
        return list.getElementsByTag("li").get(1).text();
    }

    private double parseUserRating(Document document) {
        Elements topElements = document.select(".sc-e226b0e3-3");
        Elements elements = topElements.select(".sc-3a4309f8-0")
                .select(".ipc-btn__text")
                .select(".sc-bde20123-0")
                .select(".sc-bde20123-1");
        String rating = elements.text();
        return Double.parseDouble(rating);
    }

    private List<String> parseGenres(Document document) {
        List<String> genres = new ArrayList<>();
        Elements body = document.select(".sc-e226b0e3-6");
        Elements genresListElement = body.select("[data-testid='genres']").select(".ipc-chip-list__scroller");
        if (genresListElement.select("a.ipc-chip").size() == 0) {
            LOG.info("No genres listed in the imdb page of the movie you have entered.");
            return null;
        }
        for (Element element : genresListElement.select("a.ipc-chip")) {
            genres.add(element.text());
        }
        return genres;
    }

    private Map<String, String> parseCast(Document document) {
        Map<String, String> cast = new LinkedHashMap<>();
        String castPath = IMDbUrl.URL + document.select(".sc-bfec09a1-0").select("a").first().attr("href");
        try {
            document = Jsoup.connect(castPath)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .header("Accept-Language", "en-US")
                    .get();
        } catch (IOException e) {
            LOG.error("Unable to connect to the cast page of the movie." + e.getMessage());
        }
        Elements castTable = document.select(".cast_list");
        int noOfActors = castTable.select("tr").size() / 2 - 1;
        if (noOfActors > 100) noOfActors = 100;
        int limit = 0;
        String className = "uglySolution";
        try {
            for (Element element : castTable.select("tr").stream().skip(1).toList()) {
                if (limit == noOfActors || element.text().equals("Rest of cast listed alphabetically:")) {
                    break;
                } else if (element.select(".castlist_label").first() != null) {
                    break;
                }
//            try {
//                Document actorDocument = Jsoup.connect(IMDbUrl.URL + element.select("a").get(1).attr("href"))
//                        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
//                        .header("Accept-Language", "en-US")
//                        .get();
//                actor = actorParser.parse(actorDocument);
//                character = element.select(".character").text();
//            }catch (IOException ex){
//                LOG.error("Unable to connect to a actor page" + ex.getMessage());
//            }
                if (className.equalsIgnoreCase(element.className())) {
                    continue;
                } else {
                    className = element.className();
                }
                String actorName = element.select("td").get(1).text();
                String characterPlayed = element.select("td").get(3).text();
                characterPlayed = characterPlayed.replace("/ ... ", "").replace(", ", " ");
                cast.put(actorName, characterPlayed);
                limit++;
            }
        } catch (Exception e) {
            throw new ParseException("Something went wrong parsing the cast.\n" + e.getMessage());
        }
        return cast;
    }

}
