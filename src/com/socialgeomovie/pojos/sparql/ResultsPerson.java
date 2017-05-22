
package com.socialgeomovie.pojos.sparql;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultsPerson {

    @SerializedName("distinct")
    @Expose
    private Boolean distinct;
    @SerializedName("ordered")
    @Expose
    private Boolean ordered;
    @SerializedName("bindings")
    @Expose
    private List<BindingPerson> bindings = null;

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }

    public List<BindingPerson> getBindings() {
        return bindings;
    }

    public void setBindings(List<BindingPerson> bindings) {
        this.bindings = bindings;
    }

}
