package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.Persistance.IMDbPersistance.entity.Director;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class DirectorParser extends PersonParser{


    @Override
    public  Director parse(Document document){
        Director director = new Director(super.parse(document));
        director.setCredits(parseCredits(document));
        return director;
    }

    private Set<String> parseCredits(Document document) {
        try {
            Set<String> productionsDirected = new LinkedHashSet<>();
            boolean containsDirectorSection = false;
            int sectionIndex = 0;
            Element filmography = document.select("[data-testid='Filmography']").select(".ipc-page-section").get(1);
            for (Element element:filmography.select(".sc-6703147-4")){
                sectionIndex++;
                if(element.select(".ipc-title__text").text().equalsIgnoreCase("director")){
                    containsDirectorSection = true;
                    break;
                }
            }
            if(containsDirectorSection){
                Element directorSection = filmography.select(".sc-6703147-3").get(sectionIndex-1);
                Element previousProjects = directorSection.getElementById("accordion-item-director-previous-projects");
                Elements projectsList = previousProjects.select("ul");
                for (Element element : projectsList.select(".ipc-metadata-list-summary-item")) {
                    Elements titleClass = element.select(".ipc-metadata-list-summary-item__c");
                    String title = titleClass.select(".ipc-metadata-list-summary-item__t").text();
                    productionsDirected.add(title);
                }
            }else {
                log.error("This director doesn't have director credits listed in his imdb page.");
                log.error("His writer credits have been parsed instead.");
                Element directorSection = filmography.select(".sc-19052e27-3").get(0);
                Element previousProjects = directorSection.getElementById("accordion-item-writer-previous-projects");
                Elements projectsList = previousProjects.select("ul");
                for (Element element : projectsList.select(".ipc-metadata-list-summary-item")) {
                    Elements titleClass = element.select(".ipc-metadata-list-summary-item__c");
                    String title = titleClass.select(".ipc-metadata-list-summary-item__t").text();
                    productionsDirected.add(title);
                }
            }
            return productionsDirected;

        } catch (Exception e) {
            throw new ParseException("Something went wrong parsing the credits of the director.\n" + e);
        }
    }

}
