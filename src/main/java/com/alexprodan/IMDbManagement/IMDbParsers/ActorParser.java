package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.Persistance.IMDbPersistance.entity.Actor;
import org.antlr.v4.runtime.tree.Tree;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ActorParser extends PersonParser {

    @Override
    public Actor parse(Document document) throws ParseException {
        Actor actor = new Actor(super.parse(document));
        actor.setFilmography(parseFilmography(document));
        return actor;
    }

    private Map<String, String> parseFilmography(Document document) {
        try {
            Map<String, String> filmography = new LinkedHashMap<>();
            Elements creditsElement = document.select("div[data-testid='Filmography']");
            String actorOrActresses = null;
            int actorSectionIndex = 0;
//            String actorOrActresses = document.select(".sc-d11846e2-0").select("li").get(0).text();
            for(Element element:creditsElement.select(".sc-6703147-4")){
                if(element.select(".ipc-title__text").text().equalsIgnoreCase("actor")
                        || element.select(".ipc-title__text").text().equalsIgnoreCase("actress")){
                    actorOrActresses = element.select(".ipc-title__text").text().toLowerCase();
                    actorSectionIndex++;
                    break;
                }
                actorSectionIndex++;
            }
            Elements productionsElements = creditsElement.select(".sc-6703147-3").get(actorSectionIndex-1)
                    .getElementById("accordion-item-" + actorOrActresses + "-previous-projects").getAllElements()
                    .select(".ipc-metadata-list");
            System.out.println(productionsElements);
            for (Element element : productionsElements.select(".ipc-metadata-list-summary-item")) {
                String production = element.select("a").text();
                String role = element.select("ul").get(0).text();
                filmography.put(production, role);
            }

            return filmography;
        }catch (Exception e){
            throw new ParseException("Something went wrong parsing the filmography.\n" + e);
        }

//        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)){
//            webClient.addRequestHeader("Accept-Language", "en-US");
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getOptions().setJavaScriptEnabled(false);
//            webClient.getOptions().setTimeout(150000);
//            webClient.getOptions().setRedirectEnabled(true);
//            HtmlPage htmlPage = webClient.getPage(url);
//            HtmlElement previousList = htmlPage.getHtmlElementById("accordion-item-actress-previous-projects");
//            HtmlButton button = previousList.querySelector("button[data-testid='nm-flmg-paginated-all-actress']");
//            System.out.println(button.asNormalizedText());
//            htmlPage = button.click();
//            HtmlElement updatedList = htmlPage.getHtmlElementById("accordion-item-actress-previous-projects");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

}
