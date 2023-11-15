package com.alexprodan.Service.IMDbServices;

import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.IMDbManagement.Helper;
import com.alexprodan.IMDbManagement.IMDbParsers.MovieParser;
import com.alexprodan.IMDbManagement.IMDbParsers.ProductionParser;
import com.alexprodan.IMDbManagement.IMDbParsers.TVShowParser;
import com.alexprodan.IMDbManagement.Search.*;
import com.alexprodan.IMDbManagement.SimpleIMDbObject;
import com.alexprodan.Persistance.IMDbPersistance.entity.Movie;
import com.alexprodan.Persistance.IMDbPersistance.entity.Production;
import com.alexprodan.Persistance.IMDbPersistance.entity.TvShow;
import com.alexprodan.Persistance.IMDbPersistance.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductionService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductionService.class);

    private Search search;
    private ProductionParser parser;

    private final ProductionRepository productionRepository;


    public Movie getMovie(SearchType searchFor, String idOrName) {
        switch (searchFor) {
            case ID -> {
                search = new SearchById(idOrName, SearchFor.MOVIE);
                try {
                    Document moviePage = Helper.obtainConnection(search).get();
                    parser = new MovieParser();
                    System.out.println("Movie Url:- " + search.getURL());
                    Movie movie = (Movie) parser.parse(moviePage);
//                    saveMovie(movie);
                    return movie;
                } catch (ParseException e) {
                    LOG.error("Unable to parse the movie page" + e.getMessage());
                } catch (Exception exception) {
                    LOG.info("Something went wrong.");
                    LOG.error(exception.getMessage());
                }
            }
            case NAME -> {
                search = new SearchByName(idOrName, SearchFor.MOVIE);
                try {
                    Document moviePage = Helper.obtainConnection(search).get();
                    parser = new MovieParser();
                    System.out.println("Movie Url:- " + search.getURL());
                    Movie movie = (Movie) parser.parse(moviePage);
//                    saveMovie(movie);
                    return movie;
                } catch (ParseException e) {
                    LOG.info("Unable to parse the movie page.");
                    LOG.error(e.getMessage());
                } catch (Exception exception) {
                    LOG.info("Something went wrong.");
                    LOG.error(exception.getMessage());
                }
            }
        }
        return null;
    }

    public TvShow getTvShow(SearchType searchBy, String idOrName) {
        switch (searchBy) {
            case NAME -> {
                search = new SearchByName(idOrName, SearchFor.TV_SHOW);
                try {
                    Document tvShowPage = Objects.requireNonNull(Helper.obtainConnection(search)).get();
                    parser = new TVShowParser();
                    TvShow tvShow = (TvShow) parser.parse(tvShowPage);
//                    save(tvShow);
                    return tvShow;
                } catch (ParseException e) {
                    LOG.info("Unable to parse the tv show page.");
                    LOG.error(e.getMessage());
                } catch (Exception exception) {
                    LOG.info("Something went wrong.");
                    LOG.error(exception.getMessage());
                }
            }
            case ID -> {
                search = new SearchById(idOrName, SearchFor.TV_SHOW);
                try {
                    Document tvShowPage = Objects.requireNonNull(Helper.obtainConnection(search)).get();
                    parser = new TVShowParser();
                    TvShow tvShow = (TvShow) parser.parse(tvShowPage);
//                    save(tvShow);
                    return tvShow;
                } catch (ParseException e) {
                    LOG.info("Unable to parse the tv show page.");
                    LOG.error(e.getMessage());
                } catch (Exception exception) {
                    LOG.info("Something went wrong.");
                    LOG.info(exception.getMessage());
                    LOG.info(exception.getLocalizedMessage());
                }
            }
        }
        return null;
    }

    public Set<SimpleIMDbObject> getTop250(SearchFor searchBy) {
        Set<SimpleIMDbObject> top250;
        switch (searchBy) {
            case MOVIE -> {
                top250 = new LinkedHashSet<>();
                try {
                    Document document = Jsoup.connect("https://www.imdb.com/chart/top/?ref_=nv_mv_250")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .header("Accept-Language", "en-US")
                            .get();
                    SimpleIMDbObject simpleIMDbObject;
                    for (Element element : document.select(".sc-9d2f6de0-0").select("li")) {
                        simpleIMDbObject = new SimpleIMDbObject();
                        simpleIMDbObject.setName(getSimpleName(element));
                        simpleIMDbObject.setYear(getSimpleYear(element));
                        simpleIMDbObject.setRuntime(getSimpleRuntime(element));
                        simpleIMDbObject.setImdb_rating(getSimpleRating(element));
                        top250.add(simpleIMDbObject);
                    }

                    return top250;
                } catch (IOException e) {
                    LOG.error("Couldn't connect to the top 250 movie page.");
                    LOG.error(e.getMessage());
                }
            }
            case TV_SHOW -> {
                top250 = new LinkedHashSet<>();
                try {
                    Document document = Jsoup.connect("https://www.imdb.com/chart/toptv/?ref_=nv_tvv_250")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .header("Accept-Language", "en-US")
                            .get();
                    SimpleIMDbObject simpleIMDbObject;
                    for (Element element : document.select(".sc-9d2f6de0-0").select("li")) {
                        simpleIMDbObject = new SimpleIMDbObject();
                        simpleIMDbObject.setName(getSimpleName(element));
                        simpleIMDbObject.setYear(getSimpleYear(element));
                        simpleIMDbObject.setRuntime(getSimpleRuntime(element));
                        simpleIMDbObject.setImdb_rating(getSimpleRating(element));
                        top250.add(simpleIMDbObject);
                    }

                    return top250;
                } catch (IOException e) {
                    LOG.error("Couldn't connect to the top 250 movie page.");
                    LOG.error(e.getMessage());
                }
            }

            default -> {
                LOG.error("Can't get top 250 for this");
            }
        }
        return new LinkedHashSet<>();
    }

    public Set<SimpleIMDbObject> getMostPopular(SearchFor searchBy) {
        Set<SimpleIMDbObject> mostPopular;
        switch (searchBy) {
            case MOVIE -> {
                mostPopular = new LinkedHashSet<>();
                try {
                    Document document = Jsoup.connect("https://www.imdb.com/chart/moviemeter/?ref_=nv_mv_mpm")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .header("Accept-Language", "en-US")
                            .get();
                    SimpleIMDbObject simpleIMDbObject;
                    for (Element element : document.select(".sc-9d2f6de0-0").select("li")) {
                        simpleIMDbObject = new SimpleIMDbObject();
                        simpleIMDbObject.setName(getSimpleName(element));
                        simpleIMDbObject.setYear(getSimpleYear(element));
                        simpleIMDbObject.setRuntime(getSimpleRuntime(element));
                        simpleIMDbObject.setImdb_rating(getSimpleRating(element));
                        mostPopular.add(simpleIMDbObject);
                    }

                    return mostPopular;
                } catch (IOException e) {
                    LOG.error("Couldn't connect to the top 250 movie page.");
                    LOG.error(e.getMessage());
                }
            }
            case TV_SHOW -> {
                mostPopular = new LinkedHashSet<>();
                try {
                    Document document = Jsoup.connect("https://www.imdb.com/chart/tvmeter/?ref_=nv_tvv_mptv")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .header("Accept-Language", "en-US")
                            .get();
                    SimpleIMDbObject simpleIMDbObject;
                    for (Element element : document.select(".sc-9d2f6de0-0").select("li")) {
                        simpleIMDbObject = new SimpleIMDbObject();
                        simpleIMDbObject.setName(getSimpleName(element));
                        simpleIMDbObject.setYear(getSimpleYear(element));
                        simpleIMDbObject.setRuntime(getSimpleRuntime(element));
                        simpleIMDbObject.setImdb_rating(getSimpleRating(element));
                        mostPopular.add(simpleIMDbObject);
                    }

                    return mostPopular;
                } catch (IOException e) {
                    LOG.error("Couldn't connect to the top 250 movie page.");
                    LOG.error(e.getMessage());
                }
            }

            default -> {
                LOG.error("Can't get top 250 for this");
            }
        }
        return new LinkedHashSet<>();
    }

    public void save(Production production) {
        productionRepository.saveAndFlush(production);
    }

    public boolean containsProduction(Production production) {
        return productionRepository.findAll().contains(production);
    }


    private String getSimpleName(Element listItemElement) {
        return listItemElement.select(".sc-c7e5f54-9").select("a").first().text();
    }

    private String getSimpleYear(Element listItemElement) {
        int year = 0;
        Elements titleMetadata = listItemElement.select(".sc-c7e5f54-7");
        return titleMetadata.select(".sc-c7e5f54-8").get(0).text();
    }

    private String getSimpleRuntime(Element listItemElement) {
        Elements titleMetadata = listItemElement.select(".sc-c7e5f54-7");
        if(titleMetadata.select(".sc-c7e5f54-8").size()<2){
            return "";
        }
        return titleMetadata.select(".sc-c7e5f54-8").get(1).text();
    }

    private String getSimpleRating(Element listItemElement) {

        Elements elements = listItemElement.select(".sc-c7e5f54-1");
        if(elements.text().isEmpty()){
            return "";
        }
        Element rating = elements.select("span").get(0);
        String a = rating.select(".sc-e3e7b191-0").text();
        String[] aa = a.split(" ");
        return aa[0];
    }
}
