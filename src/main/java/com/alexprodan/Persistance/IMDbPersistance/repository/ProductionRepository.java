package com.alexprodan.Persistance.IMDbPersistance.repository;

import com.alexprodan.Persistance.IMDbPersistance.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<Production, String> {
}
