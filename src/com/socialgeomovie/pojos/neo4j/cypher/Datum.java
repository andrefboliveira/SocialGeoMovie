
package com.socialgeomovie.pojos.neo4j.cypher;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("row")
    @Expose
    private List<Integer> row = null;
    @SerializedName("meta")
    @Expose
    private List<Object> meta = null;

    public List<Integer> getRow() {
        return row;
    }

    public void setRow(List<Integer> row) {
        this.row = row;
    }

    public List<Object> getMeta() {
        return meta;
    }

    public void setMeta(List<Object> meta) {
        this.meta = meta;
    }

}
