package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Constants.IMDbUrl;
import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.Persistance.IMDbPersistance.entity.Actor;
import com.alexprodan.Persistance.IMDbPersistance.entity.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;


public class MovieParser extends ProductionParser {
    private static final Logger LOG = LoggerFactory.getLogger(MovieParser.class);

    @Override
    public Movie parse(Document document) throws ParseException {
        Movie movie = new Movie(super.parse(document));
        movie.setReleaseDate(parseYear(document));
        movie.setRuntime(parseRuntime(document));
        movie.setDirectors(parseDirectors(document));
        movie.setLanguagesSpoken(parseLanguages(document));
        movie.setWriters(parseWriters(document));
        movie.setFilmingLocations(parseFilmingLocations(document));
        movie.setProductionCompanies(parseProductionCompanies(document));
        return movie;
    }

    private LocalDate parseYear(Document document) {
        Elements elements = document.select("[data-testid='title-details-section']");
        String release = elements.select(".ipc-metadata-list-item__list-content-item").get(0).text();
        String[] releaseSplit = release.split(" ");
        String monthString = releaseSplit[0];
        int dayOfMonth = Integer.parseInt(releaseSplit[1].replace(",", ""));
        int year = Integer.parseInt(releaseSplit[2]);
        int month = 0;
        for (int i = 1; i <= 12; i++) {
            Month month1 = Month.of(i);
            if (month1.name().equalsIgnoreCase(monthString)) {
                month = month1.getValue();
            }
        }
        return LocalDate.of(year, month, dayOfMonth);
    }

    private int parseRuntime(Document document) {
        Elements lineElements = document.select("div.sc-e226b0e3-3");
        Element list = Objects.requireNonNull(lineElements.first()).getElementsByTag("ul").first();
        assert list != null;
        if (list.getElementsByTag("li").size() < 3) {
            LOG.info("The imdb page of the movie you typed does not have a runtime shown.");
            return 0;
        }
        String runtime = list.getElementsByTag("li").get(2).text();
        String[] times = runtime.split(" ");
        if (times.length == 1) {
            return Integer.parseInt(String.valueOf(times[0].charAt(0))) * 60;
        }
        String minutes = times[1].replaceAll("m", "");
        return Integer.parseInt(String.valueOf(times[0].charAt(0))) * 60 + Integer.parseInt(minutes);
    }

    private List<String> parseDirectors(Document document) {
        List<String> directorsList = new ArrayList<>();
        Elements body = document.select(".sc-e226b0e3-6");
        Elements personsList = body.select(".sc-e6498a88-3")
                .select("ul.ipc-metadata-list");
        Elements directors = personsList.select("[data-testid='title-pc-principal-credit']").get(0)
                .select(".ipc-inline-list");
        for (Element director : directors.select("li")) {
            directorsList.add(director.select("a").text());
        }
        return directorsList;
    }

    private List<String> parseWriters(Document document) {
        List<String> writerList = new ArrayList<>();
        Elements body = document.select(".sc-e226b0e3-6");
        Elements personsList = body.select(".sc-e6498a88-3")
                .select("ul.ipc-metadata-list");
        Elements writers = personsList.select("[data-testid='title-pc-principal-credit']").get(1)
                .select(".ipc-inline-list");
        for (Element writer : writers.select("li")) {
            writerList.add(writer.select("a").text());
        }
        return writerList;
    }

    private List<String> parseLanguages(Document document) {
        List<String> languages = new ArrayList<>();
        Elements detailsSection = document.select("section[data-testid='Details']")
                .select("div[data-testid='title-details-section']")
                .select(".ipc-metadata-list");

        Elements languagesList = detailsSection.select("li").select("[data-testid='title-details-languages']")
                .select(".ipc-inline-list");
        for (Element element : languagesList.select("li")) {
            languages.add(element.select("a").text());
        }
        return languages;
    }

    private List<String> parseFilmingLocations(Document document) {
        List<String> locations = new ArrayList<>();
        Elements detailsSection = document.select("section[data-testid='Details']")
                .select("div[data-testid='title-details-section']")
                .select(".ipc-metadata-list");

        String filmingLocationsPath = detailsSection.select("li").select("[data-testid='title-details-filminglocations']").select("a").attr("href");
        Document locationsDocument = null;
        try {
            locationsDocument = Jsoup.connect(IMDbUrl.URL + filmingLocationsPath)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .header("Accept-Language", "en-US")
                    .get();
        } catch (IOException e) {
            LOG.info("Unable to connect to this locations path." + e.getMessage());
        }
        Elements locationsElements = locationsDocument.select(".sc-2cc31198-0").select("[data-testid='sub-section-flmg_locations']");
        for (Element element : locationsElements.select("[data-testid='item-id']")) {
            String location = element.select(".sc-43930a5b-2").select(".ipc-link").text();
            location = location.replaceAll(", ", "-");
            locations.add(location);
        }

        return locations;
    }

    private List<String> parseProductionCompanies(Document document) {
        List<String> companies = new ArrayList<>();
        Elements detailsSection = document.select("section[data-testid='Details']")
                .select("div[data-testid='title-details-section']")
                .select(".ipc-metadata-list");

        Elements companiesElements = detailsSection.select("li").select("[data-testid='title-details-companies']")
                .select(".ipc-metadata-list-item__content-container");

        for (Element element : companiesElements.select("li")) {
            companies.add(element.select("a").text());
        }

        return companies;
    }
}
