
package com.socialgeomovie.pojos.sparql;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultsMovie {

    @SerializedName("distinct")
    @Expose
    private Boolean distinct;
    @SerializedName("ordered")
    @Expose
    private Boolean ordered;
    @SerializedName("bindings")
    @Expose
    private List<BindingMovie> bindings = null;

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

    public List<BindingMovie> getBindings() {
        return bindings;
    }

    public void setBindings(List<BindingMovie> bindings) {
        this.bindings = bindings;
    }

}
