package com.alexprodan.Service.IMDbServices;

import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import com.alexprodan.IMDbManagement.Helper;
import com.alexprodan.IMDbManagement.IMDbParsers.ActorParser;
import com.alexprodan.IMDbManagement.IMDbParsers.DirectorParser;
import com.alexprodan.IMDbManagement.IMDbParsers.PersonParser;
import com.alexprodan.IMDbManagement.Search.*;
import com.alexprodan.Persistance.IMDbPersistance.entity.Actor;
import com.alexprodan.Persistance.IMDbPersistance.entity.Director;
import com.alexprodan.Persistance.IMDbPersistance.entity.Movie;
import com.alexprodan.Persistance.IMDbPersistance.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PersonService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private Search search;
    private PersonParser parser;

    private final PersonRepository personRepository;

    public Actor getActor(SearchType searchBy, String idOrName){
        switch (searchBy){
            case ID -> {
                search = new SearchById(idOrName, SearchFor.PERSON);
                try {
                    Document actorPage = Objects.requireNonNull(Helper.obtainConnection(search)).get();
                    parser = new ActorParser();
                    Actor actor =  (Actor) parser.parse(actorPage);
                    personRepository.saveAndFlush(actor);
                    return actor;
                } catch (ParseException e){
                    LOG.error("Unable to parse the actor page" + e.getMessage());
                } catch (Exception exception){
                    LOG.error("Something went wrong getting the actor.\n" + exception.getMessage());
                }
            }
            case NAME -> {
                search = new SearchByName(idOrName, SearchFor.PERSON);
                try {
                    Document actorPage = Objects.requireNonNull(Helper.obtainConnection(search)).get();
                    parser = new ActorParser();
                    Actor actor =  (Actor) parser.parse(actorPage);
                    personRepository.saveAndFlush(actor);
                    return actor;
                } catch (ParseException e){
                    LOG.error("Unable to parse the actor page. " + e.getMessage());
                } catch (Exception exception){
                    LOG.error("Something went wrong getting the actor.\n" + exception.getMessage());
                }
            }
        }
        return null;
    }

    public Director getDirector(SearchType searchBy, String idOrName){
        switch (searchBy){
            case ID -> {
                search = new SearchById(idOrName, SearchFor.PERSON);
                try {
                    Document directorsPage = Objects.requireNonNull(Helper.obtainConnection(search)).get();
                    parser = new DirectorParser();
                    Director director = (Director) parser.parse(directorsPage);
                    personRepository.saveAndFlush(director);
                    return director;
                } catch (ParseException e){
                    LOG.error("Unable to parse the director page" + e.getMessage());
                } catch (Exception exception){
                    LOG.error("Something went wrong getting the director.\n" + exception.getMessage());
                }
            }
            case NAME -> {
                search = new SearchByName(idOrName, SearchFor.PERSON);
                try {
                    Document directorsPage = Objects.requireNonNull(Helper.obtainConnection(search)).get();
                    parser = new DirectorParser();
                    Director director = (Director) parser.parse(directorsPage);
                    personRepository.saveAndFlush(director);
                    return director;
                } catch (ParseException e){
                    LOG.error("Unable to parse the director page.\n" + e.getMessage());
                } catch (Exception exception){
                    LOG.error("Something went wrong getting the director.\n" + exception.getMessage());
                }
            }
        }
        return null;
    }

}
