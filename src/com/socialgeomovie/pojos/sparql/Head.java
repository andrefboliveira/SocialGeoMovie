
package com.socialgeomovie.pojos.sparql;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Head {

    @SerializedName("link")
    @Expose
    private List<Object> link = null;
    @SerializedName("vars")
    @Expose
    private List<String> vars = null;

    public List<Object> getLink() {
        return link;
    }

    public void setLink(List<Object> link) {
        this.link = link;
    }

    public List<String> getVars() {
        return vars;
    }

    public void setVars(List<String> vars) {
        this.vars = vars;
    }

}
