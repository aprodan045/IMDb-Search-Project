package com.alexprodan.Persistance.IMDbPersistance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@Entity
@DiscriminatorValue(value = "Movie")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Movie extends Production {

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private int runtime;

    @ElementCollection
    @CollectionTable(name = "movie_directors")
    @Column(name = "movie_director")
    private List<String> directors;

    @ElementCollection
    @CollectionTable(name = "movie_writers")
    @Column(name = "movie_writer")
    private List<String> writers;

    @ElementCollection
    @CollectionTable(name = "movie_languages")
    @Column(name = "language")
    private List<String> languagesSpoken;

    @ElementCollection
    @CollectionTable(name = "movie_locations")
    @Column(name = "location")
    private List<String> filmingLocations;

    @ElementCollection
    @CollectionTable(name = "movie_production_companies")
    @Column(name = "company")
    private List<String> productionCompanies;



    public Movie(Production production) {
        super(production.getImdbId(), production.getName(),
                production.getPoster_url(), production.getTrailer_url(),
                production.getPlot(), production.getRating(),
                production.getImdb_rating(), production.getGenres(), production.getCast());
    }
}
