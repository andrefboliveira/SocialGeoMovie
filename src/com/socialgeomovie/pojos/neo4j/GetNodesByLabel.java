
package com.socialgeomovie.pojos.neo4j;

import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNodesByLabel {

    @SerializedName("metadata")
    @Expose
    private Metadata_GetNodesByLabel metadata;
    @SerializedName("data")
    @Expose
    private Map<String, Object> data;
    @SerializedName("paged_traverse")
    @Expose
    private String pagedTraverse;
    @SerializedName("outgoing_relationships")
    @Expose
    private String outgoingRelationships;
    @SerializedName("outgoing_typed_relationships")
    @Expose
    private String outgoingTypedRelationships;
    @SerializedName("create_relationship")
    @Expose
    private String createRelationship;
    @SerializedName("labels")
    @Expose
    private String labels;
    @SerializedName("traverse")
    @Expose
    private String traverse;
    @SerializedName("extensions")
    @Expose
    private Extensions extensions;
    @SerializedName("all_relationships")
    @Expose
    private String allRelationships;
    @SerializedName("all_typed_relationships")
    @Expose
    private String allTypedRelationships;
    @SerializedName("property")
    @Expose
    private String property;
    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("incoming_relationships")
    @Expose
    private String incomingRelationships;
    @SerializedName("properties")
    @Expose
    private String properties;
    @SerializedName("incoming_typed_relationships")
    @Expose
    private String incomingTypedRelationships;

    public Metadata_GetNodesByLabel getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata_GetNodesByLabel metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getPagedTraverse() {
        return pagedTraverse;
    }

    public void setPagedTraverse(String pagedTraverse) {
        this.pagedTraverse = pagedTraverse;
    }

    public String getOutgoingRelationships() {
        return outgoingRelationships;
    }

    public void setOutgoingRelationships(String outgoingRelationships) {
        this.outgoingRelationships = outgoingRelationships;
    }

    public String getOutgoingTypedRelationships() {
        return outgoingTypedRelationships;
    }

    public void setOutgoingTypedRelationships(String outgoingTypedRelationships) {
        this.outgoingTypedRelationships = outgoingTypedRelationships;
    }

    public String getCreateRelationship() {
        return createRelationship;
    }

    public void setCreateRelationship(String createRelationship) {
        this.createRelationship = createRelationship;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getTraverse() {
        return traverse;
    }

    public void setTraverse(String traverse) {
        this.traverse = traverse;
    }

    public Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public String getAllRelationships() {
        return allRelationships;
    }

    public void setAllRelationships(String allRelationships) {
        this.allRelationships = allRelationships;
    }

    public String getAllTypedRelationships() {
        return allTypedRelationships;
    }

    public void setAllTypedRelationships(String allTypedRelationships) {
        this.allTypedRelationships = allTypedRelationships;
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

    public String getIncomingRelationships() {
        return incomingRelationships;
    }

    public void setIncomingRelationships(String incomingRelationships) {
        this.incomingRelationships = incomingRelationships;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getIncomingTypedRelationships() {
        return incomingTypedRelationships;
    }

    public void setIncomingTypedRelationships(String incomingTypedRelationships) {
        this.incomingTypedRelationships = incomingTypedRelationships;
    }

}
