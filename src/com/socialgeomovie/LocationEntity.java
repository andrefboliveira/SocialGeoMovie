
package com.socialgeomovie;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationEntity {

    @SerializedName("MatchOffset")
    @Expose
    private String matchOffset;
    @SerializedName("imd_id")
    @Expose
    private String imdId;
    @SerializedName("entity_value")
    @Expose
    private String entityValue;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("MatchContent")
    @Expose
    private String matchContent;
    @SerializedName("movie_name")
    @Expose
    private String movieName;
    @SerializedName("lat")
    @Expose
    private String lat;

    public String getMatchOffset() {
        return matchOffset;
    }

    public void setMatchOffset(String matchOffset) {
        this.matchOffset = matchOffset;
    }

    public String getImdId() {
        return imdId;
    }

    public void setImdId(String imdId) {
        this.imdId = imdId;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getMatchContent() {
        return matchContent;
    }

    public void setMatchContent(String matchContent) {
        this.matchContent = matchContent;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

}
