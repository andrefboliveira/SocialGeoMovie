
package com.socialgeomovie.pojos.neo4j.cypher;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatumGraph {

    @SerializedName("row")
    @Expose
    private List<Row> row = null;
    @SerializedName("meta")
    @Expose
    private List<Metum> meta = null;
    @SerializedName("graph")
    @Expose
    private Graph graph;

    public List<Row> getRow() {
        return row;
    }

    public void setRow(List<Row> row) {
        this.row = row;
    }

    public List<Metum> getMeta() {
        return meta;
    }

    public void setMeta(List<Metum> meta) {
        this.meta = meta;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

}
