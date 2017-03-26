
package com.socialgeomovie.pojos.neo4j.cypher;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultGraph {

    @SerializedName("columns")
    @Expose
    private List<String> columns = null;
    @SerializedName("data")
    @Expose
    private List<DatumGraph> data = null;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<DatumGraph> getData() {
        return data;
    }

    public void setData(List<DatumGraph> data) {
        this.data = data;
    }

}
