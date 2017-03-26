
package com.socialgeomovie.pojos.neo4j;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNodeProperties {

    @SerializedName("foo")
    @Expose
    private String foo;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

}
