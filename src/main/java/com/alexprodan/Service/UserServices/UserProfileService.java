package com.alexprodan.Service.UserServices;

import com.alexprodan.Persistance.IMDbPersistance.entity.Production;
import com.alexprodan.Persistance.UserPersistance.entity.User;
import com.alexprodan.Persistance.UserPersistance.entity.UserProfile;
import com.alexprodan.Persistance.UserPersistance.repository.UserProfileRepository;
import com.alexprodan.Persistance.UserPersistance.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final UserService userService;


    // metoda pt adauga un film/serial in watchlist
    public void addIntoWatchlist(Production production, User user) throws Exception {
        List<Production> currentWatchlist = user.getUserProfile().getWatchList();
        currentWatchlist.add(production);
        userProfileRepository.save(user.getUserProfile());
    }

    public void addIntoCheckIns(Production production, User user) throws Exception {
        UserProfile userProfile = user.getUserProfile();
        List<Production> currentCheckIns = userProfile.getCheckIns();
        currentCheckIns.add(production);
        userProfileRepository.save(userProfile);
    }

    public void rateProduction(Production production, double rate, User user) {
        User currentUser = userService.loadUserByUsername(user.getUsername());
        UserProfile userProfile = currentUser.getUserProfile();
        Map<Production, Double> reviews = userProfile.getReviews();
        reviews.put(production,rate);
        userProfileRepository.save(userProfile);
    }


}
