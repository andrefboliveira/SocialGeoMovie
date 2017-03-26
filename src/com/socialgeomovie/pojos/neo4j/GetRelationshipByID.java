
package com.socialgeomovie.pojos.neo4j;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetRelationshipByID {

    @SerializedName("extensions")
    @Expose
    private Extensions extensions;
    @SerializedName("metadata")
    @Expose
    private Metadata_GetRelationshipByID metadata;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("property")
    @Expose
    private String property;
    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private String properties;
    @SerializedName("data")
    @Expose
    private Data data;

    public Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public Metadata_GetRelationshipByID getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata_GetRelationshipByID metadata) {
        this.metadata = metadata;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
