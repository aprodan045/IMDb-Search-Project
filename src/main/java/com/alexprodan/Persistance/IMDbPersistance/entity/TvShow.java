package com.alexprodan.Persistance.IMDbPersistance.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue(value = "TvShow")
public class TvShow extends Production{

    @ElementCollection
    @CollectionTable(name = "tv_show_creators")
    @Column(name = "creator_name")
    private List<String> creators;

    @Column(name = "period_of_release")
    private String periodOfRelease;

    @Column(name = "no_episodes")
    private Integer numberOfEpisodes;


    public TvShow(Production production){
        super(production.getImdbId(),production.getName(),
                production.getPoster_url(), production.getTrailer_url(),
                production.getPlot(), production.getRating(),
                production.getImdb_rating(),production.getGenres(), production.getCast());
    }

}
