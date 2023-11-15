package com.alexprodan.Persistance.UserPersistance.repository;

import com.alexprodan.Persistance.UserPersistance.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
