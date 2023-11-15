package com.alexprodan.Service.UserServices;


import com.alexprodan.Exception.IdOrNameNotFoundException;
import com.alexprodan.Persistance.UserPersistance.entity.Role;
import com.alexprodan.Persistance.UserPersistance.entity.User;
import com.alexprodan.Persistance.UserPersistance.entity.UserProfile;
import com.alexprodan.Persistance.UserPersistance.repository.RoleRepository;
import com.alexprodan.Persistance.UserPersistance.repository.UserProfileRepository;
import com.alexprodan.Persistance.UserPersistance.repository.UserRepository;
import com.alexprodan.Registration.SignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserProfileRepository userProfileRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(@Lazy UserRepository userRepository, @Lazy RoleRepository roleRepository, @Lazy UserProfileRepository userProfileRepository, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    public User createUser(SignupRequest request){
        if (checkUsernameAvailable(request.getEmail())) {
            User newUser = new User();
            newUser.setEmail(request.getEmail());
            newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            List<Role> allRoles = getAllRoles();
            List<Role> userRoles;
            if (request.getRole().equalsIgnoreCase("admin")) {
                userRoles = allRoles;
            } else if (request.getRole().equalsIgnoreCase("moderator")) {
                Role userRole = allRoles.stream().filter(role -> role.getName().equalsIgnoreCase("user"))
                        .findFirst().get();
                Role moderatorRole = allRoles.stream().filter(role -> role.getName().equalsIgnoreCase("moderator"))
                        .findFirst().get();
                userRoles = List.of(userRole, moderatorRole);
            } else {
                userRoles = List.of(allRoles.stream().filter(role -> role.getName().equalsIgnoreCase("user"))
                        .findFirst().get());
            }

            newUser.setRoles(userRoles);

            UserProfile newUserProfile = new UserProfile();
            newUserProfile.setUser(newUser);
            newUserProfile.setReviews(new HashMap<>());
            newUserProfile.setWatchList(new ArrayList<>());
            newUserProfile.setCheckIns(new ArrayList<>());

            newUser.setUserProfile(newUserProfile);

            userRepository.saveAndFlush(newUser);
            return newUser;
        }
        log.error("Username taken, try another 1");
        return null;
    }


    private boolean checkUsernameAvailable(String userName) {
        Optional<User> userFound = userRepository.findByEmail(userName);
        if (userFound.isPresent()) {
            log.info("There is already an user created with this username, try another one");
            return false;
        }
        return true;
    }

    private List<Role> getAllRoles(){
        List<Role> roles = roleRepository.findAll();
        if(roles.size() == 0){
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("MODERATOR"));
            roleRepository.save(new Role("USER"));
            return roleRepository.findAll();
        }
        return roles;
    }

    public void deleteUser(Long id) throws IdOrNameNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            userRepository.delete(user);
        }else {
            throw new IdOrNameNotFoundException("User with id " + id + " not found","id");
        }
    }


    public User getLoggedUser() throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggedUser = null;
        if (principal instanceof UserDetails) {
            loggedUser = (UserDetails) principal;
        }

        return userRepository
                .findByEmail(loggedUser.getUsername()).orElseThrow(() -> new Exception("Error getting the logged " +
                        "in user from the session."));
    }

}
