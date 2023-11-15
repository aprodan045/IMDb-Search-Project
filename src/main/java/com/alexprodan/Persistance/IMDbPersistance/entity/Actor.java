package com.alexprodan.Persistance.IMDbPersistance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@Entity
@DiscriminatorValue(value = "Actor")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Actor extends Person{

    @ElementCollection
    @CollectionTable(name = "actor_filmography")
    @MapKeyColumn(name = "production_name")
    @Column(name = "character_name")
    private Map<String,String> filmography;

    @Builder
    public Actor(Person person){
        super(person.getImdbId(), person.getName(),person.getPoster_url(), person.getDescription(), person.getBirthDate());
    }
}
