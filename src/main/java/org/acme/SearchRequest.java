package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SearchRequest{
    public String id;
    public String keyword;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SearchRequest{");
        sb.append("id='").append(id).append('\'');
        sb.append(", keyword='").append(keyword).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
