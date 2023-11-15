package com.alexprodan.Controler;

import com.alexprodan.Exception.IdOrNameNotFoundException;
import com.alexprodan.IMDbManagement.Search.SearchType;
import com.alexprodan.Persistance.IMDbPersistance.entity.Movie;
import com.alexprodan.Persistance.IMDbPersistance.entity.TvShow;
import com.alexprodan.Persistance.UserPersistance.entity.User;
import com.alexprodan.Persistance.UserPersistance.entity.UserProfile;
import com.alexprodan.Registration.LoginRequest;
import com.alexprodan.Registration.SignupRequest;
import com.alexprodan.Service.IMDbServices.ProductionService;
import com.alexprodan.Service.UserServices.UserProfileService;
import com.alexprodan.Service.UserServices.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.Validation;
import java.util.Map;


@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final DaoAuthenticationProvider authenticationProvider;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ProductionService productionService;

    private final UserProfileService userProfileService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));

        User user = (User) authenticationManager.authenticate(authentication).getPrincipal();

        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> createNewUser(@RequestBody @Valid SignupRequest registrationRequest) {
        try {
            User user = userService.createUser(registrationRequest);
            return new ResponseEntity<>(user, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>("Some inputs are not valid", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (IdOrNameNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add-movie")
    public ResponseEntity<Object> addMovie(@RequestParam Map<String, String> params) {
        User user = userService.loadUserByUsername(params.get("user"));
        String ID = "id";
        String NAME = "name";
        switch (params.get("list")) {
            case "watchlist" -> {
                try {
                    Movie movie;
                    if (params.containsKey(NAME)) {
                        movie = productionService.getMovie(SearchType.NAME, params.get(NAME));
                        userProfileService.addIntoWatchlist(movie, user);
                        System.out.println(user.getUserProfile().getWatchList());
                        return new ResponseEntity<>("Successfully added movie into watchlist", HttpStatus.OK);
                    } else if (params.containsKey(ID)) {
                        movie = productionService.getMovie(SearchType.ID, params.get(ID));
                        userProfileService.addIntoWatchlist(movie, user);
                        return new ResponseEntity<>("Successfully added movie into watchlist", HttpStatus.OK);
                    }
                } catch (Exception e) {
                    log.error("No user logged in");
                    log.error(e.getMessage());
                }
            }

            case "checkins" -> {
                try {
                    Movie movie;
                    if (params.containsKey(NAME)) {
                        movie = productionService.getMovie(SearchType.NAME, params.get(NAME));
                        System.out.println(user.getUserProfile().getCheckIns().size());
                        userProfileService.addIntoCheckIns(movie, user);
                        return new ResponseEntity<>("Successfully added movie into checkins", HttpStatus.OK);

                    } else if (params.containsKey(ID)) {
                        movie = productionService.getMovie(SearchType.ID, params.get(ID));
                        userProfileService.addIntoCheckIns(movie, user);
                        return new ResponseEntity<>("Successfully added movie into checkins", HttpStatus.OK);

                    }

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            default -> {
                return new ResponseEntity<>("No list with this value.", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/add-tv-show")
    public ResponseEntity<Object> addTvShow(@RequestParam Map<String, String> params) {
        User user = userService.loadUserByUsername(params.get("user"));
        String ID = "id";
        String NAME = "name";
        switch (params.get("list")) {
            case "watchlist" -> {
                try {
                    TvShow tvShow;
                    if (params.containsKey(NAME)) {
                        tvShow = productionService.getTvShow(SearchType.NAME, params.get(NAME));
                        userProfileService.addIntoWatchlist(tvShow, user);
                        user.getUserProfile().getWatchList().forEach(System.out::println);
                        return new ResponseEntity<>("Successfully added tv show: " + tvShow.getName() + " into watchlist", HttpStatus.OK);
                    } else if (params.containsKey(ID)) {
                        tvShow = productionService.getTvShow(SearchType.ID, params.get(ID));
                        userProfileService.addIntoWatchlist(tvShow, user);
                        return new ResponseEntity<>("Successfully added tv show: " + tvShow.getName() + " into watchlist", HttpStatus.OK);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            case "checkins" -> {
                try {
                    TvShow tvShow;
                    if (params.containsKey(NAME)) {
                        tvShow = productionService.getTvShow(SearchType.NAME, params.get(NAME));
                        System.out.println(user.getUserProfile().getCheckIns().size());
                        userProfileService.addIntoCheckIns(tvShow, user);
                        return new ResponseEntity<>("Successfully added movie into checkins", HttpStatus.OK);

                    } else if (params.containsKey(ID)) {
                        tvShow = productionService.getTvShow(SearchType.ID, params.get(ID));
                        System.out.println(user.getUserProfile().getCheckIns().size());
                        userProfileService.addIntoCheckIns(tvShow, user);
                        return new ResponseEntity<>("Successfully added movie into checkins", HttpStatus.OK);

                    }

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            default -> {
                return new ResponseEntity<>("No list with this value.", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/rate")
    public ResponseEntity<Object> rateProduction(@RequestParam Map<String, String> params) {
        User user = userService.loadUserByUsername(params.get("user"));
        if (params.containsKey("movie")) {
            Movie movie = productionService.getMovie(SearchType.ID, params.get("movie"));
            double rate;
            if (!params.containsKey("rating")) {
                return new ResponseEntity<>("No rating present in the params.", HttpStatus.BAD_REQUEST);
            } else {
                rate = Double.parseDouble(params.get("rating"));
            }
            if (productionService.containsProduction(movie)) {
                userProfileService.rateProduction(movie, rate, user);
            } else {
                productionService.save(movie);
                userProfileService.rateProduction(movie, rate, user);
            }
            return new ResponseEntity<>("Successfully added a rating for movie: " + movie.getName() + ", with the rating " + rate, HttpStatus.OK);
        } else if (params.containsKey("tv-show")) {
            TvShow tvShow = productionService.getTvShow(SearchType.ID, params.get("tv-show"));
            double rate;
            if (!params.containsKey("rating")) {
                return new ResponseEntity<>("No rating present in the params.", HttpStatus.BAD_REQUEST);
            } else {
                rate = Double.parseDouble(params.get("rating"));
            }
            if (productionService.containsProduction(tvShow)) {
                userProfileService.rateProduction(tvShow, rate, user);
            } else {
                productionService.save(tvShow);
                userProfileService.rateProduction(tvShow, rate, user);
            }
            return new ResponseEntity<>("Successfully added a rating for tv-show: " + tvShow.getName() + ", with the rating " + rate, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("U can't rate this kind of production, try specifying a movie or a tv-show in the params", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-list")
    public ResponseEntity<Object> getUserProfileList(@RequestParam Map<String, String> params) {
        User user = userService.loadUserByUsername(params.get("user"));
        switch (params.get("list")) {
            case "watchlist" -> {
                return new ResponseEntity<>(user.getUserProfile().getWatchList(),HttpStatus.OK);
            }
            case "checkins" -> {
                return new ResponseEntity<>(user.getUserProfile().getCheckIns(),HttpStatus.OK);
            }
            case "reviews" -> {
                return new ResponseEntity<>(user.getUserProfile().getReviews(),HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>("No list with this name on this user profile", HttpStatus.BAD_REQUEST);
            }
        }
    }

}
