package com.alexprodan.Persistance.IMDbPersistance.repository;

import com.alexprodan.Persistance.IMDbPersistance.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {
}
