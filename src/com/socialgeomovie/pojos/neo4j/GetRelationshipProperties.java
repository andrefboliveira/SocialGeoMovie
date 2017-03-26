
package com.socialgeomovie.pojos.neo4j;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetRelationshipProperties {

    @SerializedName("prop1")
    @Expose
    private String prop1;
    @SerializedName("prop2")
    @Expose
    private String prop2;

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }

}
