
package com.socialgeomovie.pojos.tmdb;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images_Person {

    @SerializedName("profiles")
    @Expose
    private List<Profile> profiles = null;

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

}
