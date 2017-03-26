
package com.socialgeomovie.pojos.neo4j;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Metadata_GetNodeByID {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("labels")
    @Expose
    private List<Object> labels = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Object> getLabels() {
        return labels;
    }

    public void setLabels(List<Object> labels) {
        this.labels = labels;
    }

}
