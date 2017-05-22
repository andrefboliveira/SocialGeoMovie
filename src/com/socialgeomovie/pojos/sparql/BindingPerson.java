
package com.socialgeomovie.pojos.sparql;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BindingPerson {

    @SerializedName("personResource")
    @Expose
    private PersonResource personResource;
    @SerializedName("wikipediaPage")
    @Expose
    private WikipediaPage wikipediaPage;
    @SerializedName("summary")
    @Expose
    private Summary summary;

    public PersonResource getPersonResource() {
        return personResource;
    }

    public void setPersonResource(PersonResource personResource) {
        this.personResource = personResource;
    }

    public WikipediaPage getWikipediaPage() {
        return wikipediaPage;
    }

    public void setWikipediaPage(WikipediaPage wikipediaPage) {
        this.wikipediaPage = wikipediaPage;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

}
