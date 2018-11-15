package com.aagproservices.jenkins.bitbucketsteps.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PullRequest implements Serializable {

    private static final long serialVersionUID = 424686714325347246L;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("fromRef")
    private String fromRef;

    @JsonProperty("toRef")
    private String toRef;

    @JsonIgnore
    private Map<String, Object> unmappedFields = new HashMap<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrom() {
        return fromRef;
    }

    public void setFrom(String from) {
        this.fromRef = from;
    }

    public String getTo() {
        return toRef;
    }

    public void setTo(String to) {
        this.toRef = to;
    }

    public Map<String, Object> getUnmappedFields() {
        return unmappedFields;
    }

    @JsonAnySetter
    public void setUnmappedFields(String name, Object value) {
        this.unmappedFields.put(name, value);
    }

    @Override
    public String toString() {
        return "Tag {" +
            "title='" + title + "'" +
            ", description='" + description + "'" +
            ", fromRef='" + fromRef + "'" +
            ", toRef='" + toRef + "'" +
            ", unmappedFields=" + unmappedFields +
            "}";
    }
}
