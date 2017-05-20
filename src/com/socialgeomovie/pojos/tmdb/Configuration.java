
package com.socialgeomovie.pojos.tmdb;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Configuration {

    @SerializedName("images")
    @Expose
    private Images_Config images;
    @SerializedName("change_keys")
    @Expose
    private List<String> changeKeys = null;

    public Images_Config getImages() {
        return images;
    }

    public void setImages(Images_Config images) {
        this.images = images;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

}
