package com.alexprodan.Persistance.IMDbPersistance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@Entity
@Table(name = "productions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "production_type",
        discriminatorType = DiscriminatorType.STRING)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Production {

    @Id
    @Column(name = "imdb_id")
    private String imdbId;

    private String name;

    @Column(name = "poster_url")
    private String poster_url;

    @Column(name = "trailer_url")
    private String trailer_url;

    @Column(columnDefinition = "text")
    private String plot;

    private String rating;

    @Column(columnDefinition = "double precision")
    private double imdb_rating;

    @ElementCollection
    @CollectionTable(name = "production_genres", joinColumns = @JoinColumn(name = "imdb_id"))
    @Column(name = "genre")
    private List<String> genres;

    @ElementCollection
    @CollectionTable(name = "cast_table")
    @MapKeyColumn(name = "actor_name")
    @Column(name = "character_name")
    private Map<String, String> cast;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Production that = (Production) o;
        return getImdbId() != null && Objects.equals(getImdbId(), that.getImdbId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
