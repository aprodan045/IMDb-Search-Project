package com.alexprodan.Controler;

import com.alexprodan.IMDbManagement.Search.SearchType;
import com.alexprodan.Persistance.IMDbPersistance.entity.Actor;
import com.alexprodan.Persistance.IMDbPersistance.entity.Director;
import com.alexprodan.Service.IMDbServices.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class PersonsController {

    private final PersonService personService;

    @GetMapping("/actor")
    public ResponseEntity<Object> getActor(@RequestParam Map<String, String> params){
        String ID = "id";
        String NAME = "name";
        if(params.containsKey(ID)){
            Actor actor = personService.getActor(SearchType.ID,params.get(ID));
            if(actor!=null){
                return new ResponseEntity<>(actor, HttpStatus.OK);
            }
            return new ResponseEntity<>("Not able to find an actor by this id: " + params.get(ID),HttpStatus.NOT_FOUND);
        }
        else if(params.containsKey(NAME)){
            Actor actor = personService.getActor(SearchType.NAME,params.get(NAME));
            if(actor!=null){
                return new ResponseEntity<>(actor, HttpStatus.OK);
            }
            return new ResponseEntity<>("Not able to find an actor by this name: " + params.get(NAME),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Not able to find an actor with these parameters, try searching by name or imdb id.",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/director")
    public ResponseEntity<Object> getDirector(@RequestParam Map<String, String> params){
        String ID = "id";
        String NAME = "name";
        if(params.containsKey(ID)){
            Director director = personService.getDirector(SearchType.ID,params.get(ID));
            if(director!=null){
                return new ResponseEntity<>(director, HttpStatus.OK);
            }else
                return new ResponseEntity<>("Not able to find a director by this id: " + params.get(ID),HttpStatus.NOT_FOUND);
        }
        else if(params.containsKey(NAME)){
            Director director = personService.getDirector(SearchType.NAME,params.get(NAME));
            if(director!=null){
                return new ResponseEntity<>(director, HttpStatus.OK);
            }
            return new ResponseEntity<>("Not able to find a director by this name: " + params.get(NAME),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Not able to find an director with these parameters, try searching by name or imdb id.",HttpStatus.BAD_REQUEST);
    }

}
