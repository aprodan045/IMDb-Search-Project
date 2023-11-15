package com.alexprodan.Controler;

import com.alexprodan.IMDbManagement.Search.SearchFor;
import com.alexprodan.IMDbManagement.Search.SearchType;
import com.alexprodan.IMDbManagement.SimpleIMDbObject;
import com.alexprodan.Persistance.IMDbPersistance.entity.Movie;
import com.alexprodan.Persistance.IMDbPersistance.entity.TvShow;
import com.alexprodan.Service.IMDbServices.ProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductionController {

    private final ProductionService productionService;

    @GetMapping("/movie")
    public ResponseEntity<Object> getMovie(@RequestParam Map<String, String> params) {
        String ID = "id";
        String NAME = "name";
        if(params.containsKey(ID)){
            Movie movie = productionService.getMovie(SearchType.ID,params.get(ID));
            if(movie!=null){
                productionService.save(movie);
                return new ResponseEntity<>(movie, HttpStatus.OK);
            }else
                return new ResponseEntity<>("Not able to find the movie by this id: " + params.get(ID),HttpStatus.NOT_FOUND);
        }
        else if(params.containsKey(NAME)){
            Movie movie = productionService.getMovie(SearchType.NAME,params.get(NAME));
            if(movie!=null){
                return new ResponseEntity<>(movie, HttpStatus.OK);
            }
            return new ResponseEntity<>("Not able to find the movie by this name: " + params.get(NAME),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Not able to find a movie with these parameters, try searching by name or imdb id.",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/tv-show")
    public ResponseEntity<Object> getTvShow(@RequestParam Map<String, String> params) {
        String ID = "id";
        String NAME = "name";
        if(params.containsKey(ID)){
            TvShow tvShow = productionService.getTvShow(SearchType.ID,params.get(ID));
            if(tvShow!=null){
                productionService.save(tvShow);
                return new ResponseEntity<>(tvShow, HttpStatus.OK);
            }else
                return new ResponseEntity<>("Not able to find this tv show by this id: " + params.get(ID),HttpStatus.NOT_FOUND);
        }
        else if(params.containsKey(NAME)){
            TvShow tvShow = productionService.getTvShow(SearchType.NAME,params.get(NAME));
            if(tvShow!=null){
                return new ResponseEntity<>(tvShow, HttpStatus.OK);
            }
            return new ResponseEntity<>("Not able to find this tv show by this name: " + params.get(NAME),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Not able to find a movie with these parameters, try searching by name or imdb id.",HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/top-250")
    public ResponseEntity<Object> getTop250(@RequestParam Map<String, String> params){
        if (params.get("top").equalsIgnoreCase("movie")) {
            Set<SimpleIMDbObject> top250 = productionService.getTop250(SearchFor.MOVIE);
            return new ResponseEntity<>(top250, HttpStatus.OK);
        }
        else if (params.get("top").equalsIgnoreCase("tv show")){
            Set<SimpleIMDbObject> top250 = productionService.getTop250(SearchFor.TV_SHOW);
            return new ResponseEntity<>(top250,HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/most-popular")
    public ResponseEntity<Object> getMostPopular(@RequestParam Map<String, String> params){
        if (params.get("most popular").equalsIgnoreCase("movie")) {
            Set<SimpleIMDbObject> mostPopular = productionService.getMostPopular(SearchFor.MOVIE);
            return new ResponseEntity<>(mostPopular, HttpStatus.OK);
        }
        else if (params.get("most popular").equalsIgnoreCase("tv show")){
            Set<SimpleIMDbObject> mostPopular = productionService.getMostPopular(SearchFor.TV_SHOW);
            return new ResponseEntity<>(mostPopular,HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
