
package com.socialgeomovie.pojos.neo4j.cypher;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("row")
    @Expose
    private List<Object> row = null;
    @SerializedName("meta")
    @Expose
    private List<Object> meta = null;

    public List<Object> getRow() {
        return row;
    }

    public void setRow(List<Object> row) {
        this.row = row;
    }

    public List<Object> getMeta() {
        return meta;
    }

    public void setMeta(List<Object> meta) {
        this.meta = meta;
    }

}
