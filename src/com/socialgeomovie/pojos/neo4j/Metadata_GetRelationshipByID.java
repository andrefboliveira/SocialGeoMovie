
package com.socialgeomovie.pojos.neo4j;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Metadata_GetRelationshipByID {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
