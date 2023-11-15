package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.Persistance.IMDbPersistance.entity.Person;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

@AllArgsConstructor
@Component(value = "personParser")
public abstract class PersonParser implements Parser<Person> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonParser.class);

    @Override
    public Person parse(Document document) throws ParseException {
        return Person.builder()
                .imdbId(parseImdbID(document))
                .name(parseName(document))
                .poster_url(parsePosterUrl(document))
                .description(parseDescription(document))
                .birthDate(parseBirthDate(document))
                .build();
    }

    private String parseImdbID(Document document) {
        String url = document.select("meta[property='og:url']").attr("content");
        String[] urlPaths = url.split("/");
        return urlPaths[urlPaths.length-1];

    }

    private String parseName(Document document) {
        Elements topSection = document.select(".sc-e226b0e3-2");
        return topSection.select(".sc-e226b0e3-3").select(".sc-7c4234bd-0").select(".sc-7f1a92f5-0").text();
    }

    private String parsePosterUrl(Document document) {
        Elements metaImage = document.select("meta[property='og:image']");
        System.out.println(metaImage.html());
        return metaImage.attr("content");
    }

    private String parseDescription(Document document) {
        Elements topSection = document.select(".sc-e226b0e3-2");
        Elements infoClass = topSection.select(".sc-e226b0e3-6");
        Elements description = infoClass.select(".sc-da717241-1");
        return description.select(".ipc-html-content-inner-div").get(0).text();
    }

    private LocalDate parseBirthDate(Document document) {
        Elements bornSection = document.select(".sc-e226b0e3-2")
                .select(".sc-e226b0e3-6")
                .select(".sc-e226b0e3-11");
        if(bornSection.select(".sc-dec7a8b-2").size() <2){
            LOG.info("This person doesn't have a born section on his page.");
            return null;
        }
        String birthDate = bornSection.select(".sc-dec7a8b-2").get(1).text();
        String[] dates = birthDate.split(" ");
        if (dates.length < 3) {
            LOG.info("This person doesn't have the complete birth date shown in the page");
            return null;
        }
        int dayOfMonth = Integer.parseInt(dates[1].replace(",", ""));
        int year = Integer.parseInt(dates[2]);
        String monthString = dates[0];
        int month = 0;
        for (int i = 1; i <= 12; i++) {
            Month month1 = Month.of(i);
            if (month1.name().equalsIgnoreCase(monthString)) {
                month = month1.getValue();
            }
        }
        return LocalDate.of(year, month, dayOfMonth);
    }

}

