package com.alexprodan.IMDbManagement.Search;

import com.alexprodan.IMDbManagement.Constants.IMDbUrl;

public class SearchById implements Search{

    private final String id;
    private final SearchFor searchFor;

    public SearchById(String id, SearchFor searchFor) {
        this.id = id;
        this.searchFor = searchFor;
    }

    @Override
    public String getURL() {
        if(searchFor.name().equalsIgnoreCase("movie") || searchFor.name().equalsIgnoreCase("tv_show")){
            return IMDbUrl.URL + "/title/" + id + "/?ref_=nm_knf_t_1";
        }else
            return IMDbUrl.URL + "/name/" + id + "/?ref_=fn_al_nm_1";

    }
}
