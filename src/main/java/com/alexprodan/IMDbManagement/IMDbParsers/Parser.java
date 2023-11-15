package com.alexprodan.IMDbManagement.IMDbParsers;

import com.alexprodan.IMDbManagement.Exceptions.ParseException;
import org.jsoup.nodes.Document;

public interface Parser<T> {
    public T parse(Document document) throws ParseException;
}
