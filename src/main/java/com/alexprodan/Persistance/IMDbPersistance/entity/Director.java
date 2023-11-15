package com.alexprodan.Persistance.IMDbPersistance.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@Entity
@DiscriminatorValue(value = "Director")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Director extends Person {

    @ElementCollection
    @CollectionTable(name = "director_credits")
    @Column(name = "movie_name")
    private Set<String> credits = new HashSet<>();

    @Builder
    public Director(Person person) {
        super(person.getImdbId(), person.getName(), person.getPoster_url(), person.getDescription(), person.getBirthDate());
    }

}
