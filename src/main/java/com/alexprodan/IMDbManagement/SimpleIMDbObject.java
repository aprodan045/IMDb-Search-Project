package com.alexprodan.IMDbManagement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SimpleIMDbObject {

    private String name;
    private String year;
    private String runtime;
    private String imdb_rating;
}
